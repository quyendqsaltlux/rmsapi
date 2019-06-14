package saltlux.ctv.tranSS.payload.resource;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.validation.constraints.DecimalMin;

@Getter
@Setter
@NoArgsConstructor
public class AbilitySearchResponse {
    private Long id;
    private String sourceLanguage;
    private String targetLanguage;
    private String projectType;
    private String task;
    private Float rate;
    private String rateUnit;
    private Float rate2;
    private String rate2unit;
    private Float minimumCharge;
    private Float minimumVolum;
    private Float wrep;
    private Float w100;
    private Float w99_95;
    private Float w94_85;
    private Float w84_75;
    private Float wnoMatch;
    private String dailyCapacity;
    private String note;
//    private String currency;

    private Integer evaluateCount;
    private Integer evaluateTotal;
    private Integer evaluateAvg;
    private CandidateBasicResponse candidate;

}