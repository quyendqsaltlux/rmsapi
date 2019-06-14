package saltlux.ctv.tranSS.controller;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.MissingServletRequestParameterException;
import org.springframework.web.bind.annotation.*;
import saltlux.ctv.tranSS.payload.ApiResponse;
import saltlux.ctv.tranSS.payload.PagedResponse;
import saltlux.ctv.tranSS.payload.po.PORequest;
import saltlux.ctv.tranSS.payload.po.POResponse;
import saltlux.ctv.tranSS.payload.po.PoFilterRequest;
import saltlux.ctv.tranSS.payload.po.PoProjectAssignmentResponse;
import saltlux.ctv.tranSS.security.CurrentUser;
import saltlux.ctv.tranSS.security.UserPrincipal;
import saltlux.ctv.tranSS.service.POService;
import saltlux.ctv.tranSS.util.AppConstants;

import javax.validation.Valid;
import javax.validation.constraints.NotNull;
import java.io.IOException;

@Slf4j
@RestController
@RequestMapping("/api/purchaseOrder")
public class POController {

    private final POService poService;

    @Autowired
    public POController(POService poService) {
        this.poService = poService;
    }

    @PostMapping("/save/{assignmentId}")
    @PreAuthorize("hasRole('PM') or hasRole('ADMIN') or hasRole('RM')")
    public POResponse create(@Valid @RequestBody PORequest poRequest,
                             @PathVariable @NotNull Long assignmentId) throws Exception {
        if (poRequest == null) {
            throw new MissingServletRequestParameterException(null, null);
        }
        return poService.create(poRequest, assignmentId);
    }

    @GetMapping("/getDefaultPo/{assignmentId}")
    @PreAuthorize("hasRole('PM') or hasRole('ADMIN') or hasRole('RM')")
    public POResponse getDefaultPo(@PathVariable Long assignmentId) {
        return poService.getDefaultPo(assignmentId);
    }

    @GetMapping("/findById/{id}")
    @PreAuthorize("hasRole('PM') or hasRole('ADMIN') or hasRole('RM')")
    public POResponse getCandidateById(@PathVariable Long id) {
        return poService.findById(id);
    }


    @PostMapping("/exportPo/{id}")
    @PreAuthorize("hasRole('PM') or hasRole('ADMIN') or hasRole('RM')")
    public ResponseEntity<Resource> exportPO(@PathVariable @NotNull Long id) throws IOException {
        Resource file = poService.exportPO(id);
//        storageService.deleteFile(file.getURI().getPath());
        return ResponseEntity.ok().header(HttpHeaders.CONTENT_DISPOSITION,
                "attachment; filename=\"" + file.getFilename() + "\"").body(file);
    }

    @PostMapping("/search/{pmVtcCode}")
    @PreAuthorize("hasRole('PM') or hasRole('ADMIN') or hasRole('RM')")
    public PagedResponse<PoProjectAssignmentResponse> search(@CurrentUser UserPrincipal currentUser,
                                                             @RequestParam(value = "keyword") String keyWord,
                                                             @RequestParam(value = "orderBy") String orderBy,
                                                             @RequestParam(value = "sortDirection") String sortDirection,
                                                             @RequestParam(value = "page", defaultValue = AppConstants.DEFAULT_PAGE_NUMBER) int page,
                                                             @RequestParam(value = "size", defaultValue = AppConstants.DEFAULT_PAGE_SIZE) int size,
                                                             @Valid @RequestBody PoFilterRequest filters,
                                                             @PathVariable String pmVtcCode) {
        return poService.search(page, size, keyWord, orderBy, sortDirection, filters, pmVtcCode, currentUser);
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('PM') or hasRole('ADMIN') or hasRole('RM')")
    public ResponseEntity<?> delete(@PathVariable Long id) {
        poService.delete(id);
        return ResponseEntity.created(null)
                .body(new ApiResponse(true, "Delete successfully"));
    }

//    @GetMapping("/sendEmail/{poId}")
//    @PreAuthorize("hasRole('PM') or hasRole('ADMIN') or hasRole('RM')")
//    public ResponseEntity<?> sendEmail(@PathVariable Long poId) {
//        poService.sendEmail(poId);
//        return ResponseEntity.created(null)
//                .body(new ApiResponse(true, "Sent email successfully"));
//    }

}
