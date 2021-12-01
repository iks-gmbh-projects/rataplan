package iks.surveytool.utils.builder;

import iks.surveytool.entities.Survey;
import iks.surveytool.entities.User;

import java.net.URL;
import java.time.LocalDateTime;
import java.util.ArrayList;

public class SurveyBuilder {

    String description = "Default Beschreibung";
    LocalDateTime startDate = LocalDateTime.of(2050, 1, 1, 12, 0);
    LocalDateTime endDate = startDate.plusWeeks(1L);
    boolean openAccess = false;
    String accessId = "Test ID";
    URL link;

    public Survey createDefaultSurvey() {
        Survey newSurvey = new Survey();
        User user = new User("Test Person");
        newSurvey.setId(1L);
        newSurvey.setName("Test Survey");
        newSurvey.setUser(user);
        setDefaults(newSurvey);
        return newSurvey;
    }

    public Survey createSurveyWithDefaultDate(Long id,
                                              String name) {
        Survey newSurvey = new Survey();
        newSurvey.setId(id);
        newSurvey.setName(name);
        setDefaults(newSurvey);
        return newSurvey;
    }

    public Survey createSurveyWithUserAndDefaultDate(Long id,
                                                     String name,
                                                     User user) {
        Survey newSurvey = new Survey();
        newSurvey.setId(id);
        newSurvey.setName(name);
        newSurvey.setUser(user);
        setDefaults(newSurvey);
        return newSurvey;
    }

    private void setDefaults(Survey newSurvey) {
        newSurvey.setDescription(description);
        newSurvey.setStartDate(startDate);
        newSurvey.setEndDate(endDate);
        newSurvey.setOpenAccess(openAccess);
        newSurvey.setAccessId(accessId);
        newSurvey.setQuestionGroups(new ArrayList<>());
    }
}
