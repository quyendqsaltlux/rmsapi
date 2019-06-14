package saltlux.ctv.tranSS.Specification;

import lombok.NoArgsConstructor;
import org.springframework.data.jpa.domain.Specification;
import saltlux.ctv.tranSS.model.Candidate;
import saltlux.ctv.tranSS.model.CandidateAbility;

import javax.persistence.criteria.*;

@NoArgsConstructor
public class CandidateTestWaitingSpecification extends BaseSpecification implements Specification<Candidate> {

    private SearchCriteria criteria;

    public CandidateTestWaitingSpecification(SearchCriteria criteria) {
        this.criteria = criteria;
    }

    @Override
    public Predicate toPredicate
            (Root<Candidate> root, CriteriaQuery<?> query, CriteriaBuilder builder) {
        query.distinct(true);

        return buildPredicate(root.get(criteria.getKey()), builder, criteria);
    }

}