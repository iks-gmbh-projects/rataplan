package de.iks.rataplan.dto;

import de.iks.rataplan.domain.VoteConfig;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.sql.Date;
import java.util.ArrayList;
import java.util.List;

@Data
@EqualsAndHashCode(callSuper = true)
@NoArgsConstructor
public class CreatorVoteDTO extends VoteDTO implements Serializable {

    private static final long serialVersionUID = 8169186536220940206L;

    private String editToken;
    
    private List<String> consigneeList;
    
    public CreatorVoteDTO(
        Integer id,
        String title,
        String description,
        Date deadline,
        String organizerName,
        String organizerMail,
        VoteConfig voteConfig,
        String personalisedInvitation
    ) {
        super(id, title, description, deadline, organizerName, organizerMail, voteConfig,  personalisedInvitation);
    }
    
    public CreatorVoteDTO(
        String title,
        String description,
        Date deadline,
        String organizerName,
        String organizerMail,
        VoteConfig voteConfig,
        List<String> consigneeList,
        String personalisedInvitation
    ) {
        super(title, description, deadline, organizerName, organizerMail, voteConfig, personalisedInvitation);
        this.consigneeList = consigneeList;
    }
    
    public CreatorVoteDTO(
        String title,
        String description,
        Date deadline,
        String organizerName,
        String organizerMail,
        VoteConfig voteConfig,
        String personalisedInvitation
    ) {
        super(title, description, deadline, organizerName, organizerMail, voteConfig, personalisedInvitation);
    }
    
    @Override
    public void defaultNullValues() {
        super.defaultNullValues();
        if(this.consigneeList == null) this.consigneeList = new ArrayList<>();
    }
}
