package saltlux.ctv.tranSS.util;

import saltlux.ctv.tranSS.Specification.SearchCriteria;
import saltlux.ctv.tranSS.model.Candidate;
import saltlux.ctv.tranSS.payload.BaseFilterRequest;
import saltlux.ctv.tranSS.payload.common.FilterRequest;

import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.Path;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

public class RepoUtil {
    /**
     * @param path
     * @param builder
     * @param criteria
     * @return
     */
    public static Predicate buildPredicate(Path path, CriteriaBuilder builder, SearchCriteria criteria) {
        String operation = criteria.getOperation();
        String valueString = String.valueOf(criteria.getValue());

        if (operation.equalsIgnoreCase(">")) {
            return builder.greaterThanOrEqualTo(path, valueString);
        } else if (operation.equalsIgnoreCase("<")) {
            return builder.lessThanOrEqualTo(path, valueString);
        } else if (operation.equalsIgnoreCase(":")) {
            if (path.getJavaType() == Candidate.class) {
                return builder.equal(path.get("id"), valueString);
            }
            if (path.getJavaType() == String.class) {
                return builder.like(path, "%" + valueString + "%");
            } else if (path.getJavaType() == Date.class) {
                try {
                    Date dateVal = new SimpleDateFormat("yyyy-MM-dd").parse(valueString);
                    return builder.equal(path, dateVal);
                } catch (ParseException e) {
                    e.printStackTrace();
                }
                return null;
            } else {
                return builder.equal(path, valueString);
            }
        }

        return null;
    }

    /**
     * @param filters filters
     * @param cb      cb
     * @param root    root
     * @param <T>     <T>
     * @return Predicate
     */
    public static <T> Predicate buildPredicate2(BaseFilterRequest filters, CriteriaBuilder cb, Root<T> root) {
        SearchCriteria searchCriteria;
        Predicate aggregatePredicate = null;
        Predicate predicate;
        for (FilterRequest filter : filters.getRootFilters()) {
            searchCriteria = new SearchCriteria(filter.getField(), filter.getOperation(), filter.getValue());
            predicate = buildPredicate(root.get(searchCriteria.getKey()), cb, searchCriteria);
            aggregatePredicate = aggregatePredicate == null ? predicate : cb.and(aggregatePredicate, predicate);
        }
        return aggregatePredicate;
    }

    public static <T> Predicate buildPredicateByFilters(List<FilterRequest> filters, CriteriaBuilder cb, Path<T> path) {
        SearchCriteria searchCriteria;
        Predicate aggregatePredicate = null;
        Predicate predicate;
        for (FilterRequest filter : filters) {
            searchCriteria = new SearchCriteria(filter.getField(), filter.getOperation(), filter.getValue());
            predicate = RepoUtil
                    .buildPredicate(path.get(searchCriteria.getKey()), cb, searchCriteria);
            aggregatePredicate = aggregatePredicate == null ? predicate : cb.and(aggregatePredicate, predicate);
        }
        return aggregatePredicate;
    }

    /**
     * @param stringWithDot a.b
     * @return b
     */
    public static String getLastStringAfterDot(String stringWithDot) {
        if (null == stringWithDot) {
            return null;
        }
        String[] strings = stringWithDot.split("\\.");
        if (strings.length == 0) {
            return null;
        }
        return strings[strings.length - 1];
    }
}
