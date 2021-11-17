package iks.surveytool.entities;

import lombok.Getter;
import lombok.NoArgsConstructor;
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
@NoArgsConstructor
public class Survey extends AbstractEntity {

    @NotNull
    private String name;
    private String description;

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

    @ManyToOne
    @JoinColumn(name = "user_id")
    private User user;

    @OneToMany(cascade = CascadeType.ALL)
    @JoinColumn(name = "survey_id")
    private List<QuestionGroup> questionGroups;
    
    public Survey(String name,
                  String description,
                  LocalDateTime startDate,
                  LocalDateTime endDate,
                  boolean open,
                  String accessID,
                  UUID uuid,
                  List<QuestionGroup> questionGroups) {
        this.name = name;
        this.description = description;
        this.startDate = startDate;
        this.endDate = endDate;
        this.open = open;
        this.accessID = accessID;
        this.uuid = uuid;
        this.questionGroups = questionGroups;
    }
}
