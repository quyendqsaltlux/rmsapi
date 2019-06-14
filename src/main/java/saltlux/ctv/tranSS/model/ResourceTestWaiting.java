package saltlux.ctv.tranSS.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import javax.persistence.*;
import javax.validation.constraints.Size;
import java.io.Serializable;
import java.time.Instant;
import java.util.Date;

@Getter
@Setter
@NoArgsConstructor
@EntityListeners(AuditingEntityListener.class)
@JsonIgnoreProperties(
        value = {"createdAt", "updatedAt"},
        allowGetters = true
)
@Entity
@Table(name = "resource_test_waiting")
public class ResourceTestWaiting implements Serializable {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Size(max = 32)
    @Column(unique = true)
    private String code;
    @Size(max = 255)
    private String source;
    @Size(max = 255)
    private String target;
    @Size(max = 255)
    private String name;
    @Size(max = 255)
    private String contact;
    @Size(max = 255)
    private String testContents;
    @Size(max = 255)
    private String tool;
    @Temporal(TemporalType.DATE)
    private Date testInvitation;
    @Temporal(TemporalType.DATE)
    private Date testSending;
    @Temporal(TemporalType.DATE)
    private Date hbReceipt;
    @Size(max = 255)
    private String hbFiles;
    @Size(max = 255)
    private String internalCheck;
    @Size(max = 255)
    private String testEvaluation;
    @Size(max = 255)
    private String testResult;
    @Size(max = 255)
    private String evaluator;
    @Size(max = 255)
    private String comments;
    @Size(max = 1023)
    private String otherNote;
    @Size(max = 255)
    private String attachment;

    @Temporal(TemporalType.DATE)
    private Date shortListDate;
    @Temporal(TemporalType.DATE)
    private Date negotiationDate;
    @Size(max = 1023)
    private String expectedRateRange;
    @Size(max = 255)
    private String field;
    @Size(max = 63)
    private String processStatus;
    @Size(max = 255)
    private String catTool;

    private Integer isShortList;

    @CreatedDate
    private Instant createdAt;
    @LastModifiedDate
    private Instant updatedAt;
}