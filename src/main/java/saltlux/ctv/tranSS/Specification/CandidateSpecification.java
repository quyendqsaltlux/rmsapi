package saltlux.ctv.tranSS.Specification;

import lombok.NoArgsConstructor;
import org.springframework.data.jpa.domain.Specification;
import saltlux.ctv.tranSS.model.Candidate;
import saltlux.ctv.tranSS.model.CandidateAbility;

import javax.persistence.criteria.*;

@NoArgsConstructor
public class CandidateSpecification extends BaseSpecification implements Specification<Candidate> {

    private SearchCriteria criteria;
    private boolean isSearchAbility;

    public CandidateSpecification(SearchCriteria criteria) {
        this.criteria = criteria;
    }

    public CandidateSpecification(SearchCriteria criteria, boolean isSearchAbility) {
        this.criteria = criteria;
        this.isSearchAbility = isSearchAbility;
    }

    @Override
    public Predicate toPredicate
            (Root<Candidate> root, CriteriaQuery<?> query, CriteriaBuilder builder) {
        query.distinct(true);
        Join<Candidate, CandidateAbility> abilityJoin = root.join("abilities", JoinType.LEFT);
        if (this.isSearchAbility) {
            return buildPredicate(abilityJoin.get(criteria.getKey()), builder, criteria);
        } else {
            return buildPredicate(root.get(criteria.getKey()), builder, criteria);
        }
    }

}