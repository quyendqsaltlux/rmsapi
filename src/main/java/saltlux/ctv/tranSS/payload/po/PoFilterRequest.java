package saltlux.ctv.tranSS.payload.po;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import saltlux.ctv.tranSS.payload.common.FilterRequest;

import java.util.List;

@Getter
@Setter
@NoArgsConstructor
public class PoFilterRequest {
    private List<FilterRequest> rootFilters;
    private List<FilterRequest> poFilters;
    private List<FilterRequest> projectFilters;
    private List<FilterRequest> candidateFilters;


    public boolean hasNoFilter() {
        return (null == rootFilters || rootFilters.isEmpty()) &&
                (null == poFilters || poFilters.isEmpty()) &&
                (null == projectFilters || projectFilters.isEmpty()) &&
                (null == candidateFilters || candidateFilters.isEmpty());
    }
}
