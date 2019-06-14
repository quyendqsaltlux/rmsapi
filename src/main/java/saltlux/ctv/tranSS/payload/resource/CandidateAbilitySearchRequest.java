package saltlux.ctv.tranSS.payload.resource;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import saltlux.ctv.tranSS.payload.BaseFilterRequest;
import saltlux.ctv.tranSS.payload.common.MySort;

import javax.validation.constraints.NotNull;
import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class CandidateAbilitySearchRequest {
    @NotNull
    private List<MySort> sorts;
    @NotNull
    private BaseFilterRequest filter;
}
