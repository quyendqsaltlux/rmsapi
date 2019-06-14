package saltlux.ctv.tranSS.controller;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import saltlux.ctv.tranSS.model.CandidateAbility;
import saltlux.ctv.tranSS.payload.BaseFilterRequest;
import saltlux.ctv.tranSS.payload.PagedResponse;
import saltlux.ctv.tranSS.payload.resource.AbilitySearchResponse;
import saltlux.ctv.tranSS.payload.resource.CandidateAbilitiesWithCurrency;
import saltlux.ctv.tranSS.payload.resource.CandidateAbilityRequest;
import saltlux.ctv.tranSS.payload.resource.CandidateAbilitySearchRequest;
import saltlux.ctv.tranSS.service.CandidateAbilityService;
import saltlux.ctv.tranSS.util.AppConstants;

import javax.validation.Valid;
import java.util.List;

@Slf4j
@RestController
@RequestMapping("/api/candidateAbilities")
public class CandidateAbilityController {

    private final CandidateAbilityService abilityService;


    @Autowired
    public CandidateAbilityController(CandidateAbilityService abilityService) {
        this.abilityService = abilityService;
    }

    @PostMapping()
    @PreAuthorize("hasRole('PM') or hasRole('ADMIN') or hasRole('RM')")
    public CandidateAbility saveAbility(@Valid @RequestBody CandidateAbilityRequest abilityRequest) throws Exception {
        return abilityService.saveAbility(abilityRequest);
    }

    @GetMapping("/listAll/{candidateId}")
    @PreAuthorize("hasRole('PM') or hasRole('ADMIN') or hasRole('RM')")
    public CandidateAbilitiesWithCurrency getList(@PathVariable Long candidateId) {
        return abilityService.getListAll(candidateId);
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('PM') or hasRole('ADMIN') or hasRole('RM')")
    public ResponseEntity<?> delete(@PathVariable Long id) {
        this.abilityService.delete(id);
        return ResponseEntity.accepted().headers(new HttpHeaders()).body(null);
    }

    @PostMapping("/search")
    @PreAuthorize("hasRole('PM') or hasRole('ADMIN') or hasRole('RM')")
    public PagedResponse<AbilitySearchResponse> search(@RequestParam(value = "keyword") String keyWord,
                                                                @RequestParam(value = "page", defaultValue = AppConstants.DEFAULT_PAGE_NUMBER) int page,
                                                                @RequestParam(value = "size", defaultValue = AppConstants.DEFAULT_PAGE_SIZE) int size,
                                                                @RequestBody @Valid CandidateAbilitySearchRequest search) {
        return abilityService.searchAbility(page, size, keyWord, search);
    }
}
