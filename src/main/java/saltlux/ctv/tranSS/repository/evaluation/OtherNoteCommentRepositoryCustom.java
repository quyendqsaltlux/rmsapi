package saltlux.ctv.tranSS.repository.evaluation;

import org.springframework.stereotype.Repository;
import saltlux.ctv.tranSS.model.GeneralComment;
import saltlux.ctv.tranSS.model.OtherNoteComment;
import saltlux.ctv.tranSS.payload.BaseFilterRequest;
import saltlux.ctv.tranSS.payload.PagedResponse;


@Repository
public interface OtherNoteCommentRepositoryCustom {
    PagedResponse<OtherNoteComment> searchComment(int page, int size, String keyWord,
                                                  String orderBy, String sortDirection,
                                                  BaseFilterRequest filters, Long candidateId);

}
