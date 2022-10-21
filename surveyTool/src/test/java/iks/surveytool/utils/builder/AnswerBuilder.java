package iks.surveytool.utils.builder;

import iks.surveytool.entities.Answer;
import iks.surveytool.entities.Checkbox;
import iks.surveytool.entities.Question;
import iks.surveytool.entities.SurveyResponse;

import java.util.List;

public class AnswerBuilder {
    public Answer createAnswer(Long id, String text, Question question, SurveyResponse response, List<Checkbox> checkboxes) {
        Answer newAnswer = new Answer();
        newAnswer.setId(id);
        newAnswer.setText(text);
        newAnswer.setQuestion(question);
        newAnswer.setResponse(response);
        newAnswer.setCheckboxes(checkboxes);
        return newAnswer;
    }
}
