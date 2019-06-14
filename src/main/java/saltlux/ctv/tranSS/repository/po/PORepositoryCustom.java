package saltlux.ctv.tranSS.repository.po;

import saltlux.ctv.tranSS.payload.PagedResponse;
import saltlux.ctv.tranSS.payload.po.PoFilterRequest;
import saltlux.ctv.tranSS.payload.po.PoProjectAssignmentResponse;
import saltlux.ctv.tranSS.security.UserPrincipal;

import java.text.ParseException;
import java.util.List;

public interface PORepositoryCustom {

    PagedResponse<PoProjectAssignmentResponse> search(int page, int size, String keyWord,
                                                      String orderBy, String sortDirection,
                                                      PoFilterRequest filters,
                                                      String pmVtcCode,
                                                      UserPrincipal currentUser);

    List<PoProjectAssignmentResponse> getAllForInvoice(String company,
                                                       String candidateCode,
                                                       String externalResourceName,
                                                       boolean checkInvoiceIsNull) throws ParseException;

    void updateInvoiceId(Long invoiceId);
}