package iks.surveytool.utils.builder;

import iks.surveytool.entities.QuestionGroup;
import iks.surveytool.entities.question.ChoiceQuestion;
import iks.surveytool.entities.question.OpenQuestion;

import java.nio.charset.StandardCharsets;

public class QuestionBuilder {
    public static OpenQuestion createQuestion(
        Long id, int rank, String text, boolean required
    )
    {
        OpenQuestion newQuestion = new OpenQuestion();
        newQuestion.setId(id);
        newQuestion.setRank(rank);
        newQuestion.setText(text == null ? null : text.getBytes(StandardCharsets.UTF_8));
        newQuestion.setRequired(required);
        return newQuestion;
    }
    
    public static OpenQuestion createQuestionIn(
        QuestionGroup group, Long id, int rank, String text, boolean required
    )
    {
        OpenQuestion newQuestion = createQuestion(id, rank, text, required);
        newQuestion.setQuestionGroup(group);
        group.getOpenQuestions().add(newQuestion);
        return newQuestion;
    }
    
    public static ChoiceQuestion createQuestion(
        Long id, int rank, String text, int minSelect, int maxSelect
    )
    {
        ChoiceQuestion newQuestion = new ChoiceQuestion();
        newQuestion.setId(id);
        newQuestion.setRank(rank);
        newQuestion.setText(text == null ? null : text.getBytes(StandardCharsets.UTF_8));
        newQuestion.setMinSelect(minSelect);
        newQuestion.setMaxSelect(maxSelect);
        return newQuestion;
    }
    
    public static ChoiceQuestion createQuestionIn(
        QuestionGroup group, Long id, int rank, String text, int minSelect, int maxSelect
    )
    {
        ChoiceQuestion newQuestion = createQuestion(id, rank, text, minSelect, maxSelect);
        newQuestion.setQuestionGroup(group);
        group.getChoiceQuestions().add(newQuestion);
        return newQuestion;
    }
}