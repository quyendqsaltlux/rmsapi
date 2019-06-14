package saltlux.ctv.tranSS.payload.evaluation;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class SpecificCommentResponse {
    private Long id;
    private Integer star;
    private String comment;
    private String evaluator;
    private EvaluationAssignmentResponse assignment;
}
