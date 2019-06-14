package saltlux.ctv.tranSS.repository.invoice;

import saltlux.ctv.tranSS.model.Invoice;
import saltlux.ctv.tranSS.payload.PagedResponse;
import saltlux.ctv.tranSS.payload.po.PoFilterRequest;
import saltlux.ctv.tranSS.payload.po.PoProjectAssignmentResponse;
import saltlux.ctv.tranSS.security.UserPrincipal;

import java.util.List;

public interface InvoiceRepositoryCustom {

    PagedResponse<PoProjectAssignmentResponse> search(int page, int size, String keyWord,
                                                      String orderBy, String sortDirection,
                                                      PoFilterRequest filters, UserPrincipal currentUser);

    List<Invoice> findByIsConfirmed(int isConfirmed);
}