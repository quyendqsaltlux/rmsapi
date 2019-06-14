package saltlux.ctv.tranSS.payload.po;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.validation.constraints.DecimalMax;
import javax.validation.constraints.DecimalMin;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Size;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class POResponse {
    private Long id;
    private String code;
    private String currency;
    private String status;
    private String company;
    private POAssignment assignment;
}
