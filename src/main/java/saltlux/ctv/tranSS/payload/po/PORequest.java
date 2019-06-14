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
public class PORequest {
    private Long id;
    @Size(max = 63)
    private String code;
    private String currency;
//    @NotBlank
    @Size(max = 31)
    private String status;
    @Size(max = 2)
    private String company;

}
