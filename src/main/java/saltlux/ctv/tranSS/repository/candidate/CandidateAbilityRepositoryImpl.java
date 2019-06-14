package saltlux.ctv.tranSS.repository.candidate;

import saltlux.ctv.tranSS.Specification.SearchCriteria;
import saltlux.ctv.tranSS.model.CandidateAbility;
import saltlux.ctv.tranSS.payload.BaseFilterRequest;
import saltlux.ctv.tranSS.payload.PagedResponse;
import saltlux.ctv.tranSS.payload.common.FilterRequest;
import saltlux.ctv.tranSS.payload.common.MySort;
import saltlux.ctv.tranSS.payload.resource.CandidateAbilitySearchRequest;
import saltlux.ctv.tranSS.util.RepoUtil;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.TypedQuery;
import javax.persistence.criteria.*;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static saltlux.ctv.tranSS.util.RepoUtil.getLastStringAfterDot;


public class CandidateAbilityRepositoryImpl implements CandidateAbilityRepositoryCustom {
    @PersistenceContext
    private
    EntityManager em;

    private final String[] JOIN_SEARCH_FIELDS = new String[]{
            "candidate.code", "candidate.grade",
            "candidate.name", "candidate.majorField",
            "candidate.email", "candidate.mobile", "candidate.catTool",
            "candidate.messenger", "candidate.socialpages",
            "candidate.personalId", "candidate.gender", "candidate.dateOfBirth",
            "candidate.country", "candidate.address", "candidate.currency"};

    @Override
    public PagedResponse<CandidateAbility> search(int page, int size, String keyWord, CandidateAbilitySearchRequest search) {
        BaseFilterRequest filters = search.getFilter();
        List<MySort> sorts = search.getSorts();
        CriteriaQuery<CandidateAbility> criteriaQuery = buildSearchQuery(CandidateAbility.class, filters, sorts);
        TypedQuery<CandidateAbility> query = em.createQuery(criteriaQuery);
        query.setFirstResult(page * size);
        query.setMaxResults(size);
        List<CandidateAbility> assignments = query.getResultList();

        CriteriaQuery<Long> countQuery = buildCountSearchQuery(Long.class, filters);
        int total = em.createQuery(countQuery).getSingleResult().intValue();
        int pageCount = (total / size + 1);

        return new PagedResponse<>(assignments, page, size, total, pageCount, page == pageCount);
    }

    /**
     * @param clazz   clazz
     * @param filters filters
     * @param sorts   sorts
     * @param <T>     <T>
     * @return CriteriaQuery<T>
     */
    private <T> CriteriaQuery<T> buildSearchQuery(Class<T> clazz, BaseFilterRequest filters, List<MySort> sorts) {
        CriteriaBuilder cb = em.getCriteriaBuilder();
        CriteriaQuery<T> criteriaQuery = cb.createQuery(clazz);
        Root<CandidateAbility> root = criteriaQuery.from(CandidateAbility.class);

        Predicate aggregatedPredicate = buildPredicate(filters, cb, root);
        if (aggregatedPredicate != null) {
            criteriaQuery.where(aggregatedPredicate);
        }
        Order nameOrder = cb.asc(root.get("candidate").get("id"));
        List<Order> orders = new ArrayList<>();
        orders.add(nameOrder);
        orders.addAll(buildOrders(sorts, root, cb));
        Order evaluateOrder = cb.desc(root.get("evaluateAvg"));
        orders.add(evaluateOrder);
        criteriaQuery.orderBy(orders);

        return criteriaQuery;
    }

    /**
     * @param sorts sorts
     * @param root  root
     * @param cb    cb
     * @return List<Order>
     */
    private List<Order> buildOrders(List<MySort> sorts, Root<CandidateAbility> root, CriteriaBuilder cb) {
        List<Order> orders = new ArrayList<>();
        for (MySort sort : sorts) {
            if (null == sort || sort.emptyData()) {
                continue;
            }
            boolean isJoinField = Arrays.asList(JOIN_SEARCH_FIELDS).contains(sort.getField());
            boolean isAsc = "asc".equals(sort.getOrder());
            boolean isDesc = "desc".equals(sort.getOrder());
            if (isAsc) {
                if (isJoinField) {
                    orders.add(cb.asc(root.get("candidate").get(getLastStringAfterDot(sort.getField()))));
                    continue;
                }
                orders.add(cb.asc(root.get(sort.getField())));
            } else if (isDesc) {
                if (isJoinField) {
                    orders.add(cb.desc(root.get("candidate").get(getLastStringAfterDot(sort.getField()))));
                    continue;
                }
                orders.add(cb.desc(root.get(sort.getField())));
            }
        }

        return orders;
    }

    /**
     * @param clazz   clazz
     * @param filters filters
     * @param <T>     <T>
     * @return CriteriaQuery<T>
     */
    private <T> CriteriaQuery<T> buildCountSearchQuery(Class<T> clazz, BaseFilterRequest filters) {
        CriteriaBuilder cb = em.getCriteriaBuilder();
        CriteriaQuery<T> criteriaQuery = cb.createQuery(clazz);
        Root<CandidateAbility> root = criteriaQuery.from(CandidateAbility.class);

        criteriaQuery.multiselect(
                cb.count(root)
        );
        Predicate aggregatePredicate = buildPredicate(filters, cb, root);
        if (aggregatePredicate != null) {
            criteriaQuery.where(aggregatePredicate);
        }

        return criteriaQuery;
    }

    /**
     * @param filters filters
     * @param cb      cb
     * @param root    root
     * @return Predicate
     */
    private Predicate buildPredicate(BaseFilterRequest filters, CriteriaBuilder cb,
                                     Root<CandidateAbility> root) {
        SearchCriteria searchCriteria;
        Predicate aggregatePredicate = null;
        Predicate predicate;
        for (FilterRequest filter : filters.getRootFilters()) {
            searchCriteria = new SearchCriteria(filter.getField(), filter.getOperation(), filter.getValue());
            predicate = RepoUtil.buildPredicate(root.get(searchCriteria.getKey()), cb, searchCriteria);
            aggregatePredicate = aggregatePredicate == null ? predicate : cb.and(aggregatePredicate, predicate);
        }
        for (FilterRequest filter : filters.getJoinFilters()) {
            searchCriteria = new SearchCriteria(filter.getField(), filter.getOperation(), filter.getValue());
            predicate = RepoUtil.buildPredicate(root.get("candidate").get(searchCriteria.getKey()), cb, searchCriteria);
            aggregatePredicate = aggregatePredicate == null ? predicate : cb.and(aggregatePredicate, predicate);
        }
        return aggregatePredicate;
    }
}
