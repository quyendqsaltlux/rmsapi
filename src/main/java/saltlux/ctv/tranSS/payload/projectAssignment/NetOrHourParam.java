package saltlux.ctv.tranSS.payload.projectAssignment;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.validation.constraints.NotNull;
import java.math.BigDecimal;
import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class NetOrHourParam {
    @NotNull
    private java.util.List<Integer> rep;
    @NotNull
    private List<BigDecimal> wf;
}
