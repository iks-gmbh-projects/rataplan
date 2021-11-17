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
        UserDTO userDTO = toUserDto(user);

        if (!complete) {
            return new SurveyOverviewDTO(id, name, description, startDate, endDate, open, accessID, uuid, userDTO);
        } else {
            List<QuestionGroup> questionGroups = survey.getQuestionGroups();
            List<QuestionGroupDTO> questionGroupDTOs = toQuestionGroupDtos(questionGroups);
            return new CompleteSurveyDTO(id, name, description, startDate, endDate, open, accessID, uuid, userDTO,
                    questionGroupDTOs);
        }
    }

    public List<UserDTO> toParticipatingUserDtos(List<User> users) {
        List<UserDTO> userDTOs = new ArrayList<>();
        for (User user : users) {
            UserDTO userDTO = toUserDto(user);
            userDTOs.add(userDTO);
        }
        return userDTOs;
    }

    private UserDTO toUserDto(User user) {
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

    public List<AnswerDTO> toAnswerDtos(List<Answer> answers) {
        List<AnswerDTO> answerDTOs = new ArrayList<>();
        for (Answer answer : answers) {
            Long id = answer.getId();
            String text = answer.getText();

            User user = answer.getUser();
            UserDTO userDTO = toUserDto(user);

            Checkbox checkbox = answer.getCheckbox();
            if (checkbox != null) {
                CheckboxDTO checkboxDTO = toCheckboxDto(checkbox);
                answerDTOs.add(new AnswerDTO(id, text, userDTO, checkboxDTO));
            } else {
                answerDTOs.add(new AnswerDTO(id, text, userDTO));
            }
        }
        return answerDTOs;
    }
}
