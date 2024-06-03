package de.iks.rataplan.dto;

import de.iks.rataplan.domain.VoteConfig;
import lombok.*;

import java.io.Serializable;
import java.sql.Date;
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
        VoteConfig voteConfig,
        String personalisedInvitation
    ) {
        super(id, title, description, deadline, organizerName, voteConfig,  personalisedInvitation);
        this.notificationSettings = notificationSettings;
    }
    
    public CreatorVoteDTO(
        String title,
        String description,
        Instant deadline,
        String organizerName,
        VoteNotificationSettingsDTO notificationSettings,
        VoteConfig voteConfig,
        List<String> consigneeList,
        List<Integer> userConsignees,
        String personalisedInvitation
    ) {
        this(title, description, deadline, organizerName, notificationSettings, voteConfig, personalisedInvitation);
        this.consigneeList = consigneeList;
        this.userConsignees = userConsignees;
    }
    
    public CreatorVoteDTO(
        String title,
        String description,
        Instant deadline,
        String organizerName,
        VoteNotificationSettingsDTO notificationSettings,
        VoteConfig voteConfig,
        String personalisedInvitation
    ) {
        super(title, description, deadline, organizerName, voteConfig, personalisedInvitation);
        this.notificationSettings = notificationSettings;
    }
    
    @Override
    public void defaultNullValues() {
        super.defaultNullValues();
        if(this.consigneeList == null) this.consigneeList = new ArrayList<>();
        if(this.userConsignees == null) this.userConsignees = new ArrayList<>();
    }
}
