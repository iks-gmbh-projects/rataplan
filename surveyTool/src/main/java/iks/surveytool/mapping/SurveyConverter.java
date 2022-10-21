package iks.surveytool.mapping;

import iks.surveytool.dtos.*;
import iks.surveytool.entities.*;
import lombok.RequiredArgsConstructor;
import org.modelmapper.AbstractConverter;
import org.springframework.stereotype.Component;

import java.time.Instant;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Component
@RequiredArgsConstructor
public class SurveyConverter extends AbstractConverter<CompleteSurveyDTO, Survey> {
    @Override
    protected Survey convert(CompleteSurveyDTO source) {
        return toSurveyEntity(source);
    }

    private static ZonedDateTime toLocalZoneDateTime(Instant instant) {
        if(instant == null) return null;
        return instant.atZone(ZoneId.systemDefault());
    }

    public Survey toSurveyEntity(CompleteSurveyDTO surveyDTO) {
        List<QuestionGroup> questionGroups = toQuestionGroupEntityList(surveyDTO.getQuestionGroups());

        Survey newSurvey = new Survey(
                surveyDTO.getName(),
                surveyDTO.getDescription(),
                toLocalZoneDateTime(surveyDTO.getStartDate()),
                toLocalZoneDateTime(surveyDTO.getEndDate()),
                surveyDTO.isOpenAccess(),
                surveyDTO.isAnonymousParticipation(),
                surveyDTO.getAccessId(),
                surveyDTO.getParticipationId(),
                questionGroups
        );

        newSurvey.setId(surveyDTO.getId());
        newSurvey.setUserId(surveyDTO.getUserId());

        return newSurvey;
    }

    private List<QuestionGroup> toQuestionGroupEntityList(List<QuestionGroupDTO> questionGroupDTOs) {
        return questionGroupDTOs.stream().map(this::toQuestionGroupEntity).collect(Collectors.toList());
    }

    private QuestionGroup toQuestionGroupEntity(QuestionGroupDTO questionGroupDTO) {
        return new QuestionGroup(questionGroupDTO.getTitle(), toQuestionEntityList(questionGroupDTO.getQuestions()));
    }

    private List<Question> toQuestionEntityList(List<QuestionDTO> questionDTOs) {
        return questionDTOs.stream().map(this::toQuestionEntity).collect(Collectors.toList());
    }

    private Question toQuestionEntity(QuestionDTO questionDTO) {
        Question question = new Question(questionDTO.getText(), questionDTO.isRequired(), questionDTO.isHasCheckbox());

        if (questionDTO.isHasCheckbox()) {
            CheckboxGroup checkboxGroup = toCheckboxGroupEntity(questionDTO.getCheckboxGroup());
            // Set references for the one-to-one-relationship
            checkboxGroup.setQuestion(question);
            question.setCheckboxGroup(checkboxGroup);
        }

        return question;
    }

    private CheckboxGroup toCheckboxGroupEntity(CheckboxGroupDTO checkboxGroupDTO) {
        return new CheckboxGroup(checkboxGroupDTO.isMultipleSelect(), checkboxGroupDTO.getMinSelect(),
                checkboxGroupDTO.getMaxSelect(), toCheckboxEntityList(checkboxGroupDTO.getCheckboxes()));
    }

    private List<Checkbox> toCheckboxEntityList(List<CheckboxDTO> checkboxDTOs) {
        return checkboxDTOs.stream().map(this::toCheckboxEntity).collect(Collectors.toList());
    }

    private Checkbox toCheckboxEntity(CheckboxDTO checkboxDTO) {
        return new Checkbox(checkboxDTO.getText(), checkboxDTO.isHasTextField());
    }

}
