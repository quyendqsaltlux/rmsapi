package saltlux.ctv.tranSS.model;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import javax.persistence.*;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Size;
import java.io.Serializable;
import java.time.Instant;
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
@Table(name = "purchase_order", uniqueConstraints = {
        @UniqueConstraint(columnNames = {
                "code"
        })
})
public class PurchaseOrder implements Serializable {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @NotBlank
    @Size(max = 63)
    private String code;
    private String currency;
    //    @NotBlank
    @Size(max = 31)
    private String status;
    /**
     * P or SP
     */
    @Size(max = 2)
    private String company;

    @CreatedDate
    private Instant createdAt;
    @LastModifiedDate
    private Instant updatedAt;

    @OneToOne(cascade = CascadeType.REFRESH)
    @JoinColumn(name = "assignment_id", referencedColumnName = "id", unique = true)
    private ProjectAssignment assignment;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "invoice_id", nullable = true)
    @JsonBackReference(value = "invoice-pos")
    private Invoice invoice;

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        PurchaseOrder o1 = (PurchaseOrder) o;
        return Objects.equals(id, o1.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }
}