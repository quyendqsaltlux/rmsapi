package saltlux.ctv.tranSS.repository.candidate;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;

public class CandidateRepositoryImpl implements CandidateRepositoryCustom {
    @PersistenceContext
    private
    EntityManager entityManager;

    @Override
    public Long count_Columns(String columnName, String columnValue, Long candidateId) {
        Query query = candidateId == null ?
                entityManager.createQuery("SELECT COUNT(c) FROM Candidate c WHERE c." + columnName + "=:name") :
                entityManager.createQuery("SELECT COUNT(c) FROM Candidate c WHERE c." + columnName + "=:name AND c.id<>:id");
        query.setParameter("name", columnValue);
        if (candidateId != null) {
            query.setParameter("id", candidateId);
        }

        return (Long) query.getSingleResult();
    }
}
