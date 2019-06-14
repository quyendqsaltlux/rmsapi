package saltlux.ctv.tranSS.service;

import lombok.extern.slf4j.Slf4j;
import org.modelmapper.ModelMapper;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.Resource;
import org.springframework.stereotype.Service;
import saltlux.ctv.tranSS.concurrency.RemoveFileTask;
import saltlux.ctv.tranSS.enums.CompanyEnum;
import saltlux.ctv.tranSS.exception.BadRequestException;
import saltlux.ctv.tranSS.exception.ResourceNotFoundException;
import saltlux.ctv.tranSS.model.*;
import saltlux.ctv.tranSS.payload.PagedResponse;
import saltlux.ctv.tranSS.payload.employee.Employee;
import saltlux.ctv.tranSS.payload.po.*;
import saltlux.ctv.tranSS.repository.candidate.CandidateAbilityRepository;
import saltlux.ctv.tranSS.repository.po.PORepository;
import saltlux.ctv.tranSS.repository.project.ProjectAssignmentRepository;
import saltlux.ctv.tranSS.repository.project.ProjectRepository;
import saltlux.ctv.tranSS.repository.user.UserRepository;
import saltlux.ctv.tranSS.security.UserPrincipal;
import saltlux.ctv.tranSS.util.ExcelUtil;
import saltlux.ctv.tranSS.util.TransformUtil;

import java.io.IOException;
import java.util.Timer;

import static saltlux.ctv.tranSS.util.ValidationUtil.isTrue;

@Slf4j
@Service
public class POService {
    private final StorageService storageService;
    private final ProjectAssignmentRepository assignmentRepository;
    private final ProjectRepository projectRepository;
    private final CandidateAbilityRepository abilityRepository;
    private final PORepository poRepository;
    private final UserRepository userRepo;
    private final ModelMapper modelMapper;
    private final FileSystemStorageService fileSystemStorageService;

    @Autowired
    public POService(ProjectAssignmentRepository assignmentRepository,
                     CandidateAbilityRepository abilityRepository,
                     ProjectRepository projectRepository,
                     ModelMapper modelMapper,
                     PORepository poRepository,
                     StorageService storageService,
                     UserRepository userRepo,
                     FileSystemStorageService fileSystemStorageService) {

        this.assignmentRepository = assignmentRepository;
        this.abilityRepository = abilityRepository;
        this.projectRepository = projectRepository;
        this.modelMapper = modelMapper;
        this.poRepository = poRepository;
        this.storageService = storageService;
        this.userRepo = userRepo;
        this.fileSystemStorageService = fileSystemStorageService;
    }

    /**
     * @param assignmentId assignmentId
     * @return POResponse
     */
    public POResponse getDefaultPo(Long assignmentId) {
        ProjectAssignment assignment = assignmentRepository.findById(assignmentId)
                .orElseThrow(() -> new ResourceNotFoundException("Assignment", "id", assignmentId));
        Project project = projectRepository.findById(assignment.getProjectId())
                .orElseThrow(() -> new ResourceNotFoundException("Project", "id", assignment.getProjectId()));

        User pmVtc = userRepo.findByCode(project.getPmVtc())
                .orElseThrow(() -> new ResourceNotFoundException("Project Manager", "code", project.getPmVtc()));
        Employee pmVtcDto = modelMapper.map(pmVtc, Employee.class);

        POResponse poResponse = new POResponse();
        POAssignment poAssignment = modelMapper.map(assignment, POAssignment.class);
        POProject poProject = modelMapper.map(project, POProject.class);
        poProject.setPmVtc(pmVtcDto);
        poAssignment.setProject(poProject);
        if (null != assignment.getAbilityId()) {
            CandidateAbility ability = abilityRepository.findById(assignment.getAbilityId())
                    .orElseThrow(() -> new ResourceNotFoundException("Candidate Rate", "id", assignment.getProjectId()));
            poAssignment.setAbility(modelMapper.map(ability, POAbility.class));
        }
        poResponse.setAssignment(poAssignment);

        if(!isTrue(assignment.getExternalResource()) &&
                null != assignment.getCandidate() && null != assignment.getCandidate().getId()){
            poResponse.setCurrency(assignment.getCandidate().getCurrency());
        }else {
            poResponse.setCurrency("USD");
        }

        return poResponse;
    }


