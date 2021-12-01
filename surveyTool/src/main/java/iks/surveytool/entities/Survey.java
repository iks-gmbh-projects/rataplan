package iks.surveytool.entities;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.format.annotation.DateTimeFormat;

import javax.persistence.*;
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

    @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME)
    private LocalDateTime startDate;

    @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME)
    private LocalDateTime endDate;

    private boolean openAccess;
    // id for creator of survey to view results
    private String accessId;
    // id for users to participate in survey
    private String participationId;

    @ManyToOne
    @JoinColumn(name = "userId", nullable = false)
    private User user;

    @OneToMany(cascade = CascadeType.ALL)
    @JoinColumn(name = "surveyId")
    private List<QuestionGroup> questionGroups;

    public Survey(String name,
                  String description,
                  LocalDateTime startDate,
                  LocalDateTime endDate,
                  boolean openAccess,
                  String accessId,
                  String participationId,
                  List<QuestionGroup> questionGroups) {
        this.name = name;
        this.description = description;
        this.startDate = startDate;
        this.endDate = endDate;
        this.openAccess = openAccess;
        this.accessId = accessId;
        this.participationId = participationId;
        this.questionGroups = questionGroups;
    }

    public boolean validate() {
        return this.checkIfComplete() && checkIfDataIsValid();
    }

    private boolean checkIfComplete() {
        return this.user != null
                && !this.questionGroups.isEmpty()
                && this.checkIfQuestionGroupsComplete();
    }

    private boolean checkIfQuestionGroupsComplete() {
        for (QuestionGroup questionGroup : this.questionGroups) {
            if (!questionGroup.checkIfComplete()) {
                return false;
            }
        }
        return true;
    }

    private boolean checkIfDataIsValid() {
        return this.validateData() && this.validateQuestionGroups();
    }

    private boolean validateData() {
        return this.checkNameAndDescription() && this.checkTimeframe();
    }

    private boolean checkNameAndDescription() {
        return this.name != null
                && this.name.length() <= 255
                && this.description.length() <= 3000;
    }

    private boolean checkTimeframe() {
        return this.startDate != null
                && this.endDate != null
                && dateTimeInFuture(this.startDate)
                && dateTimeInFuture(this.endDate)
                && startDateBeforeEndDate();
    }

    private boolean startDateBeforeEndDate() {
        return this.startDate.isBefore(this.endDate);
    }

    private boolean dateTimeInFuture(LocalDateTime dateTime) {
        return dateTime.isAfter(LocalDateTime.now());
    }

    private boolean validateQuestionGroups() {
        for (QuestionGroup questionGroup : this.questionGroups) {
            if (!questionGroup.validate()) {
                return false;
            }
        }
        return true;
    }

}
