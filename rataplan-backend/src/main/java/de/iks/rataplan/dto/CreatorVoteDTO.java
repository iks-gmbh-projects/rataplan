package de.iks.rataplan.dto;

import de.iks.rataplan.domain.VoteConfig;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.sql.Date;
import java.util.List;

@Data
@EqualsAndHashCode(callSuper = true)
@NoArgsConstructor
public class CreatorVoteDTO extends VoteDTO implements Serializable {

    private static final long serialVersionUID = 8169186536220940206L;

    private String editToken;
    
    public CreatorVoteDTO(
        Integer id,
        String title,
        String description,
        Date deadline,
        String organizerName,
        String organizerMail,
        VoteConfig voteConfig
    ) {
        super(id, title, description, deadline, organizerName, organizerMail, voteConfig);
    }
    
    public CreatorVoteDTO(
        String title,
        String description,
        Date deadline,
        String organizerName,
        String organizerMail,
        VoteConfig voteConfig,
        List<String> consigneeList
    ) {
        super(title, description, deadline, organizerName, organizerMail, voteConfig, consigneeList);
    }
    
    public CreatorVoteDTO(
        String title,
        String description,
        Date deadline,
        String organizerName,
        String organizerMail,
        VoteConfig voteConfig
    ) {
        super(title, description, deadline, organizerName, organizerMail, voteConfig);
    }
}
