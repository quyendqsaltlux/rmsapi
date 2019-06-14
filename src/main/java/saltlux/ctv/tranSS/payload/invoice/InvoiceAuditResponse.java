package saltlux.ctv.tranSS.payload.invoice;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import saltlux.ctv.tranSS.model.Candidate;
import saltlux.ctv.tranSS.model.PurchaseOrder;
import saltlux.ctv.tranSS.payload.po.PoProjectAssignmentResponse;
import saltlux.ctv.tranSS.payload.resource.CandidateBasicResponse;

import javax.validation.constraints.Size;
import java.time.Instant;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@Getter
@Setter
@NoArgsConstructor
public class InvoiceAuditResponse extends InvoiceBasicResponse {
    private List<PoProjectAssignmentResponse> purchaseOrders = new ArrayList<>();
}
