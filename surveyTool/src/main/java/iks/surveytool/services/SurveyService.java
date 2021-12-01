package iks.surveytool.services;

import iks.surveytool.components.Mapper;
import iks.surveytool.dtos.CompleteSurveyDTO;
import iks.surveytool.dtos.SurveyOverviewDTO;
import iks.surveytool.entities.*;
import iks.surveytool.repositories.SurveyRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
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

    public ResponseEntity<SurveyOverviewDTO> processSurveyDTO(CompleteSurveyDTO surveyDTO) {
        Survey newSurvey = mapSurveyToEntity(surveyDTO);
        if (validate(newSurvey)) {
            generateIds(newSurvey);
            Survey savedSurvey = saveSurvey(newSurvey);
            SurveyOverviewDTO completeSurveyDTO = mapSurveyToDTO(savedSurvey);
            return ResponseEntity.ok(completeSurveyDTO);
        } else {
            return ResponseEntity.status(HttpStatus.UNPROCESSABLE_ENTITY).build();
        }
    }

    public ResponseEntity<SurveyOverviewDTO> processSurveyByAccessId(String accessId) {
        SurveyOverviewDTO surveyOverviewDTO = mapSurveyToDTOByAccessId(accessId, true);
        if (surveyOverviewDTO != null) {
            return ResponseEntity.ok(surveyOverviewDTO);
        } else {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        }
    }

    public ResponseEntity<SurveyOverviewDTO> processSurveyByParticipationId(String participationId) {
        SurveyOverviewDTO surveyDTO = mapSurveyToDTOByParticipationId(participationId);
        if (surveyDTO != null) {
            if (surveyDTO instanceof CompleteSurveyDTO) {
                return ResponseEntity.ok(surveyDTO);
            } else {
                // If current time is not within start- and endDate: return survey without questions to fill information
                // in front-end
                return ResponseEntity.status(HttpStatus.FORBIDDEN).body(surveyDTO);
            }
        } else {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        }
    }

    public ResponseEntity<List<SurveyOverviewDTO>> processOpenAccessSurveys() {
        List<SurveyOverviewDTO> openAccessSurveys = mapSurveysToDTOByOpenIsTrue();
        return ResponseEntity.ok(openAccessSurveys);
    }

    private Optional<Survey> findSurveyByParticipationId(String participationId) {
        return surveyRepository.findSurveyByParticipationId(participationId);
    }

    private Optional<Survey> findSurveyByAccessId(String accessId) {
        return surveyRepository.findSurveyByAccessId(accessId);
    }

    private List<Survey> findSurveysByOpenAccessIsTrue() {
        LocalDateTime currentDateTime = LocalDateTime.now();
        return surveyRepository.findSurveysByOpenAccessIsTrueAndEndDateIsAfterOrderByStartDate(currentDateTime);
    }

    private SurveyOverviewDTO mapSurveyToDTO(Survey savedSurvey) {
        return mapper.toSurveyDTO(savedSurvey, true);
    }

    private SurveyOverviewDTO mapSurveyToDTOByAccessId(String accessId, boolean complete) {
        Optional<Survey> surveyOptional = findSurveyByAccessId(accessId);
        if (surveyOptional.isPresent()) {
            Survey survey = surveyOptional.get();
            return mapper.toSurveyDTO(survey, complete);
        }
        return null;
    }

    private SurveyOverviewDTO mapSurveyToDTOByParticipationId(String participationId) {
        Optional<Survey> surveyOptional = findSurveyByParticipationId(participationId);
        if (surveyOptional.isPresent()) {
            Survey survey = surveyOptional.get();
            LocalDateTime surveyStartDate = survey.getStartDate();
            LocalDateTime surveyEndDate = survey.getEndDate();
            LocalDateTime currentDateTime = LocalDateTime.now();
            if (currentDateTime.isAfter(surveyStartDate) && currentDateTime.isBefore(surveyEndDate)) {
                return mapper.toSurveyDTO(survey, true);
            } else {
                // If current time is not within start- and endDate: return survey without questions to fill information
                // in front-end
                return mapper.toSurveyDTO(survey, false);
            }
        } else {
            return null;
        }
    }

    private List<SurveyOverviewDTO> mapSurveysToDTOByOpenIsTrue() {
        List<Survey> openAccessSurveys = findSurveysByOpenAccessIsTrue();
        return mapper.toOpenAccessSurveyDTOList(openAccessSurveys);
    }

    private Survey mapSurveyToEntity(CompleteSurveyDTO surveyDTO) {
        return mapper.toSurveyEntity(surveyDTO);
    }

    // TODO: Rewrite tests to make saveSurvey() validate() private
    public Survey saveSurvey(Survey survey) {
        return surveyRepository.save(survey);
    }

    private void generateIds(Survey survey) {
        LocalDateTime currentDateTime = LocalDateTime.now();
        String accessId = generateUniqueIdWithDateTime(currentDateTime);
        survey.setAccessId(accessId);

        LocalDateTime startDateTime = survey.getStartDate();
        String participateId = generateUniqueIdWithDateTime(startDateTime);
        survey.setParticipationId(participateId);
    }

    private String generateUniqueIdWithDateTime(LocalDateTime currentDateTime) {
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
        return checkIfSurveyComplete(survey) && checkIfDataIsValid(survey);
    }

    private boolean checkIfDataIsValid(Survey survey) {
        List<QuestionGroup> questionGroups = survey.getQuestionGroups();
        return validateSurvey(survey) && validateQuestionGroups(questionGroups);
    }

    private boolean validateSurvey(Survey survey) {
        return checkSurveyNameAndDescription(survey) && checkSurveyTimeframe(survey);
    }

    private boolean checkSurveyNameAndDescription(Survey survey) {
        String name = survey.getName();
        String description = survey.getDescription();
        return name != null
                && name.length() <= 255
                && description.length() <= 3000;
    }

    private boolean checkSurveyTimeframe(Survey survey) {
        LocalDateTime startDate = survey.getStartDate();
        LocalDateTime endDate = survey.getEndDate();
        return startDate != null
                && endDate != null
                && dateTimeInFuture(startDate)
                && dateTimeInFuture(endDate)
                && startDateBeforeEndDate(startDate, endDate);
    }


    private boolean startDateBeforeEndDate(LocalDateTime startDate, LocalDateTime endDate) {
        return startDate.isBefore(endDate);
    }

    private boolean dateTimeInFuture(LocalDateTime dateTime) {
        return dateTime.isAfter(LocalDateTime.now());
    }

    private boolean validateQuestionGroups(List<QuestionGroup> questionGroups) {
        for (QuestionGroup questionGroup : questionGroups) {
            List<Question> questions = questionGroup.getQuestions();
            if (!validateQuestionGroup(questionGroup) || !validateQuestions(questions)) {
                return false;
            }
        }
        return true;
    }

    private boolean validateQuestionGroup(QuestionGroup questionGroup) {
        String title = questionGroup.getTitle();
        return title != null && title.length() <= 255;
    }

    private boolean validateQuestions(List<Question> questions) {
        for (Question question : questions) {
            if (!validateQuestion(question)) {
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

    private boolean validateQuestion(Question question) {
        String text = question.getText();
        return text != null && text.length() <= 500;
    }

    private boolean validateCheckboxGroup(CheckboxGroup checkboxGroup, boolean questionRequired) {
        List<Checkbox> checkboxes = checkboxGroup.getCheckboxes();
        return checkMinMaxSelect(checkboxGroup, questionRequired)
                && checkNumberOfCheckboxes(checkboxGroup)
                && validateCheckboxes(checkboxes);
    }

    private boolean checkMinMaxSelect(CheckboxGroup checkboxGroup, boolean questionRequired) {
        int minSelect = checkboxGroup.getMinSelect();
        int maxSelect = checkboxGroup.getMaxSelect();
        boolean multipleSelect = checkboxGroup.isMultipleSelect();
        return minSelect <= maxSelect
                && minSelect >= 0
                && maxSelect >= 2
                && !(questionRequired && multipleSelect && minSelect < 1);
    }

    private boolean checkNumberOfCheckboxes(CheckboxGroup checkboxGroup) {
        int numberOfCheckboxes = checkboxGroup.getCheckboxes().size();
        int maxSelect = checkboxGroup.getMaxSelect();
        boolean multipleSelect = checkboxGroup.isMultipleSelect();
        return (!multipleSelect && numberOfCheckboxes >= 2) || (multipleSelect && numberOfCheckboxes >= maxSelect);
    }

    private boolean validateCheckboxes(List<Checkbox> checkboxes) {
        for (Checkbox checkbox : checkboxes) {
            if (!validateCheckbox(checkbox)) {
                return false;
            }
        }
        return true;
    }

    private boolean validateCheckbox(Checkbox checkbox) {
        String text = checkbox.getText();
        return text != null && text.length() <= 255;
    }

    private boolean checkIfSurveyComplete(Survey survey) {
        User user = survey.getUser();
        List<QuestionGroup> questionGroups = survey.getQuestionGroups();
        return user != null
                && !questionGroups.isEmpty()
                && checkIfQuestionGroupsComplete(questionGroups);
    }

    private boolean checkIfQuestionGroupsComplete(List<QuestionGroup> questionGroups) {
        for (QuestionGroup questionGroup : questionGroups) {
            List<Question> questions = questionGroup.getQuestions();
            if (questions.isEmpty() || !checkIfQuestionsComplete(questions)) {
                return false;
            }
        }
        return true;
    }

    private boolean checkIfQuestionsComplete(List<Question> questions) {
        for (Question question : questions) {
            if (question.isHasCheckbox()) {
                CheckboxGroup checkboxGroup = question.getCheckboxGroup();
                if (checkboxGroup == null || checkboxGroup.getCheckboxes().isEmpty()) {
                    return false;
                }
            }
        }
        return true;
    }
}
