package saltlux.ctv.tranSS.payload.evaluation;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.validation.constraints.DecimalMax;
import javax.validation.constraints.DecimalMin;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

@Getter
@Setter
@NoArgsConstructor
public class SpecificCommentRequest {
    private Long id;
    @DecimalMin("1")
    @DecimalMax("10")
    @NotNull
    private Integer star;
    @Size(max = 511)
    private String comment;
    @Size(max = 255)
    private String evaluator;

}
