package saltlux.ctv.tranSS.util;

import org.springframework.security.core.GrantedAuthority;
import saltlux.ctv.tranSS.model.RoleName;
import saltlux.ctv.tranSS.security.UserPrincipal;

import java.util.Collection;

public class AuthUtil {
    public static boolean isPmLeader(UserPrincipal currentUser) {
        Collection<? extends GrantedAuthority> authorities = currentUser.getAuthorities();
        for (GrantedAuthority authority : authorities) {
            if (authority.getAuthority().equals(RoleName.ROLE_PM_LEADER.toString())) {
                return true;
            }
        }
        return false;
    }
}
