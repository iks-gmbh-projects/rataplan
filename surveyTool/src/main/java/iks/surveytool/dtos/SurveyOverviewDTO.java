package iks.surveytool.dtos;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.format.annotation.DateTimeFormat;

import java.time.Instant;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class SurveyOverviewDTO extends AbstractDTO {

    private String name;
    private String description;

    @DateTimeFormat(iso= DateTimeFormat.ISO.DATE_TIME)
    private Instant startDate;
    @DateTimeFormat(iso= DateTimeFormat.ISO.DATE_TIME)
    private Instant endDate;

    private boolean openAccess;
    private boolean anonymousParticipation;
    private String accessId;
    private String participationId;

    private Long userId;
    
    @Override
    public boolean valid() {
        return name != null && !name.isBlank() &&
            description != null && !description.isBlank() &&
            startDate != null && endDate != null && startDate.isBefore(endDate);
    }
}
