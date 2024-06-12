package iks.surveytool.entities;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.ToString;
import org.springframework.format.annotation.DateTimeFormat;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.OneToMany;
import java.time.Instant;
import java.time.ZonedDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.TimeZone;

@Entity
@Data
@EqualsAndHashCode(callSuper = true)
@ToString(callSuper = true)
@NoArgsConstructor
public class Survey extends AbstractEntity {
    
    @Column(nullable = false)
    private byte[] name;
    private byte[] description;
    
    @Column(nullable = false)
    @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME)
    private ZonedDateTime startDate;
    
    @Column(nullable = false)
    @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME)
    private ZonedDateTime endDate;
    
    private boolean openAccess;
    private boolean anonymousParticipation;
    // id for creator of survey to view results
    @Column(nullable = false)
    private String accessId;
    // id for users to participate in survey
    @Column(nullable = false)
    private String participationId;
    
    private Long userId;
    
    @Column
    private String timezone;
    
    @OneToMany(cascade = CascadeType.ALL, orphanRemoval = true, mappedBy = "survey")
    private List<QuestionGroup> questionGroups = new ArrayList<>();
    
    @OneToMany(cascade = CascadeType.ALL, mappedBy = "survey")
    @EqualsAndHashCode.Exclude
    @ToString.Exclude
    private List<SurveyResponse> surveyResponses = new ArrayList<>();
    
    public Survey(
        byte[] name,
        byte[] description,
        ZonedDateTime startDate,
        ZonedDateTime endDate,
        boolean openAccess,
        boolean anonymousParticipation,
        String accessId,
        String participationId,
        List<QuestionGroup> questionGroups,
        String timezone
    )
    {
        this.name = name;
        this.description = description;
        this.startDate = startDate;
        this.endDate = endDate;
        this.openAccess = openAccess;
        this.anonymousParticipation = anonymousParticipation;
        this.accessId = accessId;
        this.participationId = participationId;
        this.questionGroups = questionGroups;
        this.timezone = timezone;
    }
    
    
    @Override
    public void resetId() {
        questionGroups.forEach(AbstractEntity::resetId);
    }
    @Override
    public void bindChildren() {
        super.bindChildren();
        for(QuestionGroup qg : questionGroups) {
            if(qg.getSurvey() == null) qg.setSurvey(this);
            qg.bindChildren();
        }
    }
    public boolean isActiveAt(Instant instant) {
        return startDate.toInstant().isBefore(instant) && endDate.toInstant().isAfter(instant);
    }
    
    public void validate() throws InvalidEntityException {
        this.checkIfComplete();
        this.checkIfDataIsValid();
    }
    
    private void checkIfComplete() throws InvalidEntityException {
        if(this.questionGroups.isEmpty()) throw new InvalidEntityException("No question groups", this);
        this.checkIfQuestionGroupsComplete();
    }
    
    private void checkIfQuestionGroupsComplete() throws InvalidEntityException {
        for(QuestionGroup qg : this.questionGroups) {
            qg.checkIfComplete();
        }
    }
    
    private void checkIfDataIsValid() throws InvalidEntityException {
        this.validateData();
        this.validateQuestionGroups();
    }
    
    private void validateData() throws InvalidEntityException {
        this.checkNameAndDescription();
        this.checkTimeframe();
    }
    
    private void checkNameAndDescription() throws InvalidEntityException {
        if(this.name == null) throw new InvalidEntityException("Name is empty", this);
    }
    
    private void checkTimeframe() throws InvalidEntityException {
        if(this.startDate == null) throw new InvalidEntityException("Start Date missing", this);
        if(this.endDate == null) throw new InvalidEntityException("End Date missing", this);
        if(!dateTimeTodayOrInFuture(this.startDate) && this.getId() == null)
            throw new InvalidEntityException("Start Date is in the past", this);
        if(this.endDate.isBefore(this.startDate))
            throw new InvalidEntityException("End Date is before Start Date", this);
        if(!startDateBeforeEndDate()) throw new InvalidEntityException("End Date is before Start Date", this);
    }
    
    private boolean startDateBeforeEndDate() {
        return this.startDate.isBefore(this.endDate);
    }
    
    private static boolean dateTimeTodayOrInFuture(ZonedDateTime dateTime) {
        return dateTime.isAfter(ZonedDateTime.now().minusDays(1));
    }
    
    private void validateQuestionGroups() throws InvalidEntityException {
        for(QuestionGroup qg : this.questionGroups) {
            qg.validate();
        }
    }
}