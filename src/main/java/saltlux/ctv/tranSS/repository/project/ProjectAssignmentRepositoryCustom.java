package saltlux.ctv.tranSS.repository.project;

import org.springframework.stereotype.Repository;
import saltlux.ctv.tranSS.model.ProjectAssignment;
import saltlux.ctv.tranSS.payload.BaseFilterRequest;
import saltlux.ctv.tranSS.payload.PagedResponse;


@Repository
public interface ProjectAssignmentRepositoryCustom {
    PagedResponse<ProjectAssignment> search(int page, int size, String keyWord,
                                            String orderBy, String sortDirection, BaseFilterRequest filters, Long candidateId);

}
