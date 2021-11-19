package iks.surveytool.entities;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.format.annotation.DateTimeFormat;

import javax.persistence.*;
import javax.validation.constraints.Future;
import javax.validation.constraints.NotNull;
import java.time.LocalDateTime;
import java.util.List;

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
    // id for creator of survey to view results
    private String accessId;
    // id for users to participate in survey
    private String participationId;

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
                  String accessId,
                  String participationId,
                  List<QuestionGroup> questionGroups) {
        this.name = name;
        this.description = description;
        this.startDate = startDate;
        this.endDate = endDate;
        this.open = open;
        this.accessId = accessId;
        this.participationId = participationId;
        this.questionGroups = questionGroups;
    }
}
