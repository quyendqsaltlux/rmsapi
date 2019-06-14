package saltlux.ctv.tranSS.repository.evaluation;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import saltlux.ctv.tranSS.model.GeneralComment;
import saltlux.ctv.tranSS.model.OtherNoteComment;
import saltlux.ctv.tranSS.model.SpecificComment;

import java.util.List;
import java.util.Optional;


@Repository
public interface OtherNoteCommentRepository extends JpaRepository<OtherNoteComment, Long>, OtherNoteCommentRepositoryCustom,
        JpaSpecificationExecutor<OtherNoteComment> {

    Optional<OtherNoteComment> findById(Long id);
}
