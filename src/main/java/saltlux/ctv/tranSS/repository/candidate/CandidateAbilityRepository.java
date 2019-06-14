package saltlux.ctv.tranSS.repository.candidate;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import saltlux.ctv.tranSS.model.CandidateAbility;

import java.util.List;
import java.util.Optional;


@Repository
public interface CandidateAbilityRepository extends JpaRepository<CandidateAbility, Long>, CandidateAbilityRepositoryCustom,
        JpaSpecificationExecutor<CandidateAbility> {

    Optional<CandidateAbility> findById(Long id);

    @Query("SELECT u FROM CandidateAbility u WHERE u.candidate.id =:candidateId ORDER BY u.id DESC")
    List<CandidateAbility> findByCandidate(@Param("candidateId") Long candidateId);

    @Query("SELECT u FROM CandidateAbility u WHERE u.candidate.id =:candidateId AND u.task =:task AND " +
            "u.sourceLanguage =:sourceLanguage AND u.targetLanguage =:targetLanguage ORDER BY u.id DESC")
    List<CandidateAbility> findTopByCandidateAndTaskAndSourceLanguageAndTargetLanguage(
            @Param("candidateId") Long candidateId,
            @Param("task") String task,
            @Param("sourceLanguage") String sourceLanguage,
            @Param("targetLanguage") String targetLanguage
    );

}
