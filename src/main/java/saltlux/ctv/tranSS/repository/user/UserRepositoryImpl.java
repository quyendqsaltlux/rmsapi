package saltlux.ctv.tranSS.repository.user;

import saltlux.ctv.tranSS.model.Role;
import saltlux.ctv.tranSS.model.RoleName;
import saltlux.ctv.tranSS.model.User;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.TypedQuery;
import javax.persistence.criteria.*;
import java.util.List;

public class UserRepositoryImpl implements UserRepositoryCustom {
    @PersistenceContext
    private EntityManager entityManager;

    @Override
    public List<User> findBy_UsernameRole(String username, RoleName role, int limit) {
        CriteriaBuilder criteriaBuilder = entityManager.getCriteriaBuilder();

        CriteriaQuery<User> builderQuery = criteriaBuilder.createQuery(User.class);
        Root<User> userRoot = builderQuery.from(User.class);
        Join<User, Role> userRoleJoin = userRoot.join("roles", JoinType.LEFT);
        Predicate predicate = criteriaBuilder.like(userRoot.get("username"), "%" + username + "%");
        Predicate predicate2 = criteriaBuilder.equal(userRoleJoin.get("name"), role);
        Predicate finalPredicate
                = criteriaBuilder.and(predicate, predicate2);
        builderQuery.where(finalPredicate);
        TypedQuery<User> query = entityManager.createQuery(builderQuery);
        query.setMaxResults(limit);
        return query.getResultList();
    }
}
