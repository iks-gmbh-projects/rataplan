package iks.surveytool.components;

import iks.surveytool.dtos.*;
import iks.surveytool.entities.*;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Component
public class Mapper {
    // User/-DTO:
    public List<UserDTO> toParticipatingUserDtos(List<User> users) {
        List<UserDTO> userDTOs = new ArrayList<>();
        for (User user : users) {
            UserDTO userDTO = toUserDto(user);
            userDTOs.add(userDTO);
        }
        return userDTOs;
    }

    public UserDTO toUserDto(User user) {
        Long userId;
        String userName;
        if (user != null) {
            userId = user.getId();
            userName = user.getName();
        } else {
            userId = null;
            userName = "Anonym";
        }
        return new UserDTO(userId, userName);
    }

    // Survey/-DTO (and Classes that belong to Survey):
    public List<SurveyOverviewDTO> toOpenAccessSurveyDtos(List<Survey> openAccessSurveys) {
        List<SurveyOverviewDTO> openAccessSurveyDtos = new ArrayList<>();
        for (Survey survey : openAccessSurveys) {
            openAccessSurveyDtos.add(toSurveyDto(survey, false));
        }
        return openAccessSurveyDtos;
    }

    public SurveyOverviewDTO toSurveyDto(Survey survey, boolean complete) {
        Long id = survey.getId();
        String name = survey.getName();
        String description = survey.getDescription();
        LocalDateTime startDate = survey.getStartDate();
        LocalDateTime endDate = survey.getEndDate();
        boolean open = survey.isOpen();
        String accessID = survey.getAccessID();
        UUID uuid = survey.getUuid();

        User user = survey.getUser();
        Long userID;
        if (user != null) {
            userID = user.getId();
        } else {
            userID = null;
        }

        if (!complete) {
            return new SurveyOverviewDTO(id, name, description, startDate, endDate, open, accessID, uuid, userID);
        } else {
            List<QuestionGroup> questionGroups = survey.getQuestionGroups();
            List<QuestionGroupDTO> questionGroupDTOs = toQuestionGroupDtos(questionGroups);
            return new CompleteSurveyDTO(id, name, description, startDate, endDate, open, accessID, uuid, userID,
                    questionGroupDTOs);
        }
    }

    private List<QuestionGroupDTO> toQuestionGroupDtos(List<QuestionGroup> questionGroups) {
        List<QuestionGroupDTO> questionGroupDTOs = new ArrayList<>();
        for (QuestionGroup questionGroup : questionGroups) {
            Long id = questionGroup.getId();
            String title = questionGroup.getTitle();
            List<Question> questions = questionGroup.getQuestions();
            List<QuestionDTO> questionDTOs = toQuestionDtos(questions);

            questionGroupDTOs.add(new QuestionGroupDTO(id, title, questionDTOs));
        }
        return questionGroupDTOs;
    }

    private List<QuestionDTO> toQuestionDtos(List<Question> questions) {
        List<QuestionDTO> questionDTOs = new ArrayList<>();
        for (Question question : questions) {
            Long id = question.getId();
            String text = question.getText();
            boolean required = question.isRequired();
            boolean hasCheckbox = question.isHasCheckbox();

            if (hasCheckbox) {
                CheckboxGroupDTO checkboxGroupDTO = toCheckboxGroupDto(question.getCheckboxGroup());
                questionDTOs.add(new QuestionDTO(id, text, required, hasCheckbox, checkboxGroupDTO));
            } else {
                questionDTOs.add(new QuestionDTO(id, text, required, hasCheckbox));
            }

        }
        return questionDTOs;
    }

    private CheckboxGroupDTO toCheckboxGroupDto(CheckboxGroup checkboxGroup) {
        Long id = checkboxGroup.getId();
        boolean multipleSelect = checkboxGroup.isMultipleSelect();
        int minSelect = checkboxGroup.getMinSelect();
        int maxSelect = checkboxGroup.getMaxSelect();

        List<Checkbox> checkboxes = checkboxGroup.getCheckboxes();
        List<CheckboxDTO> checkboxDTOs = toCheckboxDtos(checkboxes);

        return new CheckboxGroupDTO(id, multipleSelect, minSelect, maxSelect, checkboxDTOs);
    }

    private List<CheckboxDTO> toCheckboxDtos(List<Checkbox> checkboxes) {
        List<CheckboxDTO> checkboxDTOs = new ArrayList<>();
        for (Checkbox checkbox : checkboxes) {
            CheckboxDTO checkboxDTO = toCheckboxDto(checkbox);
            checkboxDTOs.add(checkboxDTO);
        }
        return checkboxDTOs;
    }

