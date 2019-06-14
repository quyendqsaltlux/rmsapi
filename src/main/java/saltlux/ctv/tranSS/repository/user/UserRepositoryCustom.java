package saltlux.ctv.tranSS.repository.user;

import saltlux.ctv.tranSS.model.RoleName;
import saltlux.ctv.tranSS.model.User;

import java.util.List;

public interface UserRepositoryCustom {
    List<User> findBy_UsernameRole(String username, RoleName role, int limit);
}
