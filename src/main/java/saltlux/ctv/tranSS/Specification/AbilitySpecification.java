package saltlux.ctv.tranSS.Specification;

import lombok.NoArgsConstructor;
import org.springframework.data.jpa.domain.Specification;
import saltlux.ctv.tranSS.model.Candidate;
import saltlux.ctv.tranSS.model.CandidateAbility;

import javax.persistence.criteria.*;

@NoArgsConstructor
public class AbilitySpecification extends BaseSpecification implements Specification<CandidateAbility> {

    private SearchCriteria criteria;
    private boolean isSearchCandidate;
    private boolean isGroup;

    public AbilitySpecification(SearchCriteria criteria) {
        this.criteria = criteria;
    }

    public AbilitySpecification(SearchCriteria criteria, boolean isSearchCandidate) {
        this.criteria = criteria;
        this.isSearchCandidate = isSearchCandidate;
    }

    public AbilitySpecification(boolean isGroup) {
        this.isGroup = isGroup;
    }

    @Override
    public Predicate toPredicate
            (Root<CandidateAbility> root, CriteriaQuery<?> query, CriteriaBuilder builder) {
        Join<CandidateAbility, Candidate> abilityJoin = root.join("abilities", JoinType.LEFT);
        query.distinct(true);
        query.orderBy(builder.asc(abilityJoin.get("id")));

        if (isGroup) {
            return null;
        }
        if (this.isSearchCandidate) {
            return buildPredicate(root.get(criteria.getKey()), builder, criteria);
        } else {
            return buildPredicate(abilityJoin.get(criteria.getKey()), builder, criteria);
        }
    }

}