package iks.surveytool.entities;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.format.annotation.DateTimeFormat;

import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.JoinColumn;
import javax.persistence.OneToMany;
import javax.validation.constraints.NotNull;
import java.time.ZonedDateTime;
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
    private ZonedDateTime startDate;

    @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME)
    private ZonedDateTime endDate;

    private boolean openAccess;
    private boolean anonymousParticipation;
    // id for creator of survey to view results
    private String accessId;
    // id for users to participate in survey
    private String participationId;

    private Long userId;

    @OneToMany(cascade = CascadeType.ALL)
    @JoinColumn(name = "surveyId")
    private List<QuestionGroup> questionGroups;

    public Survey(String name,
                  String description,
                  ZonedDateTime startDate,
                  ZonedDateTime endDate,
                  boolean openAccess,
                  boolean anonymousParticipation,
                  String accessId,
                  String participationId,
                  List<QuestionGroup> questionGroups) {
        this.name = name;
        this.description = description;
        this.startDate = startDate;
        this.endDate = endDate;
        this.openAccess = openAccess;
        this.anonymousParticipation = anonymousParticipation;
        this.accessId = accessId;
        this.participationId = participationId;
        this.questionGroups = questionGroups;
    }

    public boolean validate() {
        return this.checkIfComplete() && checkIfDataIsValid();
    }

    private boolean checkIfComplete() {
        return !this.questionGroups.isEmpty()
                && this.checkIfQuestionGroupsComplete();
    }

    private boolean checkIfQuestionGroupsComplete() {
        return this.questionGroups.stream().allMatch(QuestionGroup::checkIfComplete);
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
                && dateTimeTodayOrInFuture(this.startDate)
                && dateTimeInFuture(this.endDate)
                && startDateBeforeEndDate();
    }

    private boolean startDateBeforeEndDate() {
        return this.startDate.isBefore(this.endDate);
    }

    private static boolean dateTimeInFuture(ZonedDateTime dateTime) {
        return dateTime.isAfter(ZonedDateTime.now());
    }

    private static boolean dateTimeTodayOrInFuture(ZonedDateTime dateTime) {
        return dateTime.isAfter(ZonedDateTime.now().minusDays(1));
    }

    private boolean validateQuestionGroups() {
        return this.questionGroups.stream().allMatch(QuestionGroup::validate);
    }

}
