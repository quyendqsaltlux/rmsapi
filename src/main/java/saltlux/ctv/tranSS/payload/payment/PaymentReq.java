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
public class PaymentReq {
    private Long id;
//    @NotBlank
    @Size(max = 31)
    private String type;
//    @NotBlank
    @Size(max = 255)
    private String bankName;
//    @NotBlank
    @Size(max = 63)
    private String account;
    @Size(max = 255)
    private String accountOwner;
    @Size(max = 63)
    private String registrationNumber;
    @Size(max = 255)
    private String visa;
    @Size(max = 255)
    private String bankAddress;
    @Size(max = 255)
    private String ownerAddress;
    @Size(max = 255)
    private String swiftCode;
    @Size(max = 255)
    private String payPal;
    @Size(max = 255)
    private String iban;
    @Size(max = 255)
    private String bankId;
    @Size(max = 255)
    private String attachment;

    private String candidateCode;
}
