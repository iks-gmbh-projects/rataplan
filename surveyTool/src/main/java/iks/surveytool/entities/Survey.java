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

    public void validate() throws InvalidSurveyException {
        this.checkIfComplete();
        this.checkIfDataIsValid();
    }

    private void checkIfComplete() throws InvalidSurveyException {
        if(this.questionGroups.isEmpty()) throw new InvalidSurveyException("No question groups");
        this.checkIfQuestionGroupsComplete();
    }

    private void checkIfQuestionGroupsComplete() throws InvalidSurveyException {
        for(QuestionGroup qg:this.questionGroups) {
            qg.checkIfComplete();
        }
    }

    private void checkIfDataIsValid() throws InvalidSurveyException {
        this.validateData();
        this.validateQuestionGroups();
    }

    private void validateData() throws InvalidSurveyException {
        this.checkNameAndDescription();
        this.checkTimeframe();
    }

    private void checkNameAndDescription() throws InvalidSurveyException {
        if(this.name == null) throw new InvalidSurveyException("Name is empty");
        if(this.name.getString().length() > 255) throw new InvalidSurveyException("Name too long");
        if(this.description.getString().length() > 3000) throw new InvalidSurveyException("Description too long");
        
    }

    private void checkTimeframe() throws InvalidSurveyException {
        if(this.startDate == null) throw new InvalidSurveyException("Start Date missing");
        if(this.endDate == null) throw new InvalidSurveyException("End Date missing");
        if(!dateTimeTodayOrInFuture(this.startDate)) throw new InvalidSurveyException("Start Date is in the past");
        if(!dateTimeInFuture(this.endDate)) throw new InvalidSurveyException("End Date is in the past");
        if(!startDateBeforeEndDate()) throw new InvalidSurveyException("End Date is before Start Date");
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

    private void validateQuestionGroups() throws InvalidSurveyException {
        for(QuestionGroup qg:this.questionGroups) {
            qg.validate();
        }
    }

}
