package saltlux.ctv.tranSS.repository.project;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;

public class ProjectRepositoryImpl implements ProjectRepositoryCustom {
    @PersistenceContext
    private
    EntityManager entityManager;

    @Override
    public Long count_Columns(String columnName, String columnValue, Long candidateId) {
        Query query = candidateId == null ?
                entityManager.createQuery("SELECT COUNT(c) FROM Project c WHERE c." + columnName + "=:name") :
                entityManager.createQuery("SELECT COUNT(c) FROM Project c WHERE c." + columnName + "=:name AND c.id<>:id");
        query.setParameter("name", columnValue);
        if (candidateId != null) {
            query.setParameter("id", candidateId);
        }

        return (Long) query.getSingleResult();
    }
}
