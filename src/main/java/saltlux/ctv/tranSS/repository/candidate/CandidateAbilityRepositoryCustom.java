package saltlux.ctv.tranSS.repository.candidate;

import org.springframework.stereotype.Repository;
import saltlux.ctv.tranSS.model.CandidateAbility;
import saltlux.ctv.tranSS.payload.BaseFilterRequest;
import saltlux.ctv.tranSS.payload.PagedResponse;
import saltlux.ctv.tranSS.payload.resource.CandidateAbilitySearchRequest;

@Repository
public interface CandidateAbilityRepositoryCustom {
    PagedResponse<CandidateAbility> search(int page, int size, String keyWord, CandidateAbilitySearchRequest filters);

}
