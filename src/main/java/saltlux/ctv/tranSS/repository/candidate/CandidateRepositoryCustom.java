package saltlux.ctv.tranSS.repository.candidate;

public interface CandidateRepositoryCustom {
    Long count_Columns(String columnName, String columnValue, Long candidateId);
}
