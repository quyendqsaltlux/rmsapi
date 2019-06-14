package saltlux.ctv.tranSS.payload.common;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class MySort {
    private String field;
    private String order;

    public MySort(String field, String order) {
        this.field = field;
        this.order = order;
    }

    public boolean isValid() {
        return null != field && null != order && !field.isEmpty() && !order.isEmpty();
    }

    public boolean emptyData(){
        return null == field || null == order;
    }
}
