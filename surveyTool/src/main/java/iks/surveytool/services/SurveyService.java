package iks.surveytool.services;

import iks.surveytool.entities.Question;
import iks.surveytool.entities.QuestionGroup;
import iks.surveytool.entities.Survey;
import iks.surveytool.entities.User;
import iks.surveytool.repositories.SurveyRepository;
import iks.surveytool.repositories.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Optional;
import java.util.Random;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class SurveyService {

    private final SurveyRepository surveyRepository;
    private final UserRepository userRepository;

    public Optional<Survey> findSurveyById(Long surveyID) {
        return surveyRepository.findSurveyById(surveyID);
    }

    public Optional<Survey> findSurveyByUUID(UUID uuid) {
        return surveyRepository.findSurveyByUuid(uuid);
    }

    public Optional<Survey> findSurveyByAccessId(String accessId) {
        return surveyRepository.findSurveyByAccessID(accessId);
    }

    public Long saveSurvey(Survey survey) {
        setCheckboxGroupForeignKeys(survey);
        // Need to fetch user from db for hibernate to recognize it
        setUser(survey);

        Survey savedSurvey = surveyRepository.save(survey);
        generateAccessID(savedSurvey);
        generateUUID(savedSurvey);
        savedSurvey = surveyRepository.save(savedSurvey);
        return savedSurvey.getId();
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
