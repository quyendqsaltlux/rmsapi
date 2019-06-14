package saltlux.ctv.tranSS.payload.resource;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import saltlux.ctv.tranSS.payload.payment.PaymentReq;

import java.util.Date;
import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class ResourceForProjectResponse {
    private Long id;
    private String type;
    private String name;
    private Date dateOfBirth;
    private String personalId;
    private String gender;
    private String country;
    private String address;
    private String email;
    private String email2;
    private String mobile;
    private String messenger;
    private String bank;
    private String payPal;
    private Boolean copyOfBankBook;
    private String diploma;
    private String nativeLanguage;
    private String cv;
    private String code;
    private String catTool;
    private String majorField;
    private String availableTime;
    private Date registerDate;
    private String remark;
    private String grade;
    private String currency;
    private PaymentReq payment;
    List<AbilityBasicResponse> abilities;
}
