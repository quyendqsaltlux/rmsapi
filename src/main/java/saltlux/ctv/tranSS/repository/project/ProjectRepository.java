package saltlux.ctv.tranSS.repository.project;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;
import saltlux.ctv.tranSS.model.Project;

import java.util.List;
import java.util.Optional;


@Repository
public interface ProjectRepository extends JpaRepository<Project, Long>, ProjectRepositoryCustom,
        JpaSpecificationExecutor<Project> {

    Optional<Project> findById(Long id);

    List<Project> findByCode(String code);

    Boolean existsByCode(String code);

    @Query("SELECT MAX(u.no) FROM Project u WHERE (u.isWrongCode = 0 OR u.isWrongCode IS NULL) AND (u.isOld = 0 OR u.isOld IS NULL) AND u.no LIKE CONCAT(:department,'%')")
    List<String> getMaxNo(@Param("department") String department);

    @Transactional
    @Modifying
    @Query("DELETE FROM Project WHERE id = :projectId")
    void deleteByProjectId(@Param("projectId") Long projectId);
}
