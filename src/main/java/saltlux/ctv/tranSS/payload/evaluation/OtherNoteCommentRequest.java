package saltlux.ctv.tranSS.payload.evaluation;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.validation.constraints.Size;

@Getter
@Setter
@NoArgsConstructor
public class OtherNoteCommentRequest {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Size(max = 1023)
    private String source;
    @Size(max = 1023)
    private String target;
    @Size(max = 1023)
    private String corrected;
    @Size(max = 1023)
    private String comment;
    @Size(max = 255)
    private String project;

}
