package saltlux.ctv.tranSS.repository.evaluation;

import org.springframework.stereotype.Repository;
import saltlux.ctv.tranSS.Specification.SearchCriteria;
import saltlux.ctv.tranSS.model.SpecificComment;
import saltlux.ctv.tranSS.payload.BaseFilterRequest;
import saltlux.ctv.tranSS.payload.PagedResponse;
import saltlux.ctv.tranSS.payload.common.FilterRequest;
import saltlux.ctv.tranSS.util.RepoUtil;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.TypedQuery;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;
import java.util.List;


@Repository
public class SpecificCommentRepositoryImpl implements SpecificCommentRepositoryCustom {
    @PersistenceContext
    private
    EntityManager em;

    @Override
    public PagedResponse<SpecificComment> searchComment(int page, int size, String keyWord, String orderBy,
                                                        String sortDirection, BaseFilterRequest filters, Long candidateId) {
        CriteriaQuery<SpecificComment> criteriaQuery = buildSearchQuery(SpecificComment.class, candidateId, filters);
        TypedQuery<SpecificComment> query = em.createQuery(criteriaQuery);
        query.setFirstResult(page * size);
        query.setMaxResults(size);
        List<SpecificComment> assignments = query.getResultList();

        CriteriaQuery<Long> countQuery = buildCountSearchQuery(Long.class, candidateId, filters);
        int total = Math.toIntExact(em.createQuery(countQuery).getSingleResult());
        int pageCount = (total / size + 1);

        return new PagedResponse<>(assignments, page, size, total, pageCount, page == pageCount);
    }

    private <T> CriteriaQuery<T> buildSearchQuery(Class<T> clazz, Long candidateId, BaseFilterRequest filters) {
        CriteriaBuilder cb = em.getCriteriaBuilder();
        CriteriaQuery<T> criteriaQuery = cb.createQuery(clazz);
        Root<SpecificComment> root = criteriaQuery.from(SpecificComment.class);
        criteriaQuery.where(cb.equal(root.get("assignment").get("candidate").get("id"), candidateId));
        Predicate aggregatedPredicate = RepoUtil.buildPredicate2(filters, cb, root);
        Predicate projectPredicate = buildProjectPredicate(filters, cb, root);
        if (aggregatedPredicate != null) {
            criteriaQuery.where(aggregatedPredicate);
        }
        if (projectPredicate != null) {
            criteriaQuery.where(projectPredicate);
        }

        return criteriaQuery;
    }

    private <T> CriteriaQuery<T> buildCountSearchQuery(Class<T> clazz, Long candidateId, BaseFilterRequest filters) {
        CriteriaBuilder cb = em.getCriteriaBuilder();
        CriteriaQuery<T> criteriaQuery = cb.createQuery(clazz);
        Root<SpecificComment> root = criteriaQuery.from(SpecificComment.class);
        criteriaQuery.multiselect(
                cb.count(root)
        );
        criteriaQuery.where(cb.equal(root.get("assignment").get("candidate").get("id"), candidateId));
        Predicate aggregatedPredicate = RepoUtil.buildPredicate2(filters, cb, root);
        Predicate projectPredicate = buildProjectPredicate(filters, cb, root);
        if (aggregatedPredicate != null) {
            criteriaQuery.where(aggregatedPredicate);
        }
        if (projectPredicate != null) {
            criteriaQuery.where(projectPredicate);
        }

        return criteriaQuery;
    }

    public static <T> Predicate buildProjectPredicate(BaseFilterRequest filters, CriteriaBuilder cb, Root<T> root) {
        SearchCriteria searchCriteria;
        Predicate aggregatePredicate = null;
        Predicate predicate;
        for (FilterRequest filter : filters.getJoinFilters()) {
            searchCriteria = new SearchCriteria(filter.getField(), filter.getOperation(), filter.getValue());
            predicate = RepoUtil
                    .buildPredicate(
                            root.get("assignment").get("project").get(searchCriteria.getKey()), cb, searchCriteria);
            aggregatePredicate = aggregatePredicate == null ? predicate : cb.and(aggregatePredicate, predicate);
        }
        return aggregatePredicate;
    }
}
