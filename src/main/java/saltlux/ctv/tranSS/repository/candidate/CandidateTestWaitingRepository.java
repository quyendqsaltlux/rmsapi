package saltlux.ctv.tranSS.repository.candidate;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;
import saltlux.ctv.tranSS.model.ResourceTestWaiting;

import java.util.List;
import java.util.Optional;


@Repository
public interface CandidateTestWaitingRepository extends JpaRepository<ResourceTestWaiting, Long>,
        JpaSpecificationExecutor<ResourceTestWaiting> {

    Optional<ResourceTestWaiting> findById(Long id);

    Optional<ResourceTestWaiting> findByCode(String code);

    List<ResourceTestWaiting> findByName(String name);
}
