package saltlux.ctv.tranSS.payload.resource;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.validation.constraints.DecimalMax;
import javax.validation.constraints.DecimalMin;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Size;
import java.math.BigDecimal;

@Getter
@Setter
@NoArgsConstructor
public class CandidateAbilityRequest {
    private Long id;
    @Size(max = 255)
    @NotBlank
    private String sourceLanguage;
    @NotBlank
    @Size(max = 255)
    private String targetLanguage;
    @NotBlank
    @Size(max = 16)
    private String projectType;
    @NotBlank
    @Size(max = 255)
    private String task;
    @DecimalMin("0.0")
    private BigDecimal rate;
    @Size(max = 255)
    private String rateUnit;
    @DecimalMin("0.0")
    private BigDecimal rate2;
    @Size(max = 255)
    private String rate2unit;
    @DecimalMin("0.0")
    private BigDecimal minimumCharge;
    @DecimalMin("0.0")
    private Integer minimumVolum;
    @DecimalMin("0.0")
    @DecimalMax("100.0")
    private BigDecimal wrep;
    @DecimalMin("0.0")
    @DecimalMax("100.0")
    private BigDecimal w100;
    @DecimalMin("0.0")
    @DecimalMax("100.0")
    private BigDecimal w99_95;
    @DecimalMin("0.0")
    @DecimalMax("100.0")
    private BigDecimal w94_85;
    @DecimalMin("0.0")
    @DecimalMax("100.0")
    private BigDecimal w84_75;
    @DecimalMin("0.0")
    @DecimalMax("100.0")
    private BigDecimal wnoMatch;
    @Size(max = 255)
    private String dailyCapacity;

    private Long candidateId;
    @Size(max = 512)
    private String note;
//    @Size(max = 3)
//    private String currency;

}