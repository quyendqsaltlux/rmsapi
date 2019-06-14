package saltlux.ctv.tranSS.payload.payment;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.validation.constraints.Size;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class PaymentResponse {
    private Long id;
    private String type;
    private String bankName;
    private String account;
    private String accountOwner;
    private String registrationNumber;
    private String visa;
    private String bankAddress;
    private String ownerAddress;
    private String swiftCode;
    private String payPal;
    private String iban;
    private String bankId;
    private String attachment;
    private String candidateCode;
}
