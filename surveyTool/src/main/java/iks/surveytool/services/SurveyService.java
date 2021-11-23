package iks.surveytool.services;

import iks.surveytool.components.Mapper;
import iks.surveytool.dtos.CompleteSurveyDTO;
import iks.surveytool.dtos.SurveyOverviewDTO;
import iks.surveytool.entities.*;
import iks.surveytool.repositories.SurveyRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Optional;
import java.util.Random;

@Service
@RequiredArgsConstructor
public class SurveyService {
    private final SurveyRepository surveyRepository;
    private final Mapper mapper;

    public Optional<Survey> findSurveyByParticipationId(String participationId) {
        return surveyRepository.findSurveyByParticipationId(participationId);
    }

    public Optional<Survey> findSurveyByAccessId(String accessId) {
        return surveyRepository.findSurveyByAccessId(accessId);
    }

    public List<Survey> findSurveysByOpenAccessIsTrue() {
        LocalDateTime currentDateTime = LocalDateTime.now();
        return surveyRepository.findSurveysByOpenAccessIsTrueAndEndDateIsAfterOrderByStartDate(currentDateTime);
    }

    public SurveyOverviewDTO createSurveyDtoFromSurvey(Survey savedSurvey) {
        return mapper.toSurveyDto(savedSurvey, true);
    }

    public SurveyOverviewDTO createSurveyDtoByAccessId(String accessId, boolean complete) {
        Optional<Survey> surveyOptional = findSurveyByAccessId(accessId);
        if (surveyOptional.isPresent()) {
            Survey survey = surveyOptional.get();
            return mapper.toSurveyDto(survey, complete);
        }
        return null;
    }

    public SurveyOverviewDTO createSurveyDtoByParticipationId(String participationId) {
        Optional<Survey> surveyOptional = findSurveyByParticipationId(participationId);
        if (surveyOptional.isPresent()) {
            Survey survey = surveyOptional.get();
            LocalDateTime surveyStartDate = survey.getStartDate();
            LocalDateTime surveyEndDate = survey.getEndDate();
            LocalDateTime currentDateTime = LocalDateTime.now();
            if (currentDateTime.isAfter(surveyStartDate) && currentDateTime.isBefore(surveyEndDate)) {
                return mapper.toSurveyDto(survey, true);
            } else {
                // If current time is not within start- and endDate: return survey without questions to fill information
                // in front-end
                return mapper.toSurveyDto(survey, false);
            }
        } else {
            return null;
        }
    }

    public List<SurveyOverviewDTO> createSurveyDtosByOpenIsTrue() {
        List<Survey> openAccessSurveys = findSurveysByOpenAccessIsTrue();
        return mapper.toOpenAccessSurveyDtoList(openAccessSurveys);

    }

    public Survey createSurveyFromDto(CompleteSurveyDTO surveyDTO) {
        return mapper.createSurveyFromDto(surveyDTO);
    }

    public Survey saveSurvey(Survey survey) {
        return surveyRepository.save(survey);
    }

    public void generateIds(Survey survey) {
        LocalDateTime currentDateTime = LocalDateTime.now();
        String accessId = generateUniqueIdWithDateTime(currentDateTime);
        survey.setAccessId(accessId);

        LocalDateTime startDateTime = survey.getStartDate();
        String participateId = generateUniqueIdWithDateTime(startDateTime);
        survey.setParticipationId(participateId);
    }

    public String generateUniqueIdWithDateTime(LocalDateTime currentDateTime) {
        String currentDateTimeHex = convertDateTimeToHex(currentDateTime);

        String hexSuffix = generateHexSuffix();

        String accessId = currentDateTimeHex + "-" + hexSuffix;

        while (surveyRepository.findSurveyByAccessId(accessId).isPresent()) {
            hexSuffix = generateHexSuffix();
            accessId = currentDateTimeHex + "-" + hexSuffix;
        }

        return accessId.toUpperCase();
    }

    private String convertDateTimeToHex(LocalDateTime dateTime) {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("mmkddMMyy");
        String formattedDate = dateTime.format(formatter);
        long formattedDateLong = Long.parseLong(formattedDate);

        return Long.toHexString(formattedDateLong);
    }

    private String generateHexSuffix() {
        Random random = new Random();
        int randomNumber = random.nextInt(256);
        return Integer.toHexString(randomNumber);
    }

