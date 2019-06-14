package saltlux.ctv.tranSS.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import javax.persistence.*;
import javax.validation.constraints.Size;
import java.io.Serializable;
import java.util.Objects;

@Getter
@Setter
@NoArgsConstructor
@EntityListeners(AuditingEntityListener.class)
@JsonIgnoreProperties(
        value = {"createdAt", "updatedAt"},
        allowGetters = true
)
@Entity
@Table(name = "payments")
public class Payment implements Serializable {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
//    @NotBlank
    @Size(max = 31)
    private String type;
//    @NotBlank
    @Size(max = 255)
    private String bankName;
//    @NotBlank
    @Size(max = 255)
    private String account;
    @Size(max = 255)
    private String accountOwner;
    @Size(max = 255)
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

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Payment obj = (Payment) o;
        return Objects.equals(id, obj.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }
}