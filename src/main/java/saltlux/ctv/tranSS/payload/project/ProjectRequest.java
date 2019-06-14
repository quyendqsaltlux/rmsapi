package saltlux.ctv.tranSS.payload.project;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import saltlux.ctv.tranSS.validation.ProjectProgress;

import javax.persistence.Column;
import javax.validation.constraints.DecimalMax;
import javax.validation.constraints.DecimalMin;
import javax.validation.constraints.Size;
import java.util.Date;

@Getter
@Setter
@NoArgsConstructor
public class ProjectRequest {
    private Long id;
    @Size(max = 255)
    @Column(unique = true)
    private String no;
    private Date requestDate;
    private Date dueDate;
    @Size(max = 255)
    private String dueTime;
    @Size(max = 255)
    private String category;
    @Size(max = 255)
    @Column(unique = true)
    private String code;
    @Size(max = 255)
    private String folderName;
    @Size(max = 255)
    private String client;
    @Size(max = 255)
    private String contents;
    @Size(min = 1, max = 1)
    private String reference;
    @Size(min = 1, max = 1)
    private String termbase;
    @Size(min = 1, max = 1)
    private String instruction;
    @Size(max = 1023)
    private String remark;
    private Float totalVolume;
    @Size(max = 32)
    private String unit;
    @Size(max = 32)
    private String target;
    @Size(max = 127)
    @ProjectProgress
    private String progressStatus;
    @DecimalMax("1.0")
    @DecimalMin("0.0")
    private Float progressPoint;
    @Size(max = 255)
    private String pmVtc;
    private Date ho;
    private Date hb;
    private Date reviewSchedule;
    @Size(max = 255)
    private String suggestedCandidate;
    private Date finalDelivery;
    @Size(max = 127)
    private String company;
    @Size(max = 127)
    private String pmCode;
    @Size(max = 255)
    private String field;
    /*project in pass that imported from file */
    private Boolean isOld;

}

