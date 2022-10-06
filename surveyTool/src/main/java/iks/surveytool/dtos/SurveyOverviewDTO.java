package iks.surveytool.dtos;

import lombok.*;

import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class SurveyOverviewDTO extends AbstractDTO {

    private String name;
    private String description;

    private LocalDateTime startDate;
    private LocalDateTime endDate;

    private boolean openAccess;
    private boolean anonymousParticipation;
    private String accessId;
    private String participationId;

    private Long userId;

}
