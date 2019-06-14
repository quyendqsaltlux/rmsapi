package saltlux.ctv.tranSS.payload.projectAssignment;

import lombok.Getter;
import lombok.Setter;
import saltlux.ctv.tranSS.payload.evaluation.PlainSpecificCommentResponse;
import saltlux.ctv.tranSS.payload.project.ProjectLinkAssignmentResponse;
import saltlux.ctv.tranSS.payload.resource.CandidateBasicResponse;

import javax.validation.constraints.DecimalMax;
import javax.validation.constraints.DecimalMin;
import java.math.BigDecimal;
import java.time.Instant;
import java.util.Date;

@Getter
@Setter
public class ProjectHistoryResponse {
    private Long id;
    private String task;
    private String status;
    private String progress;
    private String review;
    private Integer star;
    private CandidateBasicResponse candidate;
    private Instant createdAt;
    private Instant updatedAt;
    private Date ho;
    private Date hb;
    private String projectCode;
    private Long projectId;
    private String source;
    private String target;
    private Long abilityId;
    private ProjectLinkAssignmentResponse project;
    private PlainSpecificCommentResponse specificComment;

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
