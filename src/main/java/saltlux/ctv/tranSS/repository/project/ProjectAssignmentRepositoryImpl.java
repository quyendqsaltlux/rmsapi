package saltlux.ctv.tranSS.repository.project;

import saltlux.ctv.tranSS.Specification.SearchCriteria;
import saltlux.ctv.tranSS.model.Candidate;
import saltlux.ctv.tranSS.model.ProjectAssignment;
import saltlux.ctv.tranSS.payload.BaseFilterRequest;
import saltlux.ctv.tranSS.payload.PagedResponse;
import saltlux.ctv.tranSS.payload.common.FilterRequest;
import saltlux.ctv.tranSS.util.RepoUtil;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.TypedQuery;
import javax.persistence.criteria.*;
import java.util.List;

public class ProjectAssignmentRepositoryImpl implements ProjectAssignmentRepositoryCustom {
    @PersistenceContext
    private
    EntityManager em;

    @Override
    public PagedResponse<ProjectAssignment> search(int page, int size, String keyWord, String orderBy,
                                                   String sortDirection, BaseFilterRequest filters, Long candidateId) {
        CriteriaQuery<ProjectAssignment> criteriaQuery = buildSearchQuery(ProjectAssignment.class, candidateId, filters);
        TypedQuery<ProjectAssignment> query = em.createQuery(criteriaQuery);
        query.setFirstResult(page * size);
        query.setMaxResults(size);
        List<ProjectAssignment> assignments = query.getResultList();

        CriteriaQuery<Long> countQuery = buildCountSearchQuery(Long.class, candidateId, filters);
        int total = Math.toIntExact(em.createQuery(countQuery).getSingleResult());
        int pageCount = (total / size + 1);

        return new PagedResponse<>(assignments, page, size, total, pageCount, page == pageCount);
    }

    private <T> CriteriaQuery<T> buildSearchQuery(Class<T> clazz, Long candidateId, BaseFilterRequest filters) {
        CriteriaBuilder cb = em.getCriteriaBuilder();
        CriteriaQuery<T> criteriaQuery = cb.createQuery(clazz);
        Root<ProjectAssignment> root = criteriaQuery.from(ProjectAssignment.class);
        Join<ProjectAssignment, Candidate> join = root.join("candidate", JoinType.LEFT);

        Predicate joinPredicate = cb.equal(join.get("id"), candidateId);
        Predicate filterPredicate = buildPredicate(filters, cb, root);
        Predicate aggregatedPredicate = null == filterPredicate ?
                joinPredicate : cb.and(joinPredicate, filterPredicate);
        criteriaQuery.where(aggregatedPredicate);
        return criteriaQuery;
    }

    private <T> CriteriaQuery<T> buildCountSearchQuery(Class<T> clazz, Long candidateId, BaseFilterRequest filters) {
        CriteriaBuilder cb = em.getCriteriaBuilder();
        CriteriaQuery<T> criteriaQuery = cb.createQuery(clazz);
        Root<ProjectAssignment> root = criteriaQuery.from(ProjectAssignment.class);
        Join<ProjectAssignment, Candidate> join = root.join("candidate", JoinType.LEFT);

        criteriaQuery.multiselect(
                cb.count(root)
        );
        Predicate joinPredicate = cb.equal(join.get("id"), candidateId);
        Predicate filterPredicate = buildPredicate(filters, cb, root);
        Predicate aggregatedPredicate = null == filterPredicate ?
                joinPredicate : cb.and(joinPredicate, filterPredicate);
        criteriaQuery.where(aggregatedPredicate);
        return criteriaQuery;
    }

    private Predicate buildPredicate(BaseFilterRequest filters, CriteriaBuilder cb,
                                     Root<ProjectAssignment> root) {
        SearchCriteria searchCriteria;
        Predicate aggregatePredicate = null;
        Predicate predicate;
        for (FilterRequest filter : filters.getRootFilters()) {
            searchCriteria = new SearchCriteria(filter.getField(), filter.getOperation(), filter.getValue());
            predicate = RepoUtil.buildPredicate(root.get(searchCriteria.getKey()), cb, searchCriteria);
            aggregatePredicate = aggregatePredicate == null ? predicate : cb.and(aggregatePredicate, predicate);
        }
        return aggregatePredicate;
    }
}
