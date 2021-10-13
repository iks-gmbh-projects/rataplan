package iks.surveytool.builder;

import iks.surveytool.entities.Answer;
import iks.surveytool.entities.CheckboxGroup;
import iks.surveytool.entities.Question;
import iks.surveytool.entities.QuestionGroup;

import java.util.ArrayList;
import java.util.List;

public class QuestionBuilder {

    Long id = 1L;
    String text = "Default Question";
    boolean required = false;
    boolean hasCheckbox = false;
    QuestionGroup questionGroup = new QuestionGroup();
    List<Answer> answers = new ArrayList<>();
    CheckboxGroup checkboxGroup = new CheckboxGroup();

    public QuestionBuilder setId(Long id) {
        this.id = id;
        return this;
    }

    public QuestionBuilder setText(String text) {
        this.text = text;
        return this;
    }

    public QuestionBuilder setRequired(boolean required) {
        this.required = required;
        return this;
    }

    public QuestionBuilder setHasCheckbox(boolean hasCheckbox) {
        this.hasCheckbox = hasCheckbox;
        return this;
    }

    public QuestionBuilder setQuestionGroup(QuestionGroup questionGroup) {
        this.questionGroup = questionGroup;
        return this;
    }

    public QuestionBuilder setAnswers(List<Answer> answers) {
        this.answers = answers;
        return this;
    }

    public QuestionBuilder addAnswer(Answer answer) {
        this.answers.add(answer);
        return this;
    }

    public QuestionBuilder setCheckboxGroup(CheckboxGroup checkboxGroup) {
        this.checkboxGroup = checkboxGroup;
        return this;
    }

    public QuestionBuilder setCheckboxGroup(Long id,
                                            boolean multipleSelect,
                                            int minSelect,
                                            int maxSelect) {
        CheckboxGroup checkboxGroup = new CheckboxGroup();
        checkboxGroup.setId(id);
        checkboxGroup.setMultipleSelect(multipleSelect);
        checkboxGroup.setMinSelect(minSelect);
        checkboxGroup.setMaxSelect(maxSelect);
        this.checkboxGroup = checkboxGroup;
        return this;
    }

    public Question build() {
        Question newQuestion = new Question();
        newQuestion.setId(id);
        newQuestion.setText(text);
        newQuestion.setRequired(required);
        newQuestion.setHasCheckbox(hasCheckbox);
        newQuestion.setQuestionGroup(questionGroup);
        newQuestion.setAnswers(answers);
        newQuestion.setCheckboxGroup(checkboxGroup);
        return newQuestion;
    }
}
