package de.iks.rataplan.dto;

import de.iks.rataplan.domain.DecisionType;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;

@Data
@EqualsAndHashCode(callSuper = true)
@NoArgsConstructor
public class CreatorVoteDTO extends VoteDTO implements Serializable {
    
    private static final long serialVersionUID = 8169186536220940206L;
    
    private VoteNotificationSettingsDTO notificationSettings;
    
    private String editToken;
    
    private List<String> consigneeList;
    
    private List<Integer> userConsignees;
    
    public CreatorVoteDTO(
        Integer id,
        String title,
        String description,
        Instant deadline,
        String organizerName,
        VoteNotificationSettingsDTO notificationSettings,
        String personalisedInvitation
    )
    {
        super(id, title, description, deadline, organizerName, personalisedInvitation);
        this.notificationSettings = notificationSettings;
    }
    
    public CreatorVoteDTO(
        String title,
        String description,
        Instant deadline,
        String organizerName,
        VoteNotificationSettingsDTO notificationSettings,
        List<String> consigneeList,
        List<Integer> userConsignees,
        String personalisedInvitation
    )
    {
        this(title, description, deadline, organizerName, notificationSettings, personalisedInvitation);
        this.consigneeList = consigneeList;
        this.userConsignees = userConsignees;
    }
    
    public CreatorVoteDTO(
        String title,
        String description,
        Instant deadline,
        String organizerName,
        VoteNotificationSettingsDTO notificationSettings,
        String personalisedInvitation
    )
    {
        super(title, description, deadline, organizerName, personalisedInvitation);
        this.notificationSettings = notificationSettings;
    }
    public CreatorVoteDTO(
        String title,
        String description,
        Instant instant,
        String iksName,
        VoteNotificationSettingsDTO voteNotificationSettingsDTO,
        DecisionType decisionType,
        String message
    )
    {
        
        this(title, description, instant, iksName, voteNotificationSettingsDTO, message);
        this.setDecisionType(decisionType);
    }
    
    @Override
    public void defaultNullValues() {
        super.defaultNullValues();
        if(this.consigneeList == null) this.consigneeList = new ArrayList<>();
        if(this.userConsignees == null) this.userConsignees = new ArrayList<>();
    }
}
