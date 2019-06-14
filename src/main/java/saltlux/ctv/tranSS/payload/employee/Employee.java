package saltlux.ctv.tranSS.payload.employee;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class Employee {
    private Long id;
    private String username;
    private String name;
    private String code;
    private String email;
    private String tel;
}
