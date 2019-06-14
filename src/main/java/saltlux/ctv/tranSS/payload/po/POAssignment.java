package saltlux.ctv.tranSS.payload.po;

import lombok.Getter;
import lombok.Setter;
import saltlux.ctv.tranSS.model.Candidate;
import saltlux.ctv.tranSS.model.Project;
import saltlux.ctv.tranSS.payload.project.ProjectPm;

import javax.validation.constraints.DecimalMax;
import javax.validation.constraints.DecimalMin;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import java.math.BigDecimal;
import java.time.Instant;
import java.util.Date;

@Getter
@Setter
public class POAssignment {
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
    private Instant createdAt;
    private Instant updatedAt;
    private Date ho;
    private Date hb;
    private String projectCode;
    private Long projectId;
    private String source;
    private String target;
    private Long abilityId;
    private POAbility ability;
    private POProject project;
    private POResource candidate;
    private BigDecimal wrep;
    private BigDecimal w100;
    private BigDecimal w99_95;
    private BigDecimal w94_85;
    private BigDecimal w84_75;
    private BigDecimal wnoMatch;

   
    private Integer reprep;
    private Integer rep100;
    private Integer rep99_95;
    private Integer rep94_85;
    private Integer rep84_75;
    private Integer repnoMatch;
    /**
     * = reprep + rep100 + rep99_95 + rep94_85 + rep84_75 + repnoMatch
     */

    private Integer totalRep;
    /**
     * =(reprep*wrep)+(rep100*w100)+(rep99_95*w99_95)+(rep94_85*w94_85)+(rep84_75*w84_75)+(repnoMatch*wnoMatch) or user define
     */
   
    private BigDecimal netOrHour;
    private Boolean notAutoComputeNetHour;
    /**
     * = rate of Ability in case netOrHour > minimum volume. otherwise = rate2 of ability
     */
   
    private BigDecimal unitPrice;
    /**
     * Total Money
     * = unitPrice * netOrHour
     */
   
    private BigDecimal total;
}
