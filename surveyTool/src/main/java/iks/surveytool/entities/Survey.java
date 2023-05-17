package iks.surveytool.entities;

import iks.surveytool.mapping.crypto.DBEncryptedStringConverter;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.format.annotation.DateTimeFormat;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import java.time.ZonedDateTime;
import java.util.List;

@Entity
@Getter
@Setter
@NoArgsConstructor
public class Survey extends AbstractEntity {

    @NotNull
    @Convert(converter=DBEncryptedStringConverter.class)
    private EncryptedString name;
    @Convert(converter=DBEncryptedStringConverter.class)
    private EncryptedString description;

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

    @OneToMany(cascade = CascadeType.ALL, orphanRemoval = true, mappedBy = "survey")
    private List<QuestionGroup> questionGroups;
    
    @OneToMany(cascade = CascadeType.ALL, mappedBy = "survey")
    private List<SurveyResponse> surveyResponses;

    public Survey(EncryptedString name,
                  EncryptedString description,
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
                && this.name.getString().length() <= 255
                && this.description.getString().length() <= 3000;
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
