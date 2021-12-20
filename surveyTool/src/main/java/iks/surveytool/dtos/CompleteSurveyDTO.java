package iks.surveytool.dtos;


import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.List;

@Getter
@Setter
@NoArgsConstructor
public class CompleteSurveyDTO extends SurveyOverviewDTO {

    private List<QuestionGroupDTO> questionGroups;

    public CompleteSurveyDTO(Long id,
                             String name,
                             String description,
                             LocalDateTime startDate,
                             LocalDateTime endDate,
                             boolean openAccess,
                             boolean anonymousParticipation,
                             String accessId,
                             String participationId,
                             Long userId,
                             String userName,
                             List<QuestionGroupDTO> questionGroups) {
        super(id, name, description, startDate, endDate, openAccess, anonymousParticipation, accessId, participationId, userId, userName);
        this.questionGroups = questionGroups;
    }
}
