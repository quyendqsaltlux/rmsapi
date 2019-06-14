package saltlux.ctv.tranSS.payload.po;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import saltlux.ctv.tranSS.payload.employee.Employee;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class POProject {
    private Long id;
    private String code;
    private String company;
    private String name;
    private POPm pm;
    private Employee pmVtc;

}
