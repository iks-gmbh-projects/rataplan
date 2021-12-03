package iks.surveytool.builder;

import iks.surveytool.entities.Answer;
import iks.surveytool.entities.Checkbox;
import iks.surveytool.entities.Question;
import iks.surveytool.entities.User;

public class AnswerBuilder {
    public Answer createAnswer(Long id, String text, User user, Question question, Checkbox checkbox) {
        Answer newAnswer = new Answer();
        newAnswer.setId(id);
        newAnswer.setText(text);
        newAnswer.setUser(user);
        newAnswer.setQuestion(question);
        newAnswer.setCheckbox(checkbox);
        return newAnswer;
    }
}
