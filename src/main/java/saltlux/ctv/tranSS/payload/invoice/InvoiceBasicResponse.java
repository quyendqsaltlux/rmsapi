package saltlux.ctv.tranSS.payload.invoice;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import saltlux.ctv.tranSS.payload.resource.CandidateBasicResponse;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.Date;

@Getter
@Setter
@NoArgsConstructor
public class InvoiceBasicResponse {
    private Long id;
    private String company;
    private Date dateOfInvoice;
    private CandidateBasicResponse candidate;
    private String bankName;
    private String account;
    private String depositor;
    private String swiftCode;
    private String payPal;
    private String email;
    private String mobile;
    private String address;
    private String resourceName;
    private Instant createdAt;
    private Instant updatedAt;

    private Boolean isConfirmed;
    private BigDecimal total;
    private String currency;
}
