package iks.surveytool.builder;

import iks.surveytool.entities.QuestionGroup;

import java.util.ArrayList;

public class QuestionGroupBuilder {
    public QuestionGroup createQuestionGroup(Long id,
                                             String title) {
        QuestionGroup newQuestionGroup = new QuestionGroup();
        newQuestionGroup.setId(id);
        newQuestionGroup.setTitle(title);
        newQuestionGroup.setQuestions(new ArrayList<>());
        return newQuestionGroup;
    }
}
