package iks.surveytool.dtos;

import lombok.*;

import org.springframework.format.annotation.DateTimeFormat;

import java.time.Instant;

@Data
@EqualsAndHashCode(callSuper = true)
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
    public void trimAndNull() {
        name = trimAndNull(name);
        description = trimAndNull(description);
    }
    
    @Override
    public void valid() throws DTOValidationException {
        if(this.name != null && this.name.isBlank()) throw new DTOValidationException("SurveyOverviewDTO.name", "blank non-null");
        if(this.description != null && this.description.isBlank()) throw new DTOValidationException("SurveyOverviewDTO.description", "blank non-null");
        if(this.startDate == null) throw new DTOValidationException("SurveyOverviewDTO.startDate", "missing");
        if(this.endDate == null) throw new DTOValidationException("SurveyOverviewDTO.endDate", "missing");
        if(!startDate.isBefore(endDate)) throw new DTOValidationException("SurveyOverviewDTO.endDate", "before startDate");
    }
}