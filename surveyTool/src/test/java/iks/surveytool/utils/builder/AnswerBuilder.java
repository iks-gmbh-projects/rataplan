package iks.surveytool.utils.builder;

import iks.surveytool.entities.SurveyResponse;
import iks.surveytool.entities.answer.ChoiceAnswerText;
import iks.surveytool.entities.answer.OpenAnswer;
import iks.surveytool.entities.question.ChoiceQuestion;
import iks.surveytool.entities.question.OpenQuestion;

import java.nio.charset.StandardCharsets;

public class AnswerBuilder {
    public static OpenAnswer createAnswer(Long id, String text, OpenQuestion question) {
        OpenAnswer newAnswer = new OpenAnswer();
        newAnswer.setId(id);
        newAnswer.setText(text == null ? null : text.getBytes(StandardCharsets.UTF_8));
        newAnswer.setQuestion(question);
        return newAnswer;
    }

    public static OpenAnswer createAnswerIn(SurveyResponse response, Long id, String text, OpenQuestion question) {
        OpenAnswer newAnswer = createAnswer(id, text, question);
        newAnswer.setResponse(response);
        response.getOpenAnswers().add(newAnswer);
        return newAnswer;
    }
    
    public static ChoiceAnswerText createAnswer(Long id, String text, ChoiceQuestion question) {
        ChoiceAnswerText newAnswer = new ChoiceAnswerText();
        newAnswer.setId(id);
        newAnswer.setText(text == null ? null : text.getBytes(StandardCharsets.UTF_8));
        newAnswer.setQuestion(question);
        return newAnswer;
    }
    
    public static ChoiceAnswerText createAnswerIn(SurveyResponse response, Long id, String text, ChoiceQuestion question) {
        ChoiceAnswerText newAnswer = createAnswer(id, text, question);
        newAnswer.setResponse(response);
        response.getChoiceAnswerTexts().add(newAnswer);
        return newAnswer;
    }
}