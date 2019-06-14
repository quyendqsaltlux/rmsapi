package saltlux.ctv.tranSS.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import saltlux.ctv.tranSS.model.MigrateData;
import saltlux.ctv.tranSS.model.User;

import java.util.Optional;


@Repository
public interface MigrateRepository extends JpaRepository<MigrateData, Long> {
    Optional<MigrateData> findById(Long id);
    Optional<MigrateData> findByType(String type);
}
