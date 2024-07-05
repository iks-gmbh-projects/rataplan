package iks.surveytool.entities;

import iks.surveytool.entities.question.ChoiceQuestion;
import iks.surveytool.entities.question.OpenQuestion;
import iks.surveytool.mapping.crypto.DBEncryptedStringConverter;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.*;
import java.util.ArrayList;
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

    @OneToMany(cascade = CascadeType.ALL, orphanRemoval = true, mappedBy = "questionGroup")
    @OrderBy("rank")
    private List<OpenQuestion> openQuestions = new ArrayList<>();
    @OneToMany(cascade = CascadeType.ALL, orphanRemoval = true, mappedBy = "questionGroup")
    @OrderBy("rank")
    private List<ChoiceQuestion> choiceQuestions = new ArrayList<>();

    public QuestionGroup(EncryptedString title, List<OpenQuestion> openQuestions, List<ChoiceQuestion> choiceQuestions) {
        this.title = title;
        this.openQuestions = openQuestions;
        this.choiceQuestions = choiceQuestions;
    }

    void checkIfComplete() throws InvalidEntityException {
        if(this.openQuestions.isEmpty() && this.choiceQuestions.isEmpty()) throw new InvalidEntityException("Empty question group", this);
        this.checkIfQuestionsComplete();
    }

    private void checkIfQuestionsComplete() throws InvalidEntityException {
        for(OpenQuestion q:this.openQuestions) {
            q.checkIfComplete();
        }
        for(ChoiceQuestion q:this.choiceQuestions) {
            q.checkIfComplete();
        }
    }

    void validate() throws InvalidEntityException {
        this.validateData();
        this.validateQuestions();
    }

    private void validateData() throws InvalidEntityException {
        if(this.title == null) throw new InvalidEntityException("Question group without title", this);
        if(this.title.getString().length() > 255) throw new InvalidEntityException("Question group title too long", this);
    }

    private void validateQuestions() throws InvalidEntityException {
        for(OpenQuestion q:this.openQuestions) {
            q.validate();
        }
        for(ChoiceQuestion q:this.choiceQuestions) {
            q.validate();
        }
    }
}