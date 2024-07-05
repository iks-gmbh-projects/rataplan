package iks.surveytool.utils.builder;

import iks.surveytool.entities.question.ChoiceQuestion;
import iks.surveytool.entities.question.ChoiceQuestionChoice;

import java.nio.charset.StandardCharsets;

public class ChoiceBuilder {
    public static ChoiceQuestionChoice createChoice(
        Long id, String text, boolean hasTextField
    )
    {
        ChoiceQuestionChoice choice = new ChoiceQuestionChoice();
        choice.setId(id);
        choice.setText(text == null ? null : text.getBytes(StandardCharsets.UTF_8));
        choice.setHasTextField(hasTextField);
        return choice;
    }
    
    public static ChoiceQuestionChoice createChoiceIn(
        ChoiceQuestion question, Long id, String text, boolean hasTextField
    )
    {
        ChoiceQuestionChoice choice = createChoice(id, text, hasTextField);
        choice.setQuestion(question);
        question.getChoices().add(choice);
        return choice;
    }
}