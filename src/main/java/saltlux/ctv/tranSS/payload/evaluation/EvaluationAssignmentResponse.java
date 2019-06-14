package saltlux.ctv.tranSS.payload.evaluation;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import saltlux.ctv.tranSS.payload.project.ProjectsBasicResponse;
import saltlux.ctv.tranSS.payload.resource.CandidateBasicResponse;

import java.time.Instant;
import java.util.Date;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class EvaluationAssignmentResponse {
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
    private Float total;
    private ProjectsBasicResponse project;
}
