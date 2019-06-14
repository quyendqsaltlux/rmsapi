package saltlux.ctv.tranSS.controller;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.MissingServletRequestParameterException;
import org.springframework.web.bind.annotation.*;
import saltlux.ctv.tranSS.exception.BadRequestException;
import saltlux.ctv.tranSS.payload.ApiResponse;
import saltlux.ctv.tranSS.payload.invoice.InvoiceAuditResponse;
import saltlux.ctv.tranSS.payload.invoice.InvoiceConfirmRequest;
import saltlux.ctv.tranSS.payload.invoice.InvoiceRequest;
import saltlux.ctv.tranSS.payload.invoice.InvoiceScanRequest;
import saltlux.ctv.tranSS.payload.po.PoProjectAssignmentResponse;
import saltlux.ctv.tranSS.service.InvoiceService;

import javax.validation.Valid;
import javax.validation.constraints.NotNull;
import java.io.IOException;
import java.text.ParseException;
import java.util.List;

@Slf4j
@RestController
@RequestMapping("/api/invoices")
public class InvoiceController {

    private final InvoiceService invoiceService;

    @Autowired
    public InvoiceController(InvoiceService invoiceService) {
        this.invoiceService = invoiceService;
    }

    @PostMapping("/save/{candidateCode}/{externalResourceName}")
    @PreAuthorize("hasRole('PM') or hasRole('ADMIN') or hasRole('RM')")
    public InvoiceAuditResponse create(@Valid @RequestBody InvoiceRequest invoiceRequest, @PathVariable String candidateCode,
                                       @PathVariable String externalResourceName) throws Exception {
        if (invoiceRequest == null || (null == candidateCode && null == externalResourceName)) {
            throw new MissingServletRequestParameterException(null, null);
        }
        return invoiceService.create(invoiceRequest, candidateCode);
    }

    @PostMapping("/getDefaultInvoice")
    @PreAuthorize("hasRole('PM') or hasRole('ADMIN') or hasRole('RM')")
    public InvoiceAuditResponse getDefaultInvoice(@RequestBody @Valid InvoiceScanRequest request) throws ParseException {
        if (request.invalid()) {
            throw new BadRequestException("resource name or external resource name is required");
        }
        return invoiceService.getDefaultInvoice(request);
    }

    @GetMapping("/findById/{id}")
    @PreAuthorize("hasRole('PM') or hasRole('ADMIN') or hasRole('RM')")
    public InvoiceAuditResponse findById(@PathVariable Long id) {
        return invoiceService.findById(id);
    }


    @PostMapping("/exportInvoice/{id}")
    @PreAuthorize("hasRole('PM') or hasRole('ADMIN') or hasRole('RM')")
    public ResponseEntity<Resource> exportPO(@PathVariable @NotNull Long id) throws IOException, ParseException {
        Resource file = invoiceService.exportInvoice(id);
//        storageService.deleteFile(file.getURI().getPath());
        return ResponseEntity.ok().header(HttpHeaders.CONTENT_DISPOSITION,
                "attachment; filename=\"" + file.getFilename() + "\"").body(file);
    }

    /**
     * @param request request
     * @return List<PoProjectAssignmentResponse>
     * @throws ParseException e
     */
    @PostMapping("/scanPos")
    @PreAuthorize("hasRole('PM') or hasRole('ADMIN') or hasRole('RM')")
    public List<PoProjectAssignmentResponse> scanPos(@RequestBody @Valid InvoiceScanRequest request) throws ParseException {
        return invoiceService.getNeedToBePaidPosTillNow(request.getResourceCode(), request.getExternalResourceName());
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('PM') or hasRole('ADMIN') or hasRole('RM')")
    public ResponseEntity<?> delete(@PathVariable Long id) {
        invoiceService.delete(id);
        return ResponseEntity.created(null)
                .body(new ApiResponse(true, "Delete successfully"));
    }

    @GetMapping("/getNotConfirmedInvoices")
    @PreAuthorize("hasRole('PM') or hasRole('ADMIN') or hasRole('RM')")
    public List<InvoiceAuditResponse> getInvoices(@RequestParam(value = "isConfirmed", defaultValue = "false") String isConfirmed) {
        int confirmed = "true".equals(isConfirmed) ? 1 : 0;
        return invoiceService.getInvoices(confirmed);
    }

    @PostMapping("/markConfirm/{id}")
    @PreAuthorize("hasRole('PM') or hasRole('ADMIN') or hasRole('RM')")
    public ResponseEntity<?> markConfirm(@PathVariable Long id, @RequestBody InvoiceConfirmRequest confirmValue) {
        invoiceService.markConfirm(id, confirmValue.getValue());
        return ResponseEntity.created(null)
                .body(new ApiResponse(true, "Successfully"));
    }

}
