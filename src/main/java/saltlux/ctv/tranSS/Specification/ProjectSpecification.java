package saltlux.ctv.tranSS.Specification;

import lombok.NoArgsConstructor;
import org.springframework.data.jpa.domain.Specification;
import saltlux.ctv.tranSS.model.Candidate;
import saltlux.ctv.tranSS.model.Project;
import saltlux.ctv.tranSS.model.User;

import javax.persistence.criteria.*;

@NoArgsConstructor
public class ProjectSpecification extends BaseSpecification implements Specification<Project> {

    private boolean isSearchPm;
    private boolean isSearchAssignment;

    public ProjectSpecification(SearchCriteria criteria) {
        super(criteria);
    }

    public ProjectSpecification(SearchCriteria criteria, boolean isSearchPm, boolean isSearchAssignment) {
        super(criteria);
        this.isSearchPm = isSearchPm;
        this.isSearchAssignment = isSearchAssignment;
    }

    @Override
    public Predicate toPredicate
            (Root<Project> root, CriteriaQuery<?> query, CriteriaBuilder builder) {
        query.distinct(true);
        Join<Project, User> projectUserJoin = root.join("pm", JoinType.LEFT);
//        Join<Project, ProjectAssignment> projectAssignmentJoin = root.join("assignments", JoinType.LEFT);

        if (this.isSearchPm) {
            return buildPredicate(projectUserJoin.get(criteria.getKey()), builder, criteria);
        } else {
            return buildPredicate(root.get(criteria.getKey()), builder, criteria);
        }
    }

}