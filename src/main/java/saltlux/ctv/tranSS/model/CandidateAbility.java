package saltlux.ctv.tranSS.model;

import com.fasterxml.jackson.annotation.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import javax.persistence.*;
import javax.validation.constraints.*;
import java.io.Serializable;
import java.math.BigDecimal;
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
@Table(name = "candidates_ability")
public class CandidateAbility implements Serializable {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @NotBlank
    @Size(max = 255)
    private String sourceLanguage;
    @NotBlank
    @Size(max = 255)
    private String targetLanguage;
    @NotBlank
    @Size(max = 16)
    private String projectType;
    @Size(max = 255)
    private String task;
    @DecimalMin("0.0")
    @Column(columnDefinition = "DECIMAL(15,5) DEFAULT 0")
    private BigDecimal rate;
    @Size(max = 255)
    private String rateUnit;
    @DecimalMin("0.0")
    @Column(columnDefinition = "DECIMAL(15,5) DEFAULT 0")
    private BigDecimal rate2;
    @Size(max = 255)
    private String rate2unit;
    @DecimalMin("0.0")
    @Column(columnDefinition = "DECIMAL(10,2) DEFAULT 0")
    private BigDecimal minimumCharge;
    @DecimalMin("0.0")
    private Integer minimumVolum;
    @DecimalMin("0.0")
    @DecimalMax("100.0")
    @Column(columnDefinition = "DECIMAL(4,1) DEFAULT 0.0")
    private BigDecimal wrep;
    @DecimalMin("0.0")
    @DecimalMax("100.0")
    @Column(columnDefinition = "DECIMAL(4,1) DEFAULT 0.0")
    private BigDecimal w100;
    @DecimalMin("0.0")
    @DecimalMax("100.0")
    @Column(columnDefinition = "DECIMAL(4,1) DEFAULT 0.0")
    private BigDecimal w99_95;
    @DecimalMin("0.0")
    @DecimalMax("100.0")
    @Column(columnDefinition = "DECIMAL(4,1) DEFAULT 0.0")
    private BigDecimal w94_85;
    @DecimalMin("0.0")
    @DecimalMax("100.0")
    @Column(columnDefinition = "DECIMAL(4,1) DEFAULT 0.0")
    private BigDecimal w84_75;
    @DecimalMin("0.0")
    @DecimalMax("100.0")
    @Column(columnDefinition = "DECIMAL(4,1) DEFAULT 0.0")
    private BigDecimal wnoMatch;
    @Size(max = 255)
    private String dailyCapacity;
    @Size(max = 512)
    private String note;
//    @Size(max = 3)
//    private String currency;
    @DecimalMin("0.0")
    private Integer evaluateCount;
    @DecimalMin("0.0")
    private Integer evaluateTotal;
    @DecimalMin("0.0")
    private Integer evaluateAvg;

    @CreatedDate
    private Instant createdAt;

    @LastModifiedDate
    private Instant updatedAt;


    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "candidate_id", nullable = false)
    @JsonBackReference(value = "candidate-ability")
    private Candidate candidate;

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        CandidateAbility ability = (CandidateAbility) o;
        return Objects.equals(id, ability.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }
}