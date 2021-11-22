package iks.surveytool.dtos;


import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.List;

@Getter
@Setter
public class CompleteSurveyDTO extends SurveyOverviewDTO {

    private List<QuestionGroupDTO> questionGroups;

    public CompleteSurveyDTO(Long id,
                             String name,
                             String description,
                             LocalDateTime startDate,
                             LocalDateTime endDate,
                             boolean openAccess,
                             String accessId,
                             String participationId,
                             Long userId,
                             String userName,
                             List<QuestionGroupDTO> questionGroups) {
        super(id, name, description, startDate, endDate, openAccess, accessId, participationId, userId, userName);
        this.questionGroups = questionGroups;
    }
}
