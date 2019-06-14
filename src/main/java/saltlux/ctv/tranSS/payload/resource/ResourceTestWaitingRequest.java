package saltlux.ctv.tranSS.payload.resource;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import javax.validation.constraints.Size;
import java.util.Date;

@Getter
@Setter
@NoArgsConstructor
public class ResourceTestWaitingRequest {
    private Long id;
    @Size(max = 32)
    private String code;
    @Size(max = 255)
    private String source;
    @Size(max = 255)
    private String target;
    @Size(max = 255)
    private String name;
    @Size(max = 255)
    private String contact;
    @Size(max = 255)
    private String testContents;
    @Size(max = 255)
    private String tool;
    @Temporal(TemporalType.DATE)
    private Date testInvitation;
    @Temporal(TemporalType.DATE)
    private Date testSending;
    @Temporal(TemporalType.DATE)
    private Date hbReceipt;
    @Size(max = 255)
    private String hbFiles;
    @Size(max = 255)
    private String internalCheck;
    @Size(max = 255)
    private String testEvaluation;
    @Size(max = 255)
    private String testResult;
    @Size(max = 255)
    private String evaluator;
    @Size(max = 255)
    private String comments;
    @Size(max = 255)
    private String otherNote;
    @Size(max = 255)
    private String attachment;

    @Temporal(TemporalType.DATE)
    private Date shortListDate;
    @Temporal(TemporalType.DATE)
    private Date negotiationDate;
    @Size(max = 255)
    private String expectedRateRange;
    @Size(max = 255)
    private String field;
    @Size(max = 63)
    private String processStatus;
    @Size(max = 255)
    private String catTool;

    private Integer isShortList;
}