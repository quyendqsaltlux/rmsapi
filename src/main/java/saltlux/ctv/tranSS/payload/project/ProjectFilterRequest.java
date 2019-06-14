package saltlux.ctv.tranSS.payload.project;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import saltlux.ctv.tranSS.payload.BaseFilterRequest;
import saltlux.ctv.tranSS.payload.common.FilterRequest;

import java.util.List;

@Getter
@Setter
@NoArgsConstructor
public class ProjectFilterRequest extends BaseFilterRequest {
    private List<FilterRequest> assignFilters;

    @Override
    public boolean hasNoFilter() {
        return super.hasNoFilter() && (null == assignFilters || assignFilters.isEmpty());
    }
}
