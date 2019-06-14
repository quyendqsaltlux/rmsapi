package saltlux.ctv.tranSS.service;

import lombok.extern.slf4j.Slf4j;
import org.modelmapper.ModelMapper;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.Resource;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import saltlux.ctv.tranSS.concurrency.RemoveFileTask;
import saltlux.ctv.tranSS.exception.ResourceNotFoundException;
import saltlux.ctv.tranSS.model.*;
import saltlux.ctv.tranSS.payload.employee.Employee;
import saltlux.ctv.tranSS.payload.invoice.InvoiceAuditResponse;
import saltlux.ctv.tranSS.payload.invoice.InvoiceRequest;
import saltlux.ctv.tranSS.payload.invoice.InvoiceScanRequest;
import saltlux.ctv.tranSS.payload.po.POProject;
import saltlux.ctv.tranSS.payload.po.POResponse;
import saltlux.ctv.tranSS.payload.po.PoProjectAssignmentResponse;
import saltlux.ctv.tranSS.payload.resource.CandidateBasicResponse;
import saltlux.ctv.tranSS.repository.candidate.CandidateRepository;
import saltlux.ctv.tranSS.repository.invoice.InvoiceRepository;
import saltlux.ctv.tranSS.repository.po.PORepository;
import saltlux.ctv.tranSS.repository.project.ProjectRepository;
import saltlux.ctv.tranSS.repository.user.UserRepository;
import saltlux.ctv.tranSS.util.ExcelUtil;
import saltlux.ctv.tranSS.util.TransformUtil;

import java.io.IOException;
import java.math.BigDecimal;
import java.text.ParseException;
import java.util.Date;
import java.util.List;
import java.util.Optional;
import java.util.Timer;
import java.util.stream.Collectors;

import static saltlux.ctv.tranSS.util.TransformUtil.roundByCurrency;

@Slf4j
@Service
public class InvoiceService {
    private final InvoiceRepository invoiceRepo;
    private final CandidateRepository candidateRepo;
    private final ProjectRepository projectRepo;
    private final PORepository poRepo;
    private final ModelMapper modelMapper;
    private final StorageService storageService;
    private final FileSystemStorageService fileSystemStorageService;
    private final UserRepository userRepo;

    @Autowired
    public InvoiceService(
            ModelMapper modelMapper,
            PORepository poRepo,
            CandidateRepository candidateRepo,
            InvoiceRepository invoiceRepo,
            ProjectRepository projectRepo,
            StorageService storageService,
            UserRepository userRepo, FileSystemStorageService fileSystemStorageService) {

        this.modelMapper = modelMapper;
        this.poRepo = poRepo;
        this.candidateRepo = candidateRepo;
        this.invoiceRepo = invoiceRepo;
        this.projectRepo = projectRepo;
        this.storageService = storageService;
        this.userRepo = userRepo;
        this.fileSystemStorageService = fileSystemStorageService;
    }

    /**
     * @param request request
     * @return InvoiceAuditResponse
     */
    public InvoiceAuditResponse getDefaultInvoice(InvoiceScanRequest request) throws ParseException {
        List<PoProjectAssignmentResponse> purchaseOrders = poRepo.getAllForInvoice(
                request.getCompany(), request.getResourceCode(), request.getExternalResourceName(), true);
        BigDecimal total = new BigDecimal("0");

        for (PoProjectAssignmentResponse poResponse : purchaseOrders) {
            String currency = poResponse.getCurrency();
            poResponse.setTotal(roundByCurrency(currency, poResponse.getTotal()));
            total = total.add(poResponse.getTotal());
            Project project = projectRepo.findById(poResponse.getProjectId())
                    .orElseThrow(() -> new ResourceNotFoundException("Project", "id", poResponse.getProjectId()));
            User pmVtc = userRepo.findByCode(project.getPmVtc())
                    .orElseThrow(() -> new ResourceNotFoundException("Project Manager", "code", project.getPmVtc()));
            Employee pmVtcDto = modelMapper.map(pmVtc, Employee.class);
            POProject poProject = modelMapper.map(project, POProject.class);
            poProject.setPmVtc(pmVtcDto);
            poResponse.setProject(poProject);
        }

        InvoiceAuditResponse response = new InvoiceAuditResponse();
        response.setPurchaseOrders(purchaseOrders);
        response.setTotal(total);
        if (!purchaseOrders.isEmpty()) {
            response.setCurrency(purchaseOrders.get(0).getCurrency());
        }
        if (null != request.getResourceCode()) {
            Candidate candidate = candidateRepo.findByCode(request.getResourceCode())
                    .orElseThrow(() -> new ResourceNotFoundException("Candidate", "code", request.getResourceCode()));
            CandidateBasicResponse candidateBasicResponse = modelMapper.map(candidate, CandidateBasicResponse.class);
            response.setCandidate(candidateBasicResponse);
            Payment payment = candidate.getPayment();
            if (null != payment) {
                response.setBankName(payment.getBankName());
                response.setAccount(payment.getAccount());
                response.setSwiftCode(payment.getSwiftCode());
                response.setPayPal(payment.getPayPal());
            }
            response.setResourceName(candidate.getName());
            response.setAddress(candidate.getAddress());
            response.setMobile(candidate.getMobile());
        }

        return response;
    }

