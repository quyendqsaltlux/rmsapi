package saltlux.ctv.tranSS.repository.user;

import saltlux.ctv.tranSS.model.Role;
import saltlux.ctv.tranSS.model.RoleName;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;


@Repository
public interface RoleRepository extends JpaRepository<Role, Long> {
    Optional<Role> findByName(RoleName roleName);
}
