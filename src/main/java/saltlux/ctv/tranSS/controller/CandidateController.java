package saltlux.ctv.tranSS.controller;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.MissingServletRequestParameterException;
import org.springframework.web.bind.annotation.*;
import saltlux.ctv.tranSS.model.Candidate;
import saltlux.ctv.tranSS.payload.ApiResponse;
import saltlux.ctv.tranSS.payload.BaseFilterRequest;
import saltlux.ctv.tranSS.payload.PagedResponse;
import saltlux.ctv.tranSS.payload.resource.CandidateRequest;
import saltlux.ctv.tranSS.payload.resource.ResourceForProjectResponse;
import saltlux.ctv.tranSS.service.CandidateService;
import saltlux.ctv.tranSS.util.AppConstants;

import javax.validation.Valid;
import java.util.List;

@Slf4j
@RestController
@RequestMapping("/api/candidate")
public class CandidateController {

    private final CandidateService candidateService;


    @Autowired
    public CandidateController(CandidateService candidateService) {
        this.candidateService = candidateService;
    }

    @PostMapping
    @PreAuthorize("hasRole('PM') or hasRole('ADMIN') or hasRole('RM')")
    public Candidate createCandidate(@Valid @RequestBody CandidateRequest candidateRequest) throws Exception {
        if (candidateRequest == null) {
            throw new MissingServletRequestParameterException(null, null);
        }
        return candidateService.createCandidate(candidateRequest);
    }

    @PostMapping("/search")
    @PreAuthorize("hasRole('PM') or hasRole('ADMIN') or hasRole('RM')")
    public PagedResponse<Candidate> searchCandidate(@RequestParam(value = "keyword") String keyWord,
                                                    @RequestParam(value = "orderBy") String orderBy,
                                                    @RequestParam(value = "sortDirection") String sortDirection,
                                                    @RequestParam(value = "page", defaultValue = AppConstants.DEFAULT_PAGE_NUMBER) int page,
                                                    @RequestParam(value = "size", defaultValue = AppConstants.DEFAULT_PAGE_SIZE) int size,
                                                    @RequestBody BaseFilterRequest search) {
        return candidateService.searchCandidate(page, size, keyWord, orderBy, sortDirection, search);
    }

    @GetMapping("/findById/{id}")
    @PreAuthorize("hasRole('PM') or hasRole('ADMIN') or hasRole('RM')")
    public Candidate getCandidateById(@PathVariable Long id) {
        return candidateService.findCandidateById(id);
    }

    @GetMapping("/{code}")
    @PreAuthorize("hasRole('PM') or hasRole('ADMIN') or hasRole('RM')")
    public Candidate getCandidateByCode(@PathVariable String code) throws Exception {
        return candidateService.getByCode(code);
    }

    @GetMapping("/findCandidateByCodeOrNameLike")
    @PreAuthorize("hasRole('PM') or hasRole('ADMIN') or hasRole('RM')")
    public List<ResourceForProjectResponse> findCandidateByCodeOrNameLike(@RequestParam(value = "keyword") String keyword) {
        return candidateService.findCandidateByCodeOrNameLike(keyword);
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('PM') or hasRole('ADMIN') or hasRole('RM')")
    public ResponseEntity<?> delete(@PathVariable Long id) {
        candidateService.delete(id);
        return ResponseEntity.created(null)
                .body(new ApiResponse(true, "Delete successfully"));
    }

}
