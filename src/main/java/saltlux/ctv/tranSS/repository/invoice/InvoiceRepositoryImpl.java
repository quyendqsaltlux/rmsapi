package saltlux.ctv.tranSS.repository.invoice;

import saltlux.ctv.tranSS.model.Invoice;
import saltlux.ctv.tranSS.payload.PagedResponse;
import saltlux.ctv.tranSS.payload.po.PoFilterRequest;
import saltlux.ctv.tranSS.payload.po.PoProjectAssignmentResponse;
import saltlux.ctv.tranSS.security.UserPrincipal;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;
import java.util.List;

public class InvoiceRepositoryImpl implements InvoiceRepositoryCustom {
    @PersistenceContext
    private
    EntityManager em;

    @Override
    public PagedResponse<PoProjectAssignmentResponse> search(int page, int size, String keyWord, String orderBy,
                                                             String sortDirection, PoFilterRequest filters,
                                                             UserPrincipal currentUser) {
        return null;

    }

    @Override
    public List<Invoice> findByIsConfirmed(int isConfirmed) {

        Query query = 1 != isConfirmed ? em.createQuery("SELECT u FROM Invoice u WHERE u.isConfirmed IS NULL OR u.isConfirmed = 0") :
                em.createQuery("SELECT u FROM Invoice u WHERE u.isConfirmed = 1");
        return query.getResultList();
    }
}
