package iks.surveytool.dtos;

import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
public class SurveyOverviewDTO {

    private Long id;
    private String name;
    private String description;

    private LocalDateTime startDate;
    private LocalDateTime endDate;

    private boolean open;
    private String accessId;
    private String participationId;

    private Long userId;
    private String userName;

    public SurveyOverviewDTO(Long id,
                             String name,
                             String description,
                             LocalDateTime startDate,
                             LocalDateTime endDate,
                             boolean open,
                             String accessId,
                             String participationId,
                             Long userId,
                             String userName) {
        this.id = id;
        this.name = name;
        this.description = description;
        this.startDate = startDate;
        this.endDate = endDate;
        this.open = open;
        this.accessId = accessId;
        this.participationId = participationId;
        this.userId = userId;
        this.userName = userName;
    }
}
