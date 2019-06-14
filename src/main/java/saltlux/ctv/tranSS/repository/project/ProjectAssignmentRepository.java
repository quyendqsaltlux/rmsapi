package saltlux.ctv.tranSS.repository.project;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import saltlux.ctv.tranSS.model.Project;
import saltlux.ctv.tranSS.model.ProjectAssignment;

import java.util.List;
import java.util.Optional;


@Repository
public interface ProjectAssignmentRepository extends JpaRepository<ProjectAssignment, Long>, ProjectAssignmentRepositoryCustom,
        JpaSpecificationExecutor<Project> {

    Optional<ProjectAssignment> findById(Long id);

    @Query("SELECT u FROM ProjectAssignment u WHERE u.projectId =:projectId ORDER BY u.id DESC")
    List<ProjectAssignment> findByProjectId(@Param("projectId") Long projectId);

    @Query("SELECT u FROM ProjectAssignment u WHERE u.projectCode =:projectCode ORDER BY u.id DESC")
    List<ProjectAssignment> findByProjectCode(@Param("projectCode") String projectCode);

    @Query("SELECT u FROM ProjectAssignment u WHERE u.candidate.id =:candidateId AND u.progress <> 'FINISHED' ORDER BY u.hb DESC")
    List<ProjectAssignment> findByCandidate(@Param("candidateId") Long candidateId);

}
