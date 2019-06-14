package saltlux.ctv.tranSS.payload.project;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import saltlux.ctv.tranSS.model.User;
import saltlux.ctv.tranSS.payload.projectAssignment.ProjectAssignmentCandidateResponse;

import javax.validation.constraints.DecimalMax;
import javax.validation.constraints.DecimalMin;
import java.util.Date;
import java.util.List;

@Getter
@Setter
@NoArgsConstructor
public class ProjectResponse {
    private Long id;
    private String no;
    private Date requestDate;
    private Date dueDate;
    private String dueTime;
    private String category;
    private String code;
    private String folderName;
    private String client;
    private String contents;
    private String reference;
    private String termbase;
    private String instruction;
    private String remark;
    private Float totalVolume;
    private String unit;
    private String target;
    private String progressStatus;
    private Float progressPoint;
    private String pmVtc;
    private Date ho;
    private Date hb;
    private Date reviewSchedule;
    private String reviewResource;
    private Date finalDelivery;
    private String field;
    private String pmCode;
    private User pm;
    private List<ProjectAssignmentCandidateResponse> assignments;

}

