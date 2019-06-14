package saltlux.ctv.tranSS.payload.resource;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import saltlux.ctv.tranSS.model.CandidateAbility;
import saltlux.ctv.tranSS.payload.payment.PaymentReq;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Size;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@Getter
@Setter
@NoArgsConstructor
public class CandidateRequest {
    private Long id;
    @NotBlank
    @Size(max = 255)
    private String type;
    /*BASIC */
    @NotBlank
    @Size(max = 255)
    private String name;
    private Date dateOfBirth;
    @Size(max = 255)
    private String personalId;
    @Size(max = 16)
    private String gender;
    @Size(max = 255)
    private String country;
    @Size(max = 512)
    private String address;
    /*CONTACT*/
    @Size(max = 255)
    private String email;
    @Size(max = 255)
    private String email2;
    @Size(max = 32)
    private String mobile;
    @Size(max = 255)
    private String messenger;
    @Size(max = 255)
    private String skype;
    @Size(max = 255)
    private String socialpages;
    /*EDUCATION*/
    @Size(max = 255)
    private String education;
    @Size(max = 1024)
    private String attachments;
    @Size(max = 512)
    private String diploma;
    @Size(max = 255)
    private String nativeLanguage;
    @Size(max = 512)
    private String cv;
    /*PROFESSION*/
    @Size(max = 255)
    private String code;
    @Size(max = 1023)
    private String catTool;
    @Size(max = 255)
    private String majorField;
    /*OTHER*/
    @Size(max = 255)
    private String availableTime;
    private Date registerDate;
    @Size(max = 1024)
    private String remark;
    @Size(max = 3)
    private String currency;
    @NotBlank
    @Size(max = 16)
    private String grade;
    private List<CandidateAbility> abilities = new ArrayList<>();
    private Long paymentId;
}

