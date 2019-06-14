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
import javax.validation.constraints.*;
import java.io.Serializable;
import java.math.BigDecimal;
import java.time.Instant;
import java.util.Date;
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
@Table(name = "project_assignment")
public class ProjectAssignment implements Serializable {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    /**
     * Sometime PM want to select task that is not defined for candidate in RDB
     */
    private Boolean useCustomTask;
    /**
     * PM want to assign candidate not in RDB
     */
    private Boolean externalResource;
    @Size(max = 255)
    private String externalResourceName;

    @NotBlank
    @Size(max = 63)
    private String task;
    @NotBlank
    @Size(max = 63)
    private String source;
    @NotBlank
    @Size(max = 63)
    private String target;

    @Temporal(TemporalType.DATE)
    private Date ho;
    @Temporal(TemporalType.DATE)
    private Date hb;
    //    @NotBlank
    @Size(max = 31)
    private String status;
    @Size(max = 31)
    private String progress;
    @Size(max = 255)
    private String projectCode;
    private Long projectId;
    private Long abilityId;

    @NotNull
    @DecimalMin("0.0")
    @DecimalMax("100.0")
    private BigDecimal wrep;
    @NotNull
    @DecimalMin("0.0")
    @DecimalMax("100.0")
    private BigDecimal w100;
    @NotNull
    @DecimalMin("0.0")
    @DecimalMax("100.0")
    private BigDecimal w99_95;
    @NotNull
    @DecimalMin("0.0")
    @DecimalMax("100.0")
    private BigDecimal w94_85;
    @NotNull
    @DecimalMin("0.0")
    @DecimalMax("100.0")
    private BigDecimal w84_75;
    @NotNull
    @DecimalMin("0.0")
    @DecimalMax("100.0")
    private BigDecimal wnoMatch;

    @NotNull
    @DecimalMin("0.0")
    @DecimalMax("10000000.0")
    private Integer reprep;
    @NotNull
    @DecimalMin("0.0")
    @DecimalMax("10000000.0")
    private Integer rep100;
    @NotNull
    @DecimalMin("0.0")
    @DecimalMax("10000000.0")
    private Integer rep99_95;
    @NotNull
    @DecimalMin("0.0")
    @DecimalMax("10000000.0")
    private Integer rep94_85;
    @NotNull
    @DecimalMin("0.0")
    @DecimalMax("10000000.0")
    private Integer rep84_75;
    @NotNull
    @DecimalMin("0.0")
    @DecimalMax("10000000.0")
    private Integer repnoMatch;

    /**
     * = reprep + rep100 + rep99_95 + rep94_85 + rep84_75 + repnoMatch
     */
    @NotNull
    private Integer totalRep;
    /**
     * =(reprep*wrep)+(rep100*w100)+(rep99_95*w99_95)+(rep94_85*w94_85)+(rep84_75*w84_75)+(repnoMatch*wnoMatch) or user define
     */
    @NotNull
    private BigDecimal netOrHour;
    private Boolean notAutoComputeNetHour;
    /**
     * = rate of Ability in case netOrHour > minimum volume. otherwise = rate2 of ability
     */
    @NotNull
    private BigDecimal unitPrice;
    /**
     * Total Money
     * = unitPrice * netOrHour
     */
    @NotNull
    private BigDecimal total;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "candidate_id")
    @JsonBackReference(value = "candidate-assignments")
    private Candidate candidate;

    @CreatedDate
    private Instant createdAt;
    @LastModifiedDate
    private Instant updatedAt;

    @Temporal(TemporalType.DATE)
    private Date finishedAt;

    public ProjectAssignment(@NotBlank @Size(max = 63) String task,
                             @NotBlank @Size(max = 63) String source,
                             @NotBlank @Size(max = 63) String target,
                             Date ho, Date hb,
                             BigDecimal total,
                             @Size(max = 255) String projectCode) {
        this.task = task;
        this.source = source;
        this.target = target;
        this.total = total;
        this.projectCode = projectCode;
        this.ho = ho;
        this.hb = hb;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        ProjectAssignment choice = (ProjectAssignment) o;
        return Objects.equals(id, choice.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }
}