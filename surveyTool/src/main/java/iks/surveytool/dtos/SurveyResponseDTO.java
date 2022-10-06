package iks.surveytool.dtos;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.Map;

@Getter
@Setter
@NoArgsConstructor
public class SurveyResponseDTO extends AbstractDTO {
    private long surveyId;
    private Long userId;
    private Map<Long, AnswerDTO> answers;
}