    /**
     * Save Assignment
     *
     * @param poRequest assignment request
     * @return ProjectAssignmentCandidateResponse
     */
    public POResponse create(PORequest poRequest, Long assignmentId) {
        Long id = poRequest.getId();
        PurchaseOrder purchaseOrder = null == id || id == 0 ? new PurchaseOrder() : poRepository.findById(id).get();

        BeanUtils.copyProperties(poRequest, purchaseOrder, "code");
        ProjectAssignment assignment = assignmentRepository.findById(assignmentId)
                .orElseThrow(() -> new ResourceNotFoundException("Assignment", "id", assignmentId));
        Project project = projectRepository.findById(assignment.getProjectId())
                .orElseThrow(() -> new ResourceNotFoundException("Project", "id", assignment.getProjectId()));
        purchaseOrder.setAssignment(assignment);
        purchaseOrder.setCompany(project.getCompany());
        if (null == id || id == 0) {
            purchaseOrder.setCode(generatePOCode(project.getCompany(), project.getPmVtc()));
        }
        PurchaseOrder savedOrder = poRepository.save(purchaseOrder);
        return convertToDto(savedOrder);
    }

    /**
     * @param order assignment
     * @return POResponse
     */
    private POResponse convertToDto(PurchaseOrder order) {
        POResponse response = modelMapper.map(order, POResponse.class);

        projectRepository.findById(order.getAssignment().getProjectId())
                .ifPresent(project1 -> {
                    User pmVtc = userRepo.findByCode(project1.getPmVtc())
                            .orElseThrow(() -> new ResourceNotFoundException("Project Manager", "code", project1.getPmVtc()));
                    Employee pmVtcDto = modelMapper.map(pmVtc, Employee.class);
                    POProject poProject = modelMapper.map(project1, POProject.class);
                    poProject.setPmVtc(pmVtcDto);

                    response.getAssignment().setProject(poProject);
                });
        return response;
    }

    /**
     * @param company P or SP (saltlux or saltlux partner)
     * @return CODE
     */
    private String generatePOCode(String company, String pmCode) {
        String maxCode = poRepository.getMaxCodeByCompany(company, pmCode).get(0);
        boolean isSP = CompanyEnum.SP.toString().equals(company);
        String maxCodeWithoutCompanySuffix = null;
        if (null != maxCode) {
            maxCodeWithoutCompanySuffix = isSP ? maxCode.substring(0, maxCode.length() - 2) :
                    maxCode;
        }
        String newPoCode = TransformUtil.generatePOCode(pmCode, maxCodeWithoutCompanySuffix, 6, 2);
        return isSP ? newPoCode + company : newPoCode;
    }

    public POResponse findById(Long id) {
        PurchaseOrder order = poRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("PO", "id", id));
        return convertToDto(order);
    }

    /**
     * @param id id
     * @return Resource
     * @throws IOException e
     */
    public Resource exportPO(Long id) throws IOException {
        String filePath = ExcelUtil.exportPO(findById(id));
        Timer timer = new Timer("Timer");
        timer.schedule(new RemoveFileTask(fileSystemStorageService, filePath), 60000);
        return storageService.loadAsResource(filePath);
    }

    public PagedResponse<PoProjectAssignmentResponse> search(int page, int size, String keyWord,
                                                             String orderBy, String sortDirection,
                                                             PoFilterRequest filters, String pmVtcCode,
                                                             UserPrincipal currentUser) {

        return poRepository.search(page, size, keyWord, orderBy, sortDirection, filters, pmVtcCode, currentUser);
    }

    /**
     * @param id id
     */
    public void delete(Long id) {
        PurchaseOrder order = poRepository.findById(id).orElseThrow(() -> new ResourceNotFoundException("PO", "id", id));
        Invoice invoice = order.getInvoice();
        if(null != invoice && null != invoice.getId()){
            throw new BadRequestException("Can not delete PO due to an Invoice existed");
        }
        this.poRepository.deleteByPoId(id);
    }

}