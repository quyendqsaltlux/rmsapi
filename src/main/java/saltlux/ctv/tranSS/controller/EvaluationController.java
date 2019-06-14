package saltlux.ctv.tranSS.controller;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import saltlux.ctv.tranSS.payload.BaseFilterRequest;
import saltlux.ctv.tranSS.payload.PagedResponse;
import saltlux.ctv.tranSS.payload.evaluation.*;
import saltlux.ctv.tranSS.service.EvaluationService;
import saltlux.ctv.tranSS.util.AppConstants;

import javax.validation.Valid;
import javax.validation.constraints.NotNull;

@Slf4j
@RestController
@RequestMapping("/api/evaluation")
public class EvaluationController {

    private final EvaluationService evaluationService;


    @Autowired
    public EvaluationController(EvaluationService evaluationService) {
        this.evaluationService = evaluationService;
    }

    @PostMapping("/specific/{assignmentId}")
    @PreAuthorize("hasRole('PM') or hasRole('ADMIN') or hasRole('RM')")
    public SpecificCommentResponse saveSpecificComment(@Valid @RequestBody SpecificCommentRequest commentRequest,
                                                       @PathVariable @NotNull Long assignmentId) throws Exception {
        return evaluationService.saveSpecificComment(commentRequest, assignmentId);
    }

    @GetMapping("/specific/{id}")
    @PreAuthorize("hasRole('PM') or hasRole('ADMIN') or hasRole('RM')")
    public SpecificCommentResponse findSpecificComment(@PathVariable @NotNull Long id) {
        return evaluationService.findSpecificComment(id);
    }

    @DeleteMapping("/specific/{id}")
    @PreAuthorize("hasRole('PM') or hasRole('ADMIN') or hasRole('RM')")
    public ResponseEntity<?> deleteSpecificComment(@PathVariable Long id) {
        this.evaluationService.deleteSpecificComment(id);
        return ResponseEntity.accepted().headers(new HttpHeaders()).body(null);
    }

    @PostMapping("/specific/search/{candidateId}")
    @PreAuthorize("hasRole('PM') or hasRole('ADMIN') or hasRole('RM')")
    public PagedResponse<SpecificCommentResponse> search(@RequestParam(value = "keyword") String keyWord,
                                                         @RequestParam(value = "orderBy") String orderBy,
                                                         @RequestParam(value = "sortDirection") String sortDirection,
                                                         @RequestParam(value = "page", defaultValue = AppConstants.DEFAULT_PAGE_NUMBER) int page,
                                                         @RequestParam(value = "size", defaultValue = AppConstants.DEFAULT_PAGE_SIZE) int size,
                                                         @Valid @RequestBody BaseFilterRequest filters,
                                                         @PathVariable Long candidateId) {
        return evaluationService.searchSpecificComments(page, size, keyWord, orderBy, sortDirection, filters, candidateId);
    }

    //    GENERAL
    @PostMapping("/general/{candidateId}")
    @PreAuthorize("hasRole('PM') or hasRole('ADMIN') or hasRole('RM')")
    public GeneralCommentResponse saveGeneralComment(@Valid @RequestBody GeneralCommentRequest commentRequest,
                                                     @PathVariable @NotNull Long candidateId) throws Exception {
        return evaluationService.saveGeneralComment(commentRequest, candidateId);
    }

    @GetMapping("/general/{id}")
    @PreAuthorize("hasRole('PM') or hasRole('ADMIN') or hasRole('RM')")
    public GeneralCommentResponse findGeneralComment(@PathVariable @NotNull Long id) {
        return evaluationService.findGeneralComment(id);
    }

    @DeleteMapping("/general/{id}")
    @PreAuthorize("hasRole('PM') or hasRole('ADMIN') or hasRole('RM')")
    public ResponseEntity<?> deleteGeneralComment(@PathVariable Long id) {
        this.evaluationService.deleteGeneralComment(id);
        return ResponseEntity.accepted().headers(new HttpHeaders()).body(null);
    }

    @PostMapping("/general/search/{candidateId}")
    @PreAuthorize("hasRole('PM') or hasRole('ADMIN') or hasRole('RM')")
    public PagedResponse<GeneralCommentResponse> searchGeneralComment(@RequestParam(value = "keyword") String keyWord,
                                                                      @RequestParam(value = "orderBy") String orderBy,
                                                                      @RequestParam(value = "sortDirection") String sortDirection,
                                                                      @RequestParam(value = "page", defaultValue = AppConstants.DEFAULT_PAGE_NUMBER) int page,
                                                                      @RequestParam(value = "size", defaultValue = AppConstants.DEFAULT_PAGE_SIZE) int size,
                                                                      @Valid @RequestBody BaseFilterRequest filters,
                                                                      @PathVariable Long candidateId) {
        return evaluationService.searchGeneralComments(page, size, keyWord, orderBy, sortDirection, filters, candidateId);
    }

    //    OTHER NOTE
    @PostMapping("/otherNote/{candidateId}")
    @PreAuthorize("hasRole('PM') or hasRole('ADMIN') or hasRole('RM')")
    public OtherNoteCommentResponse saveOtherNote(@Valid @RequestBody OtherNoteCommentRequest commentRequest,
                                                  @PathVariable @NotNull Long candidateId) throws Exception {
        return evaluationService.saveOtherNote(commentRequest, candidateId);
    }

    @GetMapping("/otherNote/{id}")
    @PreAuthorize("hasRole('PM') or hasRole('ADMIN') or hasRole('RM')")
    public OtherNoteCommentResponse findOtherNote(@PathVariable @NotNull Long id) {
        return evaluationService.findOtherNote(id);
    }

    @DeleteMapping("/otherNote/{id}")
    @PreAuthorize("hasRole('PM') or hasRole('ADMIN') or hasRole('RM')")
    public ResponseEntity<?> deleteOtherNote(@PathVariable Long id) {
        this.evaluationService.deleteOtherNote(id);
        return ResponseEntity.accepted().headers(new HttpHeaders()).body(null);
    }

    @PostMapping("/otherNote/search/{candidateId}")
    @PreAuthorize("hasRole('PM') or hasRole('ADMIN') or hasRole('RM')")
    public PagedResponse<OtherNoteCommentResponse> searchOtherNote(@RequestParam(value = "keyword") String keyWord,
                                                                   @RequestParam(value = "orderBy") String orderBy,
                                                                   @RequestParam(value = "sortDirection") String sortDirection,
                                                                   @RequestParam(value = "page", defaultValue = AppConstants.DEFAULT_PAGE_NUMBER) int page,
                                                                   @RequestParam(value = "size", defaultValue = AppConstants.DEFAULT_PAGE_SIZE) int size,
                                                                   @Valid @RequestBody BaseFilterRequest filters,
                                                                   @PathVariable Long candidateId) {
        return evaluationService.searchOtherNote(page, size, keyWord, orderBy, sortDirection, filters, candidateId);
    }
}

