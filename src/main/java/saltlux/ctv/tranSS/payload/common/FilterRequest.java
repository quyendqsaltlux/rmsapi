package saltlux.ctv.tranSS.payload.common;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class FilterRequest {
    private String field;
    private String operation;
    private Object value;

    public FilterRequest(String field, String operation, Object value) {
        this.field = field;
        this.operation = operation;
        this.value = value;
    }
}

