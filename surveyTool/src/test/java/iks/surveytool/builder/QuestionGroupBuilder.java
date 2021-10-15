package iks.surveytool.builder;

import iks.surveytool.entities.QuestionGroup;

public class QuestionGroupBuilder {
    public QuestionGroup createQuestionGroup(Long id,
                                             String title) {
        QuestionGroup newQuestionGroup = new QuestionGroup();
        newQuestionGroup.setId(id);
        newQuestionGroup.setTitle(title);
        return newQuestionGroup;
    }
}
