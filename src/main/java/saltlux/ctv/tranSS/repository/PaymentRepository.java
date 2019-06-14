package saltlux.ctv.tranSS.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;
import saltlux.ctv.tranSS.model.Candidate;
import saltlux.ctv.tranSS.model.Payment;

import java.util.Optional;


@Repository
public interface PaymentRepository extends JpaRepository<Payment, Long>,
        JpaSpecificationExecutor<Payment> {

    Optional<Payment> findById(Long id);

//    @Query("SELECT MAX(u.code) FROM Candidate u WHERE u.code LIKE CONCAT('%',:codeType,'%')")
//    List<String> getMaxCode(@Param("codeType") String codeType);

}
