package iks.surveytool.builder;

import iks.surveytool.entities.Question;

public class QuestionBuilder {
    public Question createQuestion(Long id,
                                   String text,
                                   boolean required,
                                   boolean hasCheckbox) {
        Question newQuestion = new Question();
        newQuestion.setId(id);
        newQuestion.setText(text);
        newQuestion.setRequired(required);
        newQuestion.setHasCheckbox(hasCheckbox);
        return newQuestion;
    }
}
