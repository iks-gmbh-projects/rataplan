package de.iks.rataplan.dto;

import de.iks.rataplan.domain.DecisionType;
import de.iks.rataplan.exceptions.MalformedException;
import lombok.Data;
import lombok.NoArgsConstructor;

import org.springframework.format.annotation.DateTimeFormat;

import java.io.Serializable;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;

@Data
@NoArgsConstructor
public class VoteDTO implements Serializable {
    
    private static final long serialVersionUID = 8169186536220940206L;
    private Integer id;
    private String title;
    private String description;
    private String organizerName;
    @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME)
    private Instant deadline;
    private Integer userId;
    private boolean notified;
    private String participationToken;
    private List<VoteOptionDTO> options;
    private List<VoteParticipantDTO> participants;
    private String personalisedInvitation;
    private DecisionType decisionType = DecisionType.DEFAULT;
    private Integer yesAnswerLimit;
    private boolean endTime;
    private boolean startTime;
    
    public VoteDTO(
        Integer id,
        String title,
        String description,
        Instant deadline,
        String organizerName,
        String personalisedInvitation
    )
    {
        this(title, description, deadline, organizerName,  personalisedInvitation);
        this.id = id;
    }
    
    public VoteDTO(
        String title,
        String description,
        Instant deadline,
        String organizerName,
        String personalisedInvitation
    )
    {
        this.title = title;
        this.description = description;
        this.deadline = deadline;
        this.organizerName = organizerName;
        this.personalisedInvitation = personalisedInvitation;
    }
    
    private static boolean nonNullAndBlank(String s) {
        return s != null && s.trim().isEmpty();
    }
    
    private static boolean nullOrBlank(String s) {
        return s == null || s.trim().isEmpty();
    }
    
    public void assertCreationValid() {
        if(id != null || nullOrBlank(title) || nonNullAndBlank(description) || nonNullAndBlank(organizerName) ||
           deadline == null || options == null || options.isEmpty() ||
           (participants != null && !participants.isEmpty()))
            throw new MalformedException("Missing or invalid input fields");
    }
    
    public void defaultNullValues() {
        if(this.options == null) this.options = new ArrayList<>();
        if(this.participants == null) this.participants = new ArrayList<>();
    }
}
