package saltlux.ctv.tranSS.repository.evaluation;

import org.springframework.stereotype.Repository;
import saltlux.ctv.tranSS.model.GeneralComment;
import saltlux.ctv.tranSS.payload.BaseFilterRequest;
import saltlux.ctv.tranSS.payload.PagedResponse;
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
public class GeneralCommentRepositoryImpl implements GeneralCommentRepositoryCustom {
    @PersistenceContext
    private
    EntityManager em;

    @Override
    public PagedResponse<GeneralComment> searchComment(int page, int size, String keyWord, String orderBy,
                                                       String sortDirection, BaseFilterRequest filters, Long candidateId) {
        CriteriaQuery<GeneralComment> criteriaQuery = buildSearchQuery(GeneralComment.class, candidateId, filters);
        TypedQuery<GeneralComment> query = em.createQuery(criteriaQuery);
        query.setFirstResult(page * size);
        query.setMaxResults(size);
        List<GeneralComment> assignments = query.getResultList();

        CriteriaQuery<Long> countQuery = buildCountSearchQuery(Long.class, candidateId, filters);
        int total = Math.toIntExact(em.createQuery(countQuery).getSingleResult());
        int pageCount = (total / size + 1);

        return new PagedResponse<>(assignments, page, size, total, pageCount, page == pageCount);
    }

    private <T> CriteriaQuery<T> buildSearchQuery(Class<T> clazz, Long candidateId, BaseFilterRequest filters) {
        CriteriaBuilder cb = em.getCriteriaBuilder();
        CriteriaQuery<T> criteriaQuery = cb.createQuery(clazz);
        Root<GeneralComment> root = criteriaQuery.from(GeneralComment.class);
        criteriaQuery.where(cb.equal(root.get("candidate").get("id"), candidateId));
        Predicate aggregatedPredicate = RepoUtil.buildPredicate2(filters, cb, root);
        if (aggregatedPredicate != null) {
            criteriaQuery.where(aggregatedPredicate);
        }

        return criteriaQuery;
    }

    private <T> CriteriaQuery<T> buildCountSearchQuery(Class<T> clazz, Long candidateId, BaseFilterRequest filters) {
        CriteriaBuilder cb = em.getCriteriaBuilder();
        CriteriaQuery<T> criteriaQuery = cb.createQuery(clazz);
        Root<GeneralComment> root = criteriaQuery.from(GeneralComment.class);
        criteriaQuery.multiselect(
                cb.count(root)
        );
        criteriaQuery.where(cb.equal(root.get("candidate").get("id"), candidateId));
        Predicate aggregatePredicate = RepoUtil.buildPredicate2(filters, cb, root);
        if (aggregatePredicate != null) {
            criteriaQuery.where(aggregatePredicate);
        }

        return criteriaQuery;
    }

}
