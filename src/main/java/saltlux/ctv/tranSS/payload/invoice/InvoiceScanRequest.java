package saltlux.ctv.tranSS.payload.invoice;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class InvoiceScanRequest {
    private String resourceCode;
    private String externalResourceName;
    private String company;

    public boolean invalid() {
        return null == resourceCode && null == externalResourceName;
    }
}
