package saltlux.ctv.tranSS.model;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonManagedReference;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.BatchSize;
import org.hibernate.annotations.Fetch;
import org.hibernate.annotations.FetchMode;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import javax.persistence.*;
import javax.validation.constraints.DecimalMin;
import javax.validation.constraints.Size;
import java.io.Serializable;
import java.math.BigDecimal;
import java.time.Instant;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
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
@Table(name = "invoices")
public class Invoice implements Serializable {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    /**
     * P or SP
     */
    @Size(max = 2)
    private String company;

    private Date dateOfInvoice;
    @Column(columnDefinition="tinyint(1) default 0")
    private Boolean isConfirmed;
    @Size(max = 255)
    private String bankName;
    @Size(max = 255)
    private String account;
    @Size(max = 255)
    private String depositor;
    @Size(max = 255)
    private String swiftCode;
    @Size(max = 255)
    private String payPal;

    @Size(max = 255)
    private String email;
    @Size(max = 255)
    private String mobile;
    @Size(max = 512)
    private String address;
    @Size(max = 255)
    private String resourceName;

    @DecimalMin("0.0")
    @Column(columnDefinition = "DECIMAL(15,2) DEFAULT 0")
    private BigDecimal total;
    private String currency;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "candidate_id", nullable = true)
    @JsonBackReference(value = "candidate-invoice")
    private Candidate candidate;

    @OneToMany(
            mappedBy = "invoice",
            cascade = CascadeType.REFRESH,
            fetch = FetchType.EAGER,
            orphanRemoval = true
    )
    @Fetch(FetchMode.SELECT)
    @BatchSize(size = 30)
    @JsonManagedReference(value = "invoice-pos")
    private List<PurchaseOrder> purchaseOrders = new ArrayList<>();

    @CreatedDate
    private Instant createdAt;
    @LastModifiedDate
    private Instant updatedAt;

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Invoice o1 = (Invoice) o;
        return Objects.equals(id, o1.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }
}