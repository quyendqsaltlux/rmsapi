package saltlux.ctv.tranSS.model;

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
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Size;
import java.io.Serializable;
import java.time.Instant;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@EntityListeners(AuditingEntityListener.class)
@JsonIgnoreProperties(
        value = {"createdAt", "updatedAt"},
        allowGetters = true
)
@Entity
@Table(name = "candidates")
public class Candidate implements Serializable {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @NotBlank
    @Size(max = 255)
    private String type;
    /*BASIC */
    @NotBlank
    @Size(max = 255)
    @Column(unique = true)
    private String name;
    @Temporal(TemporalType.DATE)
    private Date dateOfBirth;
    @Size(max = 255)
    @Column(unique = true)
    private String personalId;
    @Size(max = 16)
    private String gender;
    @Size(max = 255)
    private String country;
    @Size(max = 512)
    private String address;
    /*CONTACT*/
//    @NaturalId
    @Size(max = 255)
//    @Email
    @Column(unique = true)
    private String email;
    //    @NaturalId
    @Size(max = 255)
//    @Email
    @Column(unique = true)
    private String email2;
    @Size(max = 255)
    @Column(unique = true)
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
    @Size(max = 127)
    @NotBlank
    @Column(unique = true)
    private String code;
    @Size(max = 1023)
    private String catTool;
    @Size(max = 255)
    private String majorField;
    /*OTHER*/
    @Size(max = 255)
    private String availableTime;
    @Temporal(TemporalType.DATE)
    private Date registerDate;
    @Size(max = 1023)
    private String remark;
    @Size(max = 16)
    private String grade;
    @Size(max = 3)
    private String currency;

    @OneToMany(
            mappedBy = "candidate",
            cascade = CascadeType.ALL,
            fetch = FetchType.EAGER,
            orphanRemoval = true
    )
    @Fetch(FetchMode.SELECT)
    @BatchSize(size = 30)
    @JsonManagedReference(value = "candidate-ability")
    private List<CandidateAbility> abilities = new ArrayList<>();

    @OneToMany(
            mappedBy = "candidate",
            cascade = CascadeType.ALL,
            fetch = FetchType.EAGER,
            orphanRemoval = true
    )
    @Fetch(FetchMode.SELECT)
    @BatchSize(size = 30)
    @JsonManagedReference(value = "candidate-assignments")
    private List<ProjectAssignment> assignments = new ArrayList<>();

    @OneToOne(cascade = CascadeType.ALL)
    @JoinColumn(name = "payment_id", referencedColumnName = "id")
    private Payment payment;

    @CreatedDate
    private Instant createdAt;

    @LastModifiedDate
    private Instant updatedAt;

    public void addAbility(CandidateAbility ability) {
        abilities.add(ability);
        ability.setCandidate(this);
    }

    public void removeAbility(CandidateAbility ability) {
        abilities.remove(ability);
        ability.setCandidate(null);
    }

    public void addAssignment(ProjectAssignment assignment) {
        assignments.add(assignment);
        assignment.setCandidate(this);
    }

    public void removeAssignment(ProjectAssignment assignment) {
        assignments.remove(assignment);
        assignment.setCandidate(null);
    }

    @Override
    public String toString() {
        return "Candidate{" +
                "id=" + id +
                ", type='" + type + '\'' +
                ", name='" + name + '\'' +
                ", dateOfBirth=" + dateOfBirth +
                ", personalId='" + personalId + '\'' +
                ", gender='" + gender + '\'' +
                ", country='" + country + '\'' +
                ", address='" + address + '\'' +
                ", email='" + email + '\'' +
                ", email2='" + email2 + '\'' +
                ", mobile='" + mobile + '\'' +
                ", messenger='" + messenger + '\'' +
                ", diploma='" + diploma + '\'' +
                ", nativeLanguage='" + nativeLanguage + '\'' +
                ", cv='" + cv + '\'' +
                ", code='" + code + '\'' +
                ", catTool='" + catTool + '\'' +
                ", majorField='" + majorField + '\'' +
                ", availableTime='" + availableTime + '\'' +
                ", registerDate=" + registerDate +
                ", remark='" + remark + '\'' +
                ", grade='" + grade + '\'' +
                ", abilities=" + abilities +
                ", createdAt=" + createdAt +
                ", updatedAt=" + updatedAt +
                '}';
    }
}