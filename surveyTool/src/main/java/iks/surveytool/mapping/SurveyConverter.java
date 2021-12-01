package iks.surveytool.mapping;

import iks.surveytool.dtos.*;
import iks.surveytool.entities.*;
import iks.surveytool.repositories.UserRepository;
import lombok.RequiredArgsConstructor;
import org.modelmapper.AbstractConverter;
import org.modelmapper.Converter;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Component
@RequiredArgsConstructor
public class SurveyConverter {
    private final UserRepository userRepository;

    public Converter<CompleteSurveyDTO, Survey> toDAO = new AbstractConverter<CompleteSurveyDTO, Survey>() {
        @Override
        protected Survey convert(CompleteSurveyDTO source) {
            return toSurveyEntity(source);
        }

        public Survey toSurveyEntity(CompleteSurveyDTO surveyDTO) {
            List<QuestionGroup> questionGroups = toQuestionGroupEntityList(surveyDTO.getQuestionGroups());

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
            if (userId != null) {
                Optional<User> userOptional = userRepository.findById(userId);
                if (userOptional.isPresent()) {
                    User user = userOptional.get();
                    newSurvey.setUser(user);
                }
            }

            return newSurvey;
        }

        private List<QuestionGroup> toQuestionGroupEntityList(List<QuestionGroupDTO> questionGroupDTOs) {
            List<QuestionGroup> questionGroups = new ArrayList<>();
            for (QuestionGroupDTO questionGroupDTO : questionGroupDTOs) {
                QuestionGroup questionGroup = toQuestionGroupEntity(questionGroupDTO);
                questionGroups.add(questionGroup);
            }
            return questionGroups;
        }

        private QuestionGroup toQuestionGroupEntity(QuestionGroupDTO questionGroupDTO) {
            String title = questionGroupDTO.getTitle();
            List<QuestionDTO> questionDTOs = questionGroupDTO.getQuestions();
            List<Question> questions = toQuestionEntityList(questionDTOs);

            return new QuestionGroup(title, questions);
        }

        private List<Question> toQuestionEntityList(List<QuestionDTO> questionDTOs) {
            List<Question> questions = new ArrayList<>();
            for (QuestionDTO questionDTO : questionDTOs) {
                Question question = toQuestionEntity(questionDTO);
                questions.add(question);
            }
            return questions;
        }

        private Question toQuestionEntity(QuestionDTO questionDTO) {
            String text = questionDTO.getText();
            boolean required = questionDTO.isRequired();
            boolean hasCheckbox = questionDTO.isHasCheckbox();

            Question question = new Question(text, required, hasCheckbox);

            if (hasCheckbox) {
                CheckboxGroupDTO checkboxGroupDTO = questionDTO.getCheckboxGroup();
                CheckboxGroup checkboxGroup = toCheckboxGroupEntity(checkboxGroupDTO);
                // Set references for the one-to-one-relationship
                checkboxGroup.setQuestion(question);
                question.setCheckboxGroup(checkboxGroup);
            }

            return question;
        }

        private CheckboxGroup toCheckboxGroupEntity(CheckboxGroupDTO checkboxGroupDTO) {
            boolean multipleSelect = checkboxGroupDTO.isMultipleSelect();
            int minSelect = checkboxGroupDTO.getMinSelect();
            int maxSelect = checkboxGroupDTO.getMaxSelect();

            List<CheckboxDTO> checkboxDTOs = checkboxGroupDTO.getCheckboxes();
            List<Checkbox> checkboxes = toCheckboxEntityList(checkboxDTOs);

            return new CheckboxGroup(multipleSelect, minSelect, maxSelect, checkboxes);
        }

        private List<Checkbox> toCheckboxEntityList(List<CheckboxDTO> checkboxDTOs) {
            List<Checkbox> checkboxes = new ArrayList<>();
            for (CheckboxDTO checkboxDTO : checkboxDTOs) {
                Checkbox checkbox = toCheckboxEntity(checkboxDTO);
                checkboxes.add(checkbox);
            }
            return checkboxes;
        }

        private Checkbox toCheckboxEntity(CheckboxDTO checkboxDTO) {
            String text = checkboxDTO.getText();
            boolean hasTextField = checkboxDTO.isHasTextField();

            return new Checkbox(text, hasTextField);
        }
    };

}
