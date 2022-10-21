package iks.surveytool.mapping;

import iks.surveytool.dtos.AnswerDTO;
import iks.surveytool.dtos.SurveyResponseDTO;
import iks.surveytool.entities.Checkbox;
import iks.surveytool.entities.SurveyResponse;
import lombok.RequiredArgsConstructor;
import org.modelmapper.AbstractConverter;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.stream.Collectors;

@Component
@RequiredArgsConstructor
public class SurveyResponseDTOConverter extends AbstractConverter<SurveyResponse, SurveyResponseDTO> {

    @Override
    protected SurveyResponseDTO convert(SurveyResponse source) {
        return toSurveyResponseDTO(source);
    }

    private SurveyResponseDTO toSurveyResponseDTO(final SurveyResponse surveyResponse) {
        SurveyResponseDTO response = new SurveyResponseDTO();
        response.setId(surveyResponse.getId());
        response.setSurveyId(surveyResponse.getSurvey().getId());
        response.setUserId(surveyResponse.getUserId());
        response.setAnswers(surveyResponse
                .getAnswers()
                .stream()
                .collect(Collectors.toMap(
                        answer -> answer.getQuestion().getId(),
                        answer -> {

                            String text = answer.getText();

                            AnswerDTO answerDTO = new AnswerDTO(answer.getId(), text);

                            List<Checkbox> checkboxes = answer.getCheckboxes();
                            if (checkboxes != null) answerDTO.setCheckboxes(
                                    checkboxes.stream()
                                            .collect(Collectors.toMap(Checkbox::getId, c -> true))
                            );

                            return answerDTO;
                        })
                ));
        return response;
    }
}
