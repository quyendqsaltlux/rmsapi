package saltlux.ctv.tranSS.payload.projectAssignment;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.validation.constraints.*;
import java.math.BigDecimal;
import java.util.Date;

@Getter
@Setter
@NoArgsConstructor
public class ProjectAssignmentRequest {
    private Long id;
    /**
     * Sometime PM want to select task that is not defined for candidate in RDB
     */
    private Boolean useCustomTask;
    /**
     * PM want to assign candidate not in RDB
     */
    private Boolean externalResource;
    @Size(max = 255)
    private String externalResourceName;
//    @NotNull
    private String candidateCode;
    private Long candidateId;
    @NotBlank
    @Size(max = 63)
    private String task;
    private Date ho;
    private Date hb;
    @Size(max = 255)
    @NotBlank
    private String projectCode;
    private Long projectId;
    private Long abilityId;
    @NotBlank
    @Size(max = 63)
    private String source;
    @NotBlank
    @Size(max = 63)
    private String target;
    @Size(max = 31)
    private String status;
    @Size(max = 31)
    private String progress;

    @NotNull
    @DecimalMin("0.0")
    @DecimalMax("100.0")
    private BigDecimal wrep;
    @NotNull
    @DecimalMin("0.0")
    @DecimalMax("100.0")
    private BigDecimal w100;
    @NotNull
    @DecimalMin("0.0")
    @DecimalMax("100.0")
    private BigDecimal w99_95;
    @NotNull
    @DecimalMin("0.0")
    @DecimalMax("100.0")
    private BigDecimal w94_85;
    @NotNull
    @DecimalMin("0.0")
    @DecimalMax("100.0")
    private BigDecimal w84_75;
    @NotNull
    @DecimalMin("0.0")
    @DecimalMax("100.0")
    private BigDecimal wnoMatch;

    @NotNull
    @DecimalMin("0.0")
    @DecimalMax("10000000.0")
    private Integer reprep;
    @NotNull
    @DecimalMin("0.0")
    @DecimalMax("10000000.0")
    private Integer rep100;
    @NotNull
    @DecimalMin("0.0")
    @DecimalMax("10000000.0")
    private Integer rep99_95;
    @NotNull
    @DecimalMin("0.0")
    @DecimalMax("10000000.0")
    private Integer rep94_85;
    @NotNull
    @DecimalMin("0.0")
    @DecimalMax("10000000.0")
    private Integer rep84_75;
    @NotNull
    @DecimalMin("0.0")
    @DecimalMax("10000000.0")
    private Integer repnoMatch;

    /**
     * = reprep + rep100 + rep99_95 + rep94_85 + rep84_75 + repnoMatch
     */
    @NotNull
    private Integer totalRep;
    /**
     * =(reprep*wrep)+(rep100*w100)+(rep99_95*w99_95)+(rep94_85*w94_85)+(rep84_75*w84_75)+(repnoMatch*wnoMatch) or user define
     */
    @NotNull
    private BigDecimal netOrHour;
    private Boolean notAutoComputeNetHour;
    /**
     * = rate of Ability in case netOrHour > minimum volume. otherwise = rate2 of ability
     */
    @NotNull
    private BigDecimal unitPrice;
    /**
     * Total Money
     * = unitPrice * netOrHour
     */
    @NotNull
    private BigDecimal total;
}
