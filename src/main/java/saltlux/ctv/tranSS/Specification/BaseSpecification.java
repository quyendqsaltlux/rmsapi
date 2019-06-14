package saltlux.ctv.tranSS.Specification;

import lombok.NoArgsConstructor;
import saltlux.ctv.tranSS.model.Candidate;
import saltlux.ctv.tranSS.util.RepoUtil;

import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.Path;
import javax.persistence.criteria.Predicate;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

@NoArgsConstructor
public abstract class BaseSpecification {
    SearchCriteria criteria;

    public BaseSpecification(SearchCriteria criteria) {
        this.criteria = criteria;
    }

    /**
     * not handle boolean value
     *
     * @param path
     * @param builder
     * @param criteria
     * @return
     */
    public static Predicate buildPredicate(Path path, CriteriaBuilder builder, SearchCriteria criteria) {
        return RepoUtil.buildPredicate(path, builder, criteria);
    }
}
