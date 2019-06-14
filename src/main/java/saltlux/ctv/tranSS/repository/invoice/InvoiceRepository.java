package saltlux.ctv.tranSS.repository.invoice;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import saltlux.ctv.tranSS.model.Candidate;
import saltlux.ctv.tranSS.model.Invoice;

import java.util.List;
import java.util.Optional;

public interface InvoiceRepository extends JpaRepository<Invoice, Long>, InvoiceRepositoryCustom,
        JpaSpecificationExecutor<Invoice> {

    Optional<Invoice> findById(Long id);

}