package iks.surveytool.mapping;

import iks.surveytool.dtos.SurveyResponseDTO;
import iks.surveytool.entities.Answer;
import iks.surveytool.entities.Question;
import iks.surveytool.entities.SurveyResponse;
import iks.surveytool.mapping.crypto.ToEncryptedStringConverter;
import iks.surveytool.repositories.CheckboxRepository;
import iks.surveytool.repositories.QuestionRepository;
import iks.surveytool.repositories.SurveyRepository;
import lombok.RequiredArgsConstructor;
import org.modelmapper.AbstractConverter;
import org.springframework.stereotype.Component;

import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

@Component
@RequiredArgsConstructor
public class SurveyResponseConverter extends AbstractConverter<SurveyResponseDTO, SurveyResponse> {

    private final QuestionRepository questionRepository;
    private final CheckboxRepository checkboxRepository;
    private final SurveyRepository surveyRepository;
    private final ToEncryptedStringConverter toEncryptedStringConverter;

    @Override
    protected SurveyResponse convert(SurveyResponseDTO source) {
        return toSurveyResponseEntity(source);
    }

    private SurveyResponse toSurveyResponseEntity(SurveyResponseDTO surveyResponseDTO) {
        SurveyResponse response = new SurveyResponse();
        response.setSurvey(surveyRepository.getById(surveyResponseDTO.getSurveyId()));
        response.setUserId(surveyResponseDTO.getUserId());
        response.setAnswers(surveyResponseDTO
                .getAnswers()
                .entrySet()
                .stream()
                .map(answerDTO -> {

                    String text = answerDTO.getValue().getText();

                    Answer answer = new Answer(toEncryptedStringConverter.convert(text));
                    answer.setId(answerDTO.getValue().getId());
                    answer.setResponse(response);

                    Long questionId = answerDTO.getKey();
                    if (questionId != null) {
                        Optional<Question> questionOptional = questionRepository.findById(questionId);
                        if (questionOptional.isPresent()) {
                            Question question = questionOptional.get();
                            answer.setQuestion(question);
                        }
                    }

                    Map<Long, Boolean> checkboxes = answerDTO.getValue().getCheckboxes();
                    if (checkboxes != null) {
                        answer.setCheckboxes(
                                checkboxes
                                        .entrySet()
                                        .stream()
                                        .filter(e -> Boolean.TRUE.equals(e.getValue()))
                                        .map(Map.Entry::getKey)
                                        .map(checkboxRepository::getById)
                                        .collect(Collectors.toList())
                        );
                    }
                    return answer;
                })
                .collect(Collectors.toList())
        );
        return response;
    }
}
