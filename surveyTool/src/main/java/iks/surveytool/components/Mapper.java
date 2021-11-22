package iks.surveytool.components;

import iks.surveytool.dtos.*;
import iks.surveytool.entities.*;
import iks.surveytool.repositories.CheckboxRepository;
import iks.surveytool.repositories.QuestionRepository;
import iks.surveytool.repositories.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Component
@RequiredArgsConstructor
public class Mapper {

    private final QuestionRepository questionRepository;
    private final CheckboxRepository checkboxRepository;
    private final UserRepository userRepository;

    // User/-DTO:
    public List<UserDTO> toParticipatingUserDtoList(List<User> users) {
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

    public User createUserFromDto(UserDTO userDTO) {
        String name = userDTO.getName();
        return new User(name);
    }

    // Survey/-DTO (and Classes that "belong" to Survey):
    public List<SurveyOverviewDTO> toOpenAccessSurveyDtoList(List<Survey> openAccessSurveys) {
        List<SurveyOverviewDTO> openAccessSurveyDtos = new ArrayList<>();
        for (Survey survey : openAccessSurveys) {
            SurveyOverviewDTO surveyOverviewDTO = toSurveyDto(survey, false);
            openAccessSurveyDtos.add(surveyOverviewDTO);
        }
        return openAccessSurveyDtos;
    }

    public SurveyOverviewDTO toSurveyDto(Survey survey, boolean complete) {
        Long id = survey.getId();
        String name = survey.getName();
        String description = survey.getDescription();
        LocalDateTime startDate = survey.getStartDate();
        LocalDateTime endDate = survey.getEndDate();
        boolean openAccess = survey.isOpenAccess();
        String accessId = survey.getAccessId();
        String participationId = survey.getParticipationId();

        User user = survey.getUser();
        Long userId;
        String userName;
        if (user != null) {
            userId = user.getId();
            userName = user.getName();
        } else {
            userId = null;
            userName = "Anonym";
        }

        if (complete) {
            List<QuestionGroup> questionGroups = survey.getQuestionGroups();
            List<QuestionGroupDTO> questionGroupDTOs = toQuestionGroupDtoList(questionGroups);
            return new CompleteSurveyDTO(id, name, description, startDate, endDate, openAccess, accessId, participationId, userId, userName,
                    questionGroupDTOs);
        } else {
            return new SurveyOverviewDTO(id, name, description, startDate, endDate, openAccess, accessId, participationId, userId, userName);
        }
    }

    private List<QuestionGroupDTO> toQuestionGroupDtoList(List<QuestionGroup> questionGroups) {
        List<QuestionGroupDTO> questionGroupDTOs = new ArrayList<>();
        for (QuestionGroup questionGroup : questionGroups) {
            QuestionGroupDTO questionGroupDTO = toQuestionGroupDto(questionGroup);
            questionGroupDTOs.add(questionGroupDTO);
        }
        return questionGroupDTOs;
    }

    private QuestionGroupDTO toQuestionGroupDto(QuestionGroup questionGroup) {
        Long id = questionGroup.getId();
        String title = questionGroup.getTitle();
        List<Question> questions = questionGroup.getQuestions();
        List<QuestionDTO> questionDTOs = toQuestionDtoList(questions);

        return new QuestionGroupDTO(id, title, questionDTOs);
    }

    private List<QuestionDTO> toQuestionDtoList(List<Question> questions) {
        List<QuestionDTO> questionDTOs = new ArrayList<>();
        for (Question question : questions) {
            QuestionDTO questionDTO = toQuestionDto(question);
            questionDTOs.add(questionDTO);
        }
        return questionDTOs;
    }

    private QuestionDTO toQuestionDto(Question question) {
        Long id = question.getId();
        String text = question.getText();
        boolean required = question.isRequired();
        boolean hasCheckbox = question.isHasCheckbox();

        QuestionDTO questionDTO;

        if (hasCheckbox) {
            CheckboxGroup checkboxGroup = question.getCheckboxGroup();
            CheckboxGroupDTO checkboxGroupDTO = toCheckboxGroupDto(checkboxGroup);
            questionDTO = new QuestionDTO(id, text, required, hasCheckbox, checkboxGroupDTO);
        } else {
            questionDTO = new QuestionDTO(id, text, required, hasCheckbox);
        }
        return questionDTO;
    }

    private CheckboxGroupDTO toCheckboxGroupDto(CheckboxGroup checkboxGroup) {
        Long id = checkboxGroup.getId();
        boolean multipleSelect = checkboxGroup.isMultipleSelect();
        int minSelect = checkboxGroup.getMinSelect();
        int maxSelect = checkboxGroup.getMaxSelect();

        List<Checkbox> checkboxes = checkboxGroup.getCheckboxes();
        List<CheckboxDTO> checkboxDTOs = toCheckboxDtoList(checkboxes);

        return new CheckboxGroupDTO(id, multipleSelect, minSelect, maxSelect, checkboxDTOs);
    }

    private List<CheckboxDTO> toCheckboxDtoList(List<Checkbox> checkboxes) {
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
        List<QuestionGroup> questionGroups = createQuestionGroupsFromDtoList(surveyDTO.getQuestionGroups());

        String name = surveyDTO.getName();
        String description = surveyDTO.getDescription();
        LocalDateTime startDate = surveyDTO.getStartDate();
        LocalDateTime endDate = surveyDTO.getEndDate();
        boolean openAccess = surveyDTO.isOpenAccess();
        String accessId = surveyDTO.getAccessId();
        String participationId = surveyDTO.getParticipationId();

        Survey newSurvey = new Survey(name, description, startDate, endDate, openAccess, accessId, participationId, questionGroups);

        // Need to fetch user from db for hibernate to recognize it
        Long userId = surveyDTO.getUserId();
        Optional<User> userOptional = userRepository.findById(userId);
        if (userOptional.isPresent()) {
            User user = userOptional.get();
            newSurvey.setUser(user);
        }

        return newSurvey;
    }

    private List<QuestionGroup> createQuestionGroupsFromDtoList(List<QuestionGroupDTO> questionGroupDTOs) {
        List<QuestionGroup> questionGroups = new ArrayList<>();
        for (QuestionGroupDTO questionGroupDTO : questionGroupDTOs) {
            QuestionGroup questionGroup = createQuestionGroupFromDto(questionGroupDTO);
            questionGroups.add(questionGroup);
        }
        return questionGroups;
    }

    private QuestionGroup createQuestionGroupFromDto(QuestionGroupDTO questionGroupDTO) {
        String title = questionGroupDTO.getTitle();
        List<QuestionDTO> questionDTOs = questionGroupDTO.getQuestions();
        List<Question> questions = createQuestionsFromDtoList(questionDTOs);

        return new QuestionGroup(title, questions);
    }

    private List<Question> createQuestionsFromDtoList(List<QuestionDTO> questionDTOs) {
        List<Question> questions = new ArrayList<>();
        for (QuestionDTO questionDTO : questionDTOs) {
            Question question = createQuestionFromDto(questionDTO);
            questions.add(question);
        }
        return questions;
    }

    private Question createQuestionFromDto(QuestionDTO questionDTO) {
        String text = questionDTO.getText();
        boolean required = questionDTO.isRequired();
        boolean hasCheckbox = questionDTO.isHasCheckbox();

        Question question = new Question(text, required, hasCheckbox);

        if (hasCheckbox) {
            CheckboxGroupDTO checkboxGroupDTO = questionDTO.getCheckboxGroup();
            CheckboxGroup checkboxGroup = createCheckboxGroupFromDto(checkboxGroupDTO);
            // Set references for the one-to-one-relationship
            checkboxGroup.setQuestion(question);
            question.setCheckboxGroup(checkboxGroup);
        }

        return question;
    }

    private CheckboxGroup createCheckboxGroupFromDto(CheckboxGroupDTO checkboxGroupDTO) {
        boolean multipleSelect = checkboxGroupDTO.isMultipleSelect();
        int minSelect = checkboxGroupDTO.getMinSelect();
        int maxSelect = checkboxGroupDTO.getMaxSelect();

        List<CheckboxDTO> checkboxDTOs = checkboxGroupDTO.getCheckboxes();
        List<Checkbox> checkboxes = createCheckboxesFromDtoList(checkboxDTOs);

        return new CheckboxGroup(multipleSelect, minSelect, maxSelect, checkboxes);
    }

    private List<Checkbox> createCheckboxesFromDtoList(List<CheckboxDTO> checkboxDTOs) {
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
    public List<AnswerDTO> toAnswerDtoList(List<Answer> answers) {
        List<AnswerDTO> answerDTOs = new ArrayList<>();
        for (Answer answer : answers) {
            AnswerDTO answerDTO = toAnswerDto(answer);
            answerDTOs.add(answerDTO);
        }
        return answerDTOs;
    }

    private AnswerDTO toAnswerDto(Answer answer) {
        Long id = answer.getId();
        String text = answer.getText();

        User user = answer.getUser();
        Long userId = user.getId();
        String userName = user.getName();

        AnswerDTO answerDTO;

        Checkbox checkbox = answer.getCheckbox();
        if (checkbox != null) {
            Long checkboxId = checkbox.getId();
            answerDTO = new AnswerDTO(id, text, userId, userName, checkboxId);
        } else {
            answerDTO = new AnswerDTO(id, text, userId, userName);
        }
        return answerDTO;
    }

    public Answer createAnswerFromDto(AnswerDTO answerDTO) {
        String text = answerDTO.getText();

        Answer answer = new Answer(text);

        Long userId = answerDTO.getUserId();
        Optional<User> userOptional = userRepository.findById(userId);
        if (userOptional.isPresent()) {
            User user = userOptional.get();
            answer.setUser(user);
        }

        Long questionId = answerDTO.getQuestionId();
        Optional<Question> questionOptional = questionRepository.findById(questionId);
        if (questionOptional.isPresent()) {
            Question question = questionOptional.get();
            answer.setQuestion(question);
        }

        Long checkboxId = answerDTO.getCheckboxId();
        if (checkboxId != null) {
            Optional<Checkbox> checkboxOptional = checkboxRepository.findById(checkboxId);
            if (checkboxOptional.isPresent()) {
                Checkbox checkbox = checkboxOptional.get();
                answer.setCheckbox(checkbox);
            }
        }

        return answer;
    }
}
