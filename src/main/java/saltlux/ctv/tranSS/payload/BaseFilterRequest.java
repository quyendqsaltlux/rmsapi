package saltlux.ctv.tranSS.payload;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import saltlux.ctv.tranSS.payload.common.FilterRequest;

import java.util.List;

@Getter
@Setter
@NoArgsConstructor
public class BaseFilterRequest {
    private List<FilterRequest> rootFilters;
    private List<FilterRequest> joinFilters;

    public boolean hasNoFilter() {
        return (null == rootFilters || rootFilters.isEmpty()) && (null == joinFilters || joinFilters.isEmpty());
    }
}