    public boolean validate(Survey survey) {
        if (checkIfAnythingEmpty(survey)) {
            return false;
        } else if (!checkIfDataIsValid(survey)) {
            return false;
        } else {
            return true;
        }
    }

    private boolean checkIfDataIsValid(Survey survey) {
        User user = survey.getUser();
        String name = survey.getName();
        LocalDateTime startDate = survey.getStartDate();
        LocalDateTime endDate = survey.getEndDate();
        if (user == null || name == null || startDate == null || endDate == null) {
            return false;
        } else {
            if (!checkDates(startDate, endDate)) {
                return false;
            }
        }

        List<QuestionGroup> questionGroups = survey.getQuestionGroups();
        if (!validateQuestionGroups(questionGroups)) {
            return false;
        }

        return true;
    }

    private boolean checkDates(LocalDateTime startDate, LocalDateTime endDate) {
        return dateTimeInFuture(startDate) && dateTimeInFuture(endDate) && startDateBeforeEndDate(startDate, endDate);
    }

    private boolean startDateBeforeEndDate(LocalDateTime startDate, LocalDateTime endDate) {
        return startDate.isBefore(endDate);
    }

    private boolean dateTimeInFuture(LocalDateTime dateTime) {
        return dateTime.isAfter(LocalDateTime.now());
    }

    private boolean validateQuestionGroups(List<QuestionGroup> questionGroups) {
        for (QuestionGroup questionGroup : questionGroups) {
            String title = questionGroup.getTitle();
            if (title == null) {
                return false;
            }

            List<Question> questions = questionGroup.getQuestions();
            if (!validateQuestions(questions)) {
                return false;
            }
        }
        return true;
    }

    private boolean validateQuestions(List<Question> questions) {
        for (Question question : questions) {
            String text = question.getText();
            if (text == null) {
                return false;
            }
            if (question.isHasCheckbox()) {
                boolean required = question.isRequired();
                CheckboxGroup checkboxGroup = question.getCheckboxGroup();
                if (!validateCheckboxGroup(checkboxGroup, required)) {
                    return false;
                }
            }
        }
        return true;
    }

    private boolean validateCheckboxGroup(CheckboxGroup checkboxGroup, boolean required) {
        int minSelect = checkboxGroup.getMinSelect();
        int maxSelect = checkboxGroup.getMaxSelect();
        if (!checkMinMaxSelect(minSelect, maxSelect)) {
            return false;
        }

        int numberOfCheckboxes = checkboxGroup.getCheckboxes().size();
        if (!checkboxGroup.isMultipleSelect() && (numberOfCheckboxes < 2)) {
            return false;
        } else if (checkboxGroup.isMultipleSelect() && (numberOfCheckboxes < maxSelect)) {
            return false;
        } else if (required && checkboxGroup.isMultipleSelect() && (minSelect < 1)) {
            return false;
        }

        List<Checkbox> checkboxes = checkboxGroup.getCheckboxes();
        if (!validateCheckboxes(checkboxes)) {
            return false;
        }

        return true;
    }

    private boolean checkMinMaxSelect(int minSelect, int maxSelect) {
        return (minSelect <= maxSelect) && (0 <= minSelect) && (2 <= maxSelect);
    }

    private boolean validateCheckboxes(List<Checkbox> checkboxes) {
        for (Checkbox checkbox : checkboxes) {
            String text = checkbox.getText();
            if (text == null) {
                return false;
            }
        }
        return true;
    }

    private boolean checkIfAnythingEmpty(Survey survey) {
        List<QuestionGroup> questionGroups = survey.getQuestionGroups();
        if (questionGroups.isEmpty()) {
            return true;
        } else {
            for (QuestionGroup questionGroup : questionGroups) {
                List<Question> questions = questionGroup.getQuestions();
                if (questions.isEmpty()) {
                    return true;
                } else {
                    if (checkQuestions(questions)) {
                        return true;
                    }
                }
            }
        }
        return false;
    }

    private boolean checkQuestions(List<Question> questions) {
        for (Question question : questions) {
            if (question.isHasCheckbox()) {
                CheckboxGroup checkboxGroup = question.getCheckboxGroup();
                if (checkboxGroup.getCheckboxes().isEmpty()) {
                    return true;
                }
            }
        }
        return false;
    }
}
