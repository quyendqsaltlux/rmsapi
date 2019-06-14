package saltlux.ctv.tranSS.payload.invoice;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;
import java.util.Date;
import java.util.List;

@Getter
@Setter
@NoArgsConstructor
public class InvoiceRequest {
    private Long id;
    private String company;
    private Date dateOfInvoice;
    private Long candidateId;
    private List<String> purchaseOrders;
    private String bankName;
    private String account;
    private String depositor;
    private String swiftCode;
    private String payPal;
    private String email;
    private String mobile;
    private String address;
    private String resourceName;
    private BigDecimal total;
    private String currency;

}
