package iks.surveytool.entities;

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

    private String title;

    @ManyToOne(cascade = CascadeType.ALL)
    @JoinColumn(name = "surveyId", nullable = false)
    private Survey survey;

    @OneToMany(cascade = CascadeType.ALL)
    @JoinColumn(name = "questionGroupId")
    private List<Question> questions;

    public QuestionGroup(String title, List<Question> questions) {
        this.title = title;
        this.questions = questions;
    }

    public boolean checkIfComplete() {
        return !this.questions.isEmpty() && this.checkIfQuestionsComplete();
    }

    private boolean checkIfQuestionsComplete() {
        for (Question question : this.questions) {
            if (!question.checkIfComplete()) {
                return false;
            }
        }
        return true;
    }

    public boolean validate() {
        return validateData() && this.validateQuestions();
    }

    private boolean validateData() {
        return this.title != null && this.title.length() <= 255;
    }

    private boolean validateQuestions() {
        for (Question question : this.questions) {
            if (!question.validate()) {
                return false;
            }
        }
        return true;
    }
}
