package iks.surveytool.utils.builder;

import iks.surveytool.entities.QuestionGroup;
import iks.surveytool.entities.Survey;

import java.nio.charset.StandardCharsets;
import java.util.ArrayList;

public class QuestionGroupBuilder {
    public static QuestionGroup createQuestionGroup(Long id,
                                             String title) {
        QuestionGroup newQuestionGroup = new QuestionGroup();
        newQuestionGroup.setId(id);
        newQuestionGroup.setTitle(title == null ? null : title.getBytes(StandardCharsets.UTF_8));
        newQuestionGroup.setOpenQuestions(new ArrayList<>());
        newQuestionGroup.setChoiceQuestions(new ArrayList<>());
        return newQuestionGroup;
    }

    public static QuestionGroup createQuestionGroupIn(Survey survey, Long id, String title) {
        QuestionGroup newQuestionGroup = createQuestionGroup(id, title);
        newQuestionGroup.setSurvey(survey);
        survey.getQuestionGroups().add(newQuestionGroup);
        return newQuestionGroup;
    }
}