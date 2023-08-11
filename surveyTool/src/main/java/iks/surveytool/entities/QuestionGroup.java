package iks.surveytool.entities;

import iks.surveytool.mapping.crypto.DBEncryptedStringConverter;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.*;
import java.util.List;

@Entity
@Getter
@Setter
@NoArgsConstructor
public class QuestionGroup extends AbstractEntity {
    
    @Convert(converter= DBEncryptedStringConverter.class)
    private EncryptedString title;

    @ManyToOne(cascade = {CascadeType.DETACH, CascadeType.MERGE, CascadeType.PERSIST, CascadeType.REFRESH})
    @JoinColumn(name = "surveyId", nullable = false)
    private Survey survey;

    @OneToMany(cascade = CascadeType.ALL, orphanRemoval = true)
    @JoinColumn(name = "questionGroupId", nullable = false, insertable = false, updatable = false)
    @OrderBy("id")
    private List<Question> questions;

    public QuestionGroup(EncryptedString title, List<Question> questions) {
        this.title = title;
        this.questions = questions;
    }

    void checkIfComplete() throws InvalidSurveyException {
        if(this.questions.isEmpty()) throw new InvalidSurveyException("Empty question group");
        this.checkIfQuestionsComplete();
    }

    private void checkIfQuestionsComplete() throws InvalidSurveyException {
        for(Question q:this.questions) {
            q.checkIfComplete();
        }
    }

    void validate() throws InvalidSurveyException {
        this.validateData();
        this.validateQuestions();
    }

    private void validateData() throws InvalidSurveyException {
        if(this.title == null) throw new InvalidSurveyException("Question group without title");
        if(this.title.getString().length() > 255) throw new InvalidSurveyException("Question group title too long");
    }

    private void validateQuestions() throws InvalidSurveyException {
        for(Question q:this.questions) {
            q.validate();
        }
    }
}
