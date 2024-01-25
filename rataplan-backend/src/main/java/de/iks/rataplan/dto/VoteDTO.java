package de.iks.rataplan.dto;

import de.iks.rataplan.domain.VoteConfig;
import de.iks.rataplan.exceptions.MalformedException;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.sql.Date;
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
    private String organizerMail;
    private Date deadline;
    private Integer userId;
    private boolean notified;
    private String participationToken;
    private VoteConfig voteConfig = new VoteConfig();
    private List<VoteOptionDTO> options;
    private List<VoteParticipantDTO> participants;
    private String personalisedInvitation;
    

    public VoteDTO(Integer id, String title, String description, Date deadline, String organizerName,  String organizerMail, VoteConfig voteConfig,  String personalisedInvitation) {
        this(title, description, deadline, organizerName, organizerMail, voteConfig,personalisedInvitation);
        this.id = id;
    }
    
    public VoteDTO(String title, String description, Date deadline, String organizerName, String organizerMail, VoteConfig voteConfig, String personalisedInvitation) {
        this.title = title;
        this.description = description;
        this.deadline = deadline;
        this.organizerName = organizerName;
        this.organizerMail = organizerMail;
        this.voteConfig = voteConfig;
        this.personalisedInvitation = personalisedInvitation;
    }
    
    private static boolean nonNullAndBlank(String s) {
        return s != null && s.trim().isEmpty();
    }
    
    private static boolean nullOrBlank(String s) {
        return s == null || s.trim().isEmpty();
    }
    
    public void assertCreationValid() {
        if(id != null ||
            nullOrBlank(title) ||
            nonNullAndBlank(description) ||
            nonNullAndBlank(organizerName) ||
            nonNullAndBlank(organizerMail) ||
            deadline == null ||
            voteConfig == null ||
            options == null ||
            options.isEmpty() ||
            (participants != null && !participants.isEmpty())
        ) throw new MalformedException("Missing or invalid input fields");
        voteConfig.assertCreationValid();
        options.forEach(a -> a.assertValid(voteConfig.getVoteOptionConfig()));
    }
    
    public void defaultNullValues() {
        if(this.options == null) this.options = new ArrayList<>();
        if(this.participants == null) this.participants = new ArrayList<>();
    }
}
