package iks.surveytool.dtos;

import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.UUID;

@Getter
@Setter
public class SurveyOverviewDTO {

    private Long id;
    private String name;
    private String description;

    private LocalDateTime startDate;
    private LocalDateTime endDate;

    private boolean open;
    private String accessID;
    private UUID uuid;

    private Long userID;
    private String userName;

    public SurveyOverviewDTO(Long id,
                             String name,
                             String description,
                             LocalDateTime startDate,
                             LocalDateTime endDate,
                             boolean open,
                             String accessID,
                             UUID uuid,
                             Long userID,
                             String userName) {
        this.id = id;
        this.name = name;
        this.description = description;
        this.startDate = startDate;
        this.endDate = endDate;
        this.open = open;
        this.accessID = accessID;
        this.uuid = uuid;
        this.userID = userID;
        this.userName = userName;
    }
}
