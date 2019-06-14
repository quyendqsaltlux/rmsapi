package saltlux.ctv.tranSS.payload.projectAssignment;

import lombok.Getter;
import lombok.Setter;
import saltlux.ctv.tranSS.payload.resource.AbilityBasicResponse;
import saltlux.ctv.tranSS.payload.resource.CandidateWithAbilityResponse;

import javax.validation.constraints.DecimalMax;
import javax.validation.constraints.DecimalMin;
import javax.validation.constraints.NotNull;
import java.math.BigDecimal;
import java.time.Instant;
import java.util.Date;

@Getter
@Setter
public class ProjectAssignmentCandidateResponse {
    private Long id;
    /**
     * Sometime PM want to select task that is not defined for candidate in RDB
     */
    private Boolean useCustomTask;
    /**
     * PM want to assign candidate not in RDB
     */
    private Boolean externalResource;
    private String externalResourceName;
    private String task;
    private String status;
    private String progress;
    private String review;
    private Integer star;
    private CandidateWithAbilityResponse candidate;
    private Instant createdAt;
    private Instant updatedAt;
    private Date ho;
    private Date hb;
    private String projectCode;
    private Long projectId;
    private String source;
    private String target;
    private Long abilityId;
    private AbilityBasicResponse ability;

    private String candidateCode;

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

    private Long poId;
}
