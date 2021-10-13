package iks.surveytool.builder;

import iks.surveytool.entities.Question;
import iks.surveytool.entities.QuestionGroup;
import iks.surveytool.entities.Survey;

import java.util.ArrayList;
import java.util.List;

public class QuestionGroupBuilder {
    Long id = 1L;
    String title = "Test QuestionGroup";
    Survey survey = new Survey();
    List<Question> questions = new ArrayList<>();

    public QuestionGroupBuilder setId(Long id) {
        this.id = id;
        return this;
    }

    public QuestionGroupBuilder setTitle(String title) {
        this.title = title;
        return this;
    }

    public QuestionGroupBuilder setSurvey(Survey survey) {
        this.survey = survey;
        return this;
    }

    public QuestionGroupBuilder setQuestions(List<Question> questions) {
        this.questions = questions;
        return this;
    }

    public QuestionGroup build() {
        QuestionGroup newQuestionGroup = new QuestionGroup();
        newQuestionGroup.setId(id);
        newQuestionGroup.setTitle(title);
        newQuestionGroup.setSurvey(survey);
        newQuestionGroup.setQuestions(questions);
        return newQuestionGroup;
    }
}
