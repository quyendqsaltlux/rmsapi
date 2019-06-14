package saltlux.ctv.tranSS.payload.evaluation;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.validation.constraints.Size;
import java.time.Instant;
import java.util.Date;

@Getter
@Setter
@NoArgsConstructor
public class GeneralCommentResponse {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Size(max = 511)
    private String comment;
    @Size(max = 255)
    private String evaluator;
    private Date date;

    private Instant createdAt;
    private Instant updatedAt;

//    private CandidateBasicResponse candidate;
}