    private CheckboxDTO toCheckboxDto(Checkbox checkbox) {
        Long id = checkbox.getId();
        String text = checkbox.getText();
        boolean hasTextField = checkbox.isHasTextField();

        return new CheckboxDTO(id, text, hasTextField);
    }

    public Survey createSurveyFromDto(CompleteSurveyDTO surveyDTO) {
        List<QuestionGroup> questionGroups = createQuestionGroupsFromDtos(surveyDTO.getQuestionGroups());

        String name = surveyDTO.getName();
        String description = surveyDTO.getDescription();
        LocalDateTime startDate = surveyDTO.getStartDate();
        LocalDateTime endDate = surveyDTO.getEndDate();
        boolean open = surveyDTO.isOpen();
        String accessID = surveyDTO.getAccessID();
        UUID uuid = surveyDTO.getUuid();

        return new Survey(name, description, startDate, endDate, open, accessID, uuid, questionGroups);
    }

    private List<QuestionGroup> createQuestionGroupsFromDtos(List<QuestionGroupDTO> questionGroupDTOs) {
        List<QuestionGroup> questionGroups = new ArrayList<>();
        for (QuestionGroupDTO questionGroupDTO : questionGroupDTOs) {
            String title = questionGroupDTO.getTitle();
            List<Question> questions = createQuestionsFromDtos(questionGroupDTO.getQuestions());

            questionGroups.add(new QuestionGroup(title, questions));
        }
        return questionGroups;
    }

    private List<Question> createQuestionsFromDtos(List<QuestionDTO> questionDTOs) {
        List<Question> questions = new ArrayList<>();
        for (QuestionDTO questionDTO : questionDTOs) {
            String text = questionDTO.getText();
            boolean required = questionDTO.isRequired();
            boolean hasCheckbox = questionDTO.isHasCheckbox();

            if (hasCheckbox) {
                CheckboxGroupDTO checkboxGroupDTO = questionDTO.getCheckboxGroup();
                CheckboxGroup checkboxGroup = createCheckboxGroupFromDtos(checkboxGroupDTO);
                questions.add(new Question(text, required, hasCheckbox, checkboxGroup));
            } else {
                questions.add(new Question(text, required, hasCheckbox));
            }
        }
        return questions;
    }

    private CheckboxGroup createCheckboxGroupFromDtos(CheckboxGroupDTO checkboxGroupDTO) {
        boolean multipleSelect = checkboxGroupDTO.isMultipleSelect();
        int minSelect = checkboxGroupDTO.getMinSelect();
        int maxSelect = checkboxGroupDTO.getMaxSelect();

        List<CheckboxDTO> checkboxDTOs = checkboxGroupDTO.getCheckboxes();
        List<Checkbox> checkboxes = createCheckboxesFromDtos(checkboxDTOs);

        return new CheckboxGroup(multipleSelect, minSelect, maxSelect, checkboxes);
    }

    private List<Checkbox> createCheckboxesFromDtos(List<CheckboxDTO> checkboxDTOs) {
        List<Checkbox> checkboxes = new ArrayList<>();
        for (CheckboxDTO checkboxDTO : checkboxDTOs) {
            Checkbox checkbox = createCheckboxFromDto(checkboxDTO);
            checkboxes.add(checkbox);
        }
        return checkboxes;
    }

    private Checkbox createCheckboxFromDto(CheckboxDTO checkboxDTO) {
        String text = checkboxDTO.getText();
        boolean hasTextField = checkboxDTO.isHasTextField();

        return new Checkbox(text, hasTextField);
    }

    // Answer/-DTO:
    public List<AnswerDTO> toAnswerDtos(List<Answer> answers) {
        List<AnswerDTO> answerDTOs = new ArrayList<>();
        for (Answer answer : answers) {
            Long id = answer.getId();
            String text = answer.getText();

            User user = answer.getUser();
            Long userID = user.getId();

            Checkbox checkbox = answer.getCheckbox();
            if (checkbox != null) {
                Long checkboxID = checkbox.getId();
                answerDTOs.add(new AnswerDTO(id, text, userID, checkboxID));
            } else {
                answerDTOs.add(new AnswerDTO(id, text, userID));
            }
        }
        return answerDTOs;
    }

    public Answer createAnswerFromDto(AnswerDTO answerDTO) {
        String text = answerDTO.getText();
        return new Answer(text);
    }
}
