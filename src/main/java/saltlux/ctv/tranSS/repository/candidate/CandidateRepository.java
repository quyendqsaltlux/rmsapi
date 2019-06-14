package saltlux.ctv.tranSS.repository.candidate;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import saltlux.ctv.tranSS.model.Candidate;

import java.util.List;
import java.util.Optional;


@Repository
public interface CandidateRepository extends JpaRepository<Candidate, Long>, CandidateRepositoryCustom,
        JpaSpecificationExecutor<Candidate> {

    Optional<Candidate> findById(Long id);

    Optional<Candidate> findByCode(String code);

    List<Candidate> findByNameContains(String code);

    @Query("SELECT u FROM Candidate u WHERE u.name LIKE CONCAT('%',:name,'%')")
    List<Candidate> findTop100ByCodeOrNameContains(@Param("name") String name);

    @Query("SELECT MAX(u.code) FROM Candidate u WHERE u.code LIKE CONCAT('%',:codeType,'%')")
    List<String> getMaxCode(@Param("codeType") String codeType);

}
