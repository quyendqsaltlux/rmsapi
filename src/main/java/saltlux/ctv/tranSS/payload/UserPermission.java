package saltlux.ctv.tranSS.payload;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import saltlux.ctv.tranSS.model.Role;

import java.util.HashSet;
import java.util.Set;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class UserPermission {
    private Long id;
    private String name;
    private String code;
    private String username;
    private String email;
    private Set<Role> roles = new HashSet<>();
}
