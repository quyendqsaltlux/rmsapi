package saltlux.ctv.tranSS.repository.evaluation;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import saltlux.ctv.tranSS.model.SpecificComment;

import java.util.Optional;


@Repository
public interface SpecificCommentRepository extends JpaRepository<SpecificComment, Long>, SpecificCommentRepositoryCustom,
        JpaSpecificationExecutor<SpecificComment> {

    Optional<SpecificComment> findById(Long id);

    @Query("SELECT u FROM SpecificComment u WHERE u.assignment.id =:assignmentId ORDER BY u.id DESC")
    Optional<SpecificComment> findTopByAssignment(@Param("assignmentId") Long assignmentId);

}
