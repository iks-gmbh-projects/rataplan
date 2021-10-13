package iks.surveytool.builder;

import iks.surveytool.entities.Question;
import iks.surveytool.entities.QuestionGroup;
import iks.surveytool.entities.Survey;
import iks.surveytool.entities.User;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

public class SurveyBuilder {

    Long id = 1L;
    String name = "Test Umfrage";
    String description = "Default Beschreibung";
    LocalDateTime startDate = LocalDateTime.of(2050, 1, 1, 12, 0);
    LocalDateTime endDate = startDate.plusWeeks(1L);
    boolean open = false;
    String accessID = "Test ID";
    String link = "Eine URL";
    User user = new User();
    List<QuestionGroup> questionGroups = new ArrayList<>();

    public SurveyBuilder setId(Long id) {
        this.id = id;
        return this;
    }

    public SurveyBuilder setName(String name) {
        this.name = name;
        return this;
    }

    public SurveyBuilder setDescription(String description) {
        this.description = description;
        return this;
    }

    public SurveyBuilder setStartDate(LocalDateTime startDate) {
        this.startDate = startDate;
        return this;
    }

    public SurveyBuilder setEndDate(LocalDateTime endDate) {
        this.endDate = endDate;
        return this;
    }

    public SurveyBuilder setOpen(boolean open) {
        this.open = open;
        return this;
    }

    public SurveyBuilder setAccessID(String accessID) {
        this.accessID = accessID;
        return this;
    }

    public SurveyBuilder setLink(String link) {
        this.link = link;
        return this;
    }

    public SurveyBuilder setUser(User user) {
        this.user = user;
        return this;
    }

    public SurveyBuilder setQuestionGroups(List<QuestionGroup> questionGroups) {
        this.questionGroups = questionGroups;
        return this;
    }

    public SurveyBuilder addQuestionGroup(QuestionGroup questionGroup) {
        this.questionGroups.add(questionGroup);
        return this;
    }

    public SurveyBuilder addQuestionGroup(String title) {
        QuestionGroup questionGroup = new QuestionGroup();
        questionGroup.setTitle(title);
        this.questionGroups.add(questionGroup);
        return this;
    }

    public SurveyBuilder addQuestionToGroup(Question question, int groupIndex) {
        QuestionGroup questionGroup = questionGroups.get(groupIndex);
        if (questionGroup.getQuestions() == null) {
            questionGroup.setQuestions(new ArrayList<>());
        }
        questionGroup.getQuestions().add(question);
        return this;
    }

    public Survey build() {
        Survey newSurvey = new Survey();
        newSurvey.setId(id);
        newSurvey.setName(name);
        newSurvey.setDescription(description);
        newSurvey.setStartDate(startDate);
        newSurvey.setEndDate(endDate);
        newSurvey.setOpen(open);
        newSurvey.setAccessID(accessID);
        newSurvey.setLink(link);
        newSurvey.setUser(user);
        newSurvey.setQuestionGroups(questionGroups);
        return newSurvey;
    }
}
