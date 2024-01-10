package iks.surveytool.utils.builder;

import iks.surveytool.entities.EncryptedString;
import iks.surveytool.entities.Question;
import iks.surveytool.entities.QuestionGroup;

public class QuestionBuilder {
    public Question createQuestion(Long id,
                                   String text,
                                   boolean required,
                                   boolean hasCheckbox) {
        Question newQuestion = new Question();
        newQuestion.setId(id);
        newQuestion.setText(text == null ? null : new EncryptedString(text, false));
        newQuestion.setRequired(required);
        newQuestion.setHasCheckbox(hasCheckbox);
        return newQuestion;
    }

    public Question createQuestionIn(QuestionGroup group,
                                     Long id,
                                     String text,
                                     boolean required,
                                     boolean hasCheckbox) {
        Question newQuestion = createQuestion(id, text, required, hasCheckbox);
        newQuestion.setQuestionGroup(group);
        group.getQuestions().add(newQuestion);
        return newQuestion;
    }
}
