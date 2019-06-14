package saltlux.ctv.tranSS.Specification;

import lombok.NoArgsConstructor;
import org.springframework.data.jpa.domain.Specification;
import saltlux.ctv.tranSS.model.Candidate;
import saltlux.ctv.tranSS.model.Project;
import saltlux.ctv.tranSS.model.ProjectAssignment;
import saltlux.ctv.tranSS.model.User;

import javax.persistence.criteria.*;

@NoArgsConstructor
public class ProjectAssignmentSpecification extends BaseSpecification implements Specification<ProjectAssignment> {


    public ProjectAssignmentSpecification(SearchCriteria criteria) {
        super(criteria);
    }


    @Override
    public Predicate toPredicate
            (Root<ProjectAssignment> root, CriteriaQuery<?> query, CriteriaBuilder builder) {
        query.distinct(true);

        return buildPredicate(root.get(criteria.getKey()), builder, criteria);
    }

}