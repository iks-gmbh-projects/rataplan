package iks.surveytool.entities;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.Type;
import org.springframework.format.annotation.DateTimeFormat;

import javax.persistence.*;
import javax.validation.constraints.Future;
import javax.validation.constraints.NotNull;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Entity
@Getter
@Setter
public class Survey extends AbstractEntity {

    @NotNull
    private String name;
    @Type(type = "text")
    private String description;

    // TODO: NotNull error message for both dates
    @Column(name = "startDate")
    @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME)
    private LocalDateTime startDate;

    @Column(name = "endDate")
    @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME)
    @Future(message = "Das Enddatum muss in der Zukunft liegen.")
    private LocalDateTime endDate;

    // Whether the survey is open to be answered or not:
    private boolean open;
    // ID for creator of survey to view results. May get changed to UUID:
    private String accessID;
    // Link for users to submit their answers:
    @Type(type = "org.hibernate.type.UUIDCharType")
    private UUID uuid;

    @JsonIgnore
    @ManyToOne
    @JoinColumn(name = "user_id")
    private User user;

    @OneToMany(cascade = CascadeType.ALL)
    @JoinColumn(name = "survey_id")
    private List<QuestionGroup> questionGroups;
}
