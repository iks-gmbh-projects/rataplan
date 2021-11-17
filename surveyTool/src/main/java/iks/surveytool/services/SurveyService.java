package iks.surveytool.services;

import iks.surveytool.components.Mapper;
import iks.surveytool.dtos.CompleteSurveyDTO;
import iks.surveytool.dtos.SurveyOverviewDTO;
import iks.surveytool.entities.Question;
import iks.surveytool.entities.QuestionGroup;
import iks.surveytool.entities.Survey;
import iks.surveytool.entities.User;
import iks.surveytool.repositories.SurveyRepository;
import iks.surveytool.repositories.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.Random;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class SurveyService {

    private final SurveyRepository surveyRepository;
    private final UserRepository userRepository;
    private final Mapper mapper;

    public Optional<Survey> findSurveyById(Long surveyID) {
        return surveyRepository.findSurveyById(surveyID);
    }

    public Optional<Survey> findSurveyByUUID(UUID uuid) {
        return surveyRepository.findSurveyByUuid(uuid);
    }

    public Optional<Survey> findSurveyByAccessId(String accessId) {
        return surveyRepository.findSurveyByAccessID(accessId);
    }

    public List<Survey> findSurveysByOpenIsTrue() {
        LocalDateTime currentDateTime = LocalDateTime.now();
        return surveyRepository.findSurveysByOpenIsTrueAndEndDateIsAfterOrderByStartDate(currentDateTime);
    }

    public Survey createSurveyFromDto(CompleteSurveyDTO surveyDTO) {
        Survey newSurvey = mapper.createSurveyFromDto(surveyDTO);

        // Need to fetch user from db for hibernate to recognize it
        Long userID = surveyDTO.getUserID();
        Optional<User> userOptional = userRepository.findById(userID);
        if (userOptional.isPresent()) {
            User user = userOptional.get();
            newSurvey.setUser(user);
        }

        return newSurvey;
    }

    public SurveyOverviewDTO createSurveyDtoFromSurvey(Survey savedSurvey) {
        return mapper.toSurveyDto(savedSurvey, true);
    }

    public SurveyOverviewDTO createSurveyDtoById(Long surveyID, boolean complete) {
        Optional<Survey> surveyOptional = findSurveyById(surveyID);
        if (surveyOptional.isPresent()) {
            Survey survey = surveyOptional.get();
            return mapper.toSurveyDto(survey, complete);
        }
        return null;
    }

    public SurveyOverviewDTO createSurveyDtoByAccessId(String accessId, boolean complete) {
        Optional<Survey> surveyOptional = findSurveyByAccessId(accessId);
        if (surveyOptional.isPresent()) {
            Survey survey = surveyOptional.get();
            return mapper.toSurveyDto(survey, complete);
        }
        return null;
    }

    public SurveyOverviewDTO createSurveyDtoByUUID(UUID uuid) {
        Optional<Survey> surveyOptional = findSurveyByUUID(uuid);
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
        List<Survey> openAccessSurveys = findSurveysByOpenIsTrue();
        return mapper.toOpenAccessSurveyDtos(openAccessSurveys);

    }

    public Survey saveSurvey(Survey survey) {
        setCheckboxGroupForeignKeys(survey);
        Survey savedSurvey = surveyRepository.save(survey);
        generateAccessID(savedSurvey);
        generateUUID(savedSurvey);
        savedSurvey = surveyRepository.save(savedSurvey);
        return savedSurvey;
    }

    private void setUser(Survey survey) {
        Long userID = survey.getUser().getId();
        Optional<User> userOptional = userRepository.findById(userID);

        if (userOptional.isPresent()) {
            User user = userOptional.get();
            survey.setUser(user);
        }
    }

    private void setCheckboxGroupForeignKeys(Survey survey) {
        for (QuestionGroup questionGroup : survey.getQuestionGroups()) {
            for (Question question : questionGroup.getQuestions()) {
                if (question.isHasCheckbox()) {
                    question.getCheckboxGroup().setQuestion(question);
                }
            }
        }
    }

    // Temporary:
    public void generateAccessID(Survey survey) {
        Random random = new Random();
        String accessID = "";
        accessID += survey.getStartDate().getYear() + "-" + random.nextInt(10) + "-" + survey.getId();
        survey.setAccessID(accessID);
    }

    private void generateUUID(Survey survey) {
        UUID uuid = UUID.randomUUID();
        survey.setUuid(uuid);
    }
}
