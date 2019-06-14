package saltlux.ctv.tranSS.controller;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.MissingServletRequestParameterException;
import org.springframework.web.bind.annotation.*;
import saltlux.ctv.tranSS.model.ResourceTestWaiting;
import saltlux.ctv.tranSS.payload.ApiResponse;
import saltlux.ctv.tranSS.payload.BaseFilterRequest;
import saltlux.ctv.tranSS.payload.PagedResponse;
import saltlux.ctv.tranSS.payload.resource.ResourceTestWaitingRequest;
import saltlux.ctv.tranSS.service.CandidateTestWaitingService;
import saltlux.ctv.tranSS.util.AppConstants;

import javax.validation.Valid;

@Slf4j
@RestController
@RequestMapping("/api/resource-test-waiting")
public class CandidateTestWaitingController {

    private final CandidateTestWaitingService candidateService;


    @Autowired
    public CandidateTestWaitingController(CandidateTestWaitingService candidateService) {
        this.candidateService = candidateService;
    }

    @PostMapping
    @PreAuthorize("hasRole('PM') or hasRole('ADMIN') or hasRole('RM')")
    public ResourceTestWaiting createCandidate(@Valid @RequestBody ResourceTestWaitingRequest candidateRequest) throws Exception {
        if (candidateRequest == null) {
            throw new MissingServletRequestParameterException(null, null);
        }
        return candidateService.create(candidateRequest);
    }

    @PostMapping("/search")
    @PreAuthorize("hasRole('PM') or hasRole('ADMIN') or hasRole('RM')")
    public PagedResponse<ResourceTestWaiting> searchCandidate(@RequestParam(value = "keyword") String keyWord,
                                                              @RequestParam(value = "orderBy") String orderBy,
                                                              @RequestParam(value = "sortDirection") String sortDirection,
                                                              @RequestParam(value = "page", defaultValue = AppConstants.DEFAULT_PAGE_NUMBER) int page,
                                                              @RequestParam(value = "size", defaultValue = AppConstants.DEFAULT_PAGE_SIZE) int size,
                                                              @RequestBody BaseFilterRequest search) {
        return candidateService.search(page, size, keyWord, orderBy, sortDirection, search);
    }

    @GetMapping("/findById/{id}")
    @PreAuthorize("hasRole('PM') or hasRole('ADMIN') or hasRole('RM')")
    public ResourceTestWaiting getById(@PathVariable Long id) {
        return candidateService.findById(id);
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('PM') or hasRole('ADMIN') or hasRole('RM')")
    public ResponseEntity<?> delete(@PathVariable Long id) {
        candidateService.delete(id);
        return ResponseEntity.created(null)
                .body(new ApiResponse(true, "Delete successfully"));
    }
}
