package iks.surveytool.utils.builder;

import iks.surveytool.entities.EncryptedString;
import iks.surveytool.entities.QuestionGroup;
import iks.surveytool.entities.Survey;

import java.util.ArrayList;

public class QuestionGroupBuilder {
    public QuestionGroup createQuestionGroup(Long id,
                                             String title) {
        QuestionGroup newQuestionGroup = new QuestionGroup();
        newQuestionGroup.setId(id);
        newQuestionGroup.setTitle(title == null ? null : new EncryptedString(title, false));
        newQuestionGroup.setQuestions(new ArrayList<>());
        return newQuestionGroup;
    }

    public QuestionGroup createQuestionGroupIn(Survey survey, Long id, String title) {
        QuestionGroup newQuestionGroup = createQuestionGroup(id, title);
        newQuestionGroup.setSurvey(survey);
        survey.getQuestionGroups().add(newQuestionGroup);
        return newQuestionGroup;
    }
}
