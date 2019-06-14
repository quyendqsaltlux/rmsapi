package saltlux.ctv.tranSS.payload.po;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.Date;

@Getter
@Setter
@NoArgsConstructor
public class PoProjectAssignmentResponse {
    private String poNo;
    private Long id;
    private Long invoiceId;
    /**
     * Sometime PM want to select task that is not defined for candidate in RDB
     */
    private Boolean useCustomTask;
    /**
     * PM want to assign candidate not in RDB
     */
    private Boolean externalResource;
    private String candidateCode;
    private String resourceName;
    private String task;
    private Date ho;
    private Date hb;
    private String projectCode;
    private Long projectId;
    private String company;
    private POProject project;
    private Long abilityId;
    private String source;
    private String target;
    private String status;
    private String progress;


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
    private String currency;


    private Instant createdAt;
    private Instant updatedAt;
}