    /**
     * Save Assignment
     *
     * @param invoiceRequest assignment request
     * @return ProjectAssignmentCandidateResponse
     */
    public InvoiceAuditResponse create(InvoiceRequest invoiceRequest, String candidateCode) {
        Long id = invoiceRequest.getId();
        Invoice invoice = null == id || id == 0 ? new Invoice() : invoiceRepo.findById(id).get();

        BeanUtils.copyProperties(invoiceRequest, invoice, "candidateId", "purchaseOrders");
        if (null != candidateCode && !"null".equals(candidateCode)) {
            Candidate candidate = candidateRepo.findByCode(candidateCode)
                    .orElseThrow(() -> new ResourceNotFoundException("Candidate", "code", candidateCode));
            invoice.setCandidate(candidate);
        }
        invoice.setDateOfInvoice(new Date());
        Invoice savedInvoice = invoiceRepo.save(invoice);

        List<String> purchaseOrderIds = invoiceRequest.getPurchaseOrders();
        if (null != purchaseOrderIds && !purchaseOrderIds.isEmpty()) {
            for (String poNo : purchaseOrderIds) {
                Optional<PurchaseOrder> orderOptional = poRepo.findByCode(poNo);
                if (orderOptional.isPresent()) {
                    PurchaseOrder order = orderOptional.get();
                    order.setInvoice(savedInvoice);
                    poRepo.save(order);
                }
            }
        }
        return convertToDto(savedInvoice);
    }

    /**
     * @param order assignment
     * @return InvoiceAuditResponse
     */
    private InvoiceAuditResponse convertToDto(Invoice order) {
        return modelMapper.map(order, InvoiceAuditResponse.class);
    }

    /**
     * @param invoiceId invoiceId
     * @return InvoiceAuditResponse
     */
    public InvoiceAuditResponse findById(Long invoiceId) {
        Invoice invoice = invoiceRepo.findById(invoiceId)
                .orElseThrow(() -> new ResourceNotFoundException("PO", "id", invoiceId));
        return toInvoiceItemResponse(invoice);
    }

    /**
     * @param invoice invoice
     * @return InvoiceAuditResponse
     */
    private InvoiceAuditResponse toInvoiceItemResponse(Invoice invoice) {
        InvoiceAuditResponse response = convertToDto(invoice);
        List<PurchaseOrder> purchaseOrders = invoice.getPurchaseOrders();
        List<PoProjectAssignmentResponse> poResponses = purchaseOrders.stream().map(purchaseOrder -> {
            POResponse poResponse = modelMapper.map(purchaseOrder, POResponse.class);
            Project project = projectRepo.findById(purchaseOrder.getAssignment().getProjectId())
                    .orElseThrow(() ->
                            new ResourceNotFoundException("Project", "id",
                                    purchaseOrder.getAssignment().getProjectId()));
            POProject poProject = modelMapper.map(project, POProject.class);
            User pmVtc = userRepo.findByCode(project.getPmVtc())
                    .orElseThrow(() -> new ResourceNotFoundException("Project Manager", "code", project.getPmVtc()));
            Employee pmVtcDto = modelMapper.map(pmVtc, Employee.class);
            poProject.setPmVtc(pmVtcDto);
            poResponse.getAssignment().setProject(poProject);
            return convertToPOAssignment(poResponse, invoice.getId());
        }).collect(Collectors.toList());

        if (!poResponses.isEmpty()) {
            response.setCompany(poResponses.get(0).getCompany());
        }

        response.setPurchaseOrders(poResponses);
        return response;
    }

    /**
     * @param poResponse poResponse
     * @param invoiceId  invoiceId
     * @return PoProjectAssignmentResponse
     */
    private PoProjectAssignmentResponse convertToPOAssignment(POResponse poResponse, Long invoiceId) {
        PoProjectAssignmentResponse poProjectAssignmentResponse = new PoProjectAssignmentResponse();
        BeanUtils.copyProperties(poResponse, poProjectAssignmentResponse);
        BeanUtils.copyProperties(poResponse.getAssignment(), poProjectAssignmentResponse);
        poProjectAssignmentResponse.setPoNo(poResponse.getCode());
        poProjectAssignmentResponse.setInvoiceId(invoiceId);
        poProjectAssignmentResponse.setCompany(poResponse.getCompany());
        return poProjectAssignmentResponse;
    }

    /**
     * @param id id
     * @return Resource
     * @throws IOException    e
     * @throws ParseException e
     */
    public Resource exportInvoice(Long id) throws IOException, ParseException {
        String filePath = ExcelUtil.exportInvoice(findById(id));
        Timer timer = new Timer("Timer");
        timer.schedule(new RemoveFileTask(fileSystemStorageService, filePath), 60000);
        return storageService.loadAsResource(filePath);
    }

    /**
     * @param id id
     */
    @Transactional
    public void delete(Long id) {
        poRepo.updateInvoiceId(id);
        this.invoiceRepo.deleteById(id);
    }

    public List<PoProjectAssignmentResponse> getNeedToBePaidPosTillNow(String candidateCode,
                                                                       String externalResourceName) throws ParseException {
        return poRepo.getAllForInvoice(null, candidateCode, externalResourceName, true);
    }

    /**
     * @return List<InvoiceAuditResponse>
     */
    public List<InvoiceAuditResponse> getInvoices(int isConfirmed) {
        List<Invoice> invoices = invoiceRepo.findByIsConfirmed(isConfirmed);

        return invoices.stream().map(this::toInvoiceItemResponse)
                .collect(Collectors.toList());
    }

    /**
     * @param invoiceId invoiceId
     */
    public void markConfirm(Long invoiceId, Boolean value) {
        Invoice invoice = invoiceRepo.findById(invoiceId)
                .orElseThrow(() -> new ResourceNotFoundException("Invoice", "id", invoiceId));
        invoice.setIsConfirmed(value);
        invoiceRepo.save(invoice);
    }
}