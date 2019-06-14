package saltlux.ctv.tranSS.payload.po;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;

@Getter
@Setter
@NoArgsConstructor
public class POAbility {
    private Long id;
    private String sourceLanguage;
    private String targetLanguage;
    private String projectType;
    private String task;
    private BigDecimal rate;
    private String rateUnit;
    private BigDecimal rate2;
    private String rate2unit;
    private String minimumCharge;
    private String minimumVolum;
    private BigDecimal wrep;
    private BigDecimal w100;
    private BigDecimal w99_95;
    private BigDecimal w94_85;
    private BigDecimal w84_75;
    private BigDecimal wnoMatch;
    private String dailyCapacity;

    private String note;

}