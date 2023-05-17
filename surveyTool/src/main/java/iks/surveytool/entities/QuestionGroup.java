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

    boolean checkIfComplete() {
        return !this.questions.isEmpty() && this.checkIfQuestionsComplete();
    }

    private boolean checkIfQuestionsComplete() {
        return this.questions.stream().allMatch(Question::checkIfComplete);
    }

    boolean validate() {
        return validateData() && this.validateQuestions();
    }

    private boolean validateData() {
        return this.title != null && this.title.getString().length() <= 255;
    }

    private boolean validateQuestions() {
        return this.questions.stream().allMatch(Question::validate);
    }
}
