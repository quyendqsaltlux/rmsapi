package saltlux.ctv.tranSS.model;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.*;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import javax.persistence.*;
import javax.validation.constraints.DecimalMax;
import javax.validation.constraints.DecimalMin;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Size;
import java.io.Serializable;
import java.time.Instant;
import java.util.Date;
import java.util.Objects;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor(access = AccessLevel.PUBLIC)
@EntityListeners(AuditingEntityListener.class)
@JsonIgnoreProperties(
        value = {"createdAt", "updatedAt"},
        allowGetters = true
)
@Entity
@Table(name = "projects")
public class Project implements Serializable {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @NotBlank
    @Size(max = 255)
    @Column(unique = true)
    private String no;
    @Temporal(TemporalType.DATE)
    private Date requestDate;
    @Temporal(TemporalType.DATE)
    private Date dueDate;
    @Size(max = 255)
    private String dueTime;
    @Size(max = 255)
    private String category;
    @NotBlank
    @Size(max = 255)
//    @Column(unique = true)
    private String code;
    @Size(max = 255)
    private String folderName;
    @Size(max = 255)
    private String client;
    @Size(max = 255)
    private String contents;
    @Size(max = 255)
    private String reference;
    @Size(max = 255)
    private String termbase;
    @Size(max = 255)
    private String instruction;
    @Size(max = 1023)
    private String remark;
    private Float totalVolume;
    @Size(max = 32)
    private String unit;
    @Size(max = 32)
    private String target;
    @Size(max = 127)
    private String progressStatus;
    @DecimalMin("0.0")
    @DecimalMax("1.0")
    private Float progressPoint;
    @Size(max = 255)
    private String pmVtc;
    @Temporal(TemporalType.DATE)
    private Date ho;
    @Temporal(TemporalType.DATE)
    private Date hb;
    @Temporal(TemporalType.DATE)
    private Date reviewSchedule;
    @Size(max = 255)
    private String suggestedCandidate;
    @Temporal(TemporalType.DATE)
    private Date finalDelivery;
    @Size(max = 127)
    private String company;
    @Size(max = 255)
    private String field;

    /*project in pass that imported from file */

    @Column(columnDefinition="tinyint(1) default 0")
    private Boolean isOld;
    @Column(columnDefinition="tinyint(1) default 0")
    private Boolean isWrongCode;


    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "pm", nullable = false)
    @JsonBackReference(value = "project-user")
    private User pm;

    @CreatedDate
    private Instant createdAt;
    @LastModifiedDate
    private Instant updatedAt;

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Project o1 = (Project) o;
        return Objects.equals(id, o1.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }

}

