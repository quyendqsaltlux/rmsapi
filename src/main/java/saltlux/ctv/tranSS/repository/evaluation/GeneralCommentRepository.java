package saltlux.ctv.tranSS.repository.evaluation;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import saltlux.ctv.tranSS.model.GeneralComment;
import saltlux.ctv.tranSS.model.SpecificComment;

import java.util.List;
import java.util.Optional;


@Repository
public interface GeneralCommentRepository extends JpaRepository<GeneralComment, Long>, GeneralCommentRepositoryCustom,
        JpaSpecificationExecutor<SpecificComment> {

    Optional<GeneralComment> findById(Long id);

    @Query("SELECT u FROM GeneralComment u WHERE u.candidate.id =:candidateId ORDER BY u.id DESC")
    List<GeneralComment> findByCandidate(@Param("candidateId") Long candidateId);

}
