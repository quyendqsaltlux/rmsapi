package saltlux.ctv.tranSS.payload.invoice;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import saltlux.ctv.tranSS.payload.po.POResponse;
import saltlux.ctv.tranSS.payload.po.PoProjectAssignmentResponse;

import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
@NoArgsConstructor
public class InvoiceFormResponse extends InvoiceBasicResponse {
    private List<POResponse> purchaseOrders = new ArrayList<>();
}
