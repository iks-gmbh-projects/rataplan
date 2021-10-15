package iks.surveytool.builder;

import iks.surveytool.entities.Survey;

import java.net.URL;
import java.time.LocalDateTime;

public class SurveyBuilder {

    String description = "Default Beschreibung";
    LocalDateTime startDate = LocalDateTime.of(2050, 1, 1, 12, 0);
    LocalDateTime endDate = startDate.plusWeeks(1L);
    boolean open = false;
    String accessID = "Test ID";
    URL link;

    public Survey createSurveyWithDefaultDate(Long id,
                                              String name) {
        Survey newSurvey = new Survey();
        newSurvey.setId(id);
        newSurvey.setName(name);
        newSurvey.setDescription(description);
        newSurvey.setStartDate(startDate);
        newSurvey.setEndDate(endDate);
        newSurvey.setOpen(open);
        newSurvey.setAccessID(accessID);
        return newSurvey;
    }
}
