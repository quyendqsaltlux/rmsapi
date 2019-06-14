package saltlux.ctv.tranSS.payload;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.validation.constraints.Size;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class UserProfile {
    private Long id;
    private String username;
    private String code;
    @Size(max = 255)
    private String name;
    @Size(max = 255)
    private String email;
    @Size(max = 255)
    private String tel;
    @Size(max = 255)
    private String avatar;
}
