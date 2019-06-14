package saltlux.ctv.tranSS.repository.po;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import saltlux.ctv.tranSS.model.Candidate;
import saltlux.ctv.tranSS.model.PurchaseOrder;

import java.util.List;
import java.util.Optional;

public interface PORepository extends JpaRepository<PurchaseOrder, Long>, PORepositoryCustom,
        JpaSpecificationExecutor<Candidate> {

    Optional<PurchaseOrder> findById(Long id);

    Optional<PurchaseOrder> findByCode(String code);

    Optional<PurchaseOrder> findTopByAssignmentId(Long assignmentId);

    @Query("SELECT MAX(u.code) FROM PurchaseOrder u WHERE u.company=:company AND u.code like CONCAT(:pmCode,'%')")
    List<String> getMaxCodeByCompany(@Param("company") String company, @Param("pmCode") String pmCode);

    @Query("SELECT u FROM PurchaseOrder u WHERE u.invoice.id=:invoiceId")
    List<PurchaseOrder> findByInvoice(@Param("invoiceId") Long invoiceId);

}