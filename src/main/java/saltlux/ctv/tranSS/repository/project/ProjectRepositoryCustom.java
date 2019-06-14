package saltlux.ctv.tranSS.repository.project;

public interface ProjectRepositoryCustom {
    Long count_Columns(String columnName, String columnValue, Long candidateId);
}
