package saltlux.ctv.tranSS.payload.project;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class ProjectPm {
    private Long id;
    private String username;
    private String name;
    private String code;
    private String email;
    private String tel;
}
