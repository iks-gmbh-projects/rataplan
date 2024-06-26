package de.iks.rataplan.domain;

import de.iks.rataplan.exceptions.MalformedException;
import de.iks.rataplan.mapping.crypto.DBEncryptedStringConverter;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import javax.persistence.*;
import java.io.Serializable;
import java.sql.Date;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

@Entity
@Table(name = "vote")
@NoArgsConstructor
@Getter
@Setter
@ToString
public class Vote implements Serializable {
    
    private static final long serialVersionUID = 6229127764261785894L;
    
    private Timestamp creationTime;
    private Timestamp lastUpdated;
    private Integer version;
    
    private Integer id;
    private EncryptedString title;
    private EncryptedString description;
    private Date deadline;
    private EncryptedString organizerName;
    private VoteNotificationSettings notificationSettings;
    private Integer userId;
    private boolean isNotified = false;
    private String participationToken;
    private String editToken;
    
    private VoteConfig voteConfig = new VoteConfig();
    
    private List<String> consigneeList = new ArrayList<>();
    private List<Integer> userConsignees = new ArrayList<>();
    private List<VoteOption> options = new ArrayList<>();
    private List<VoteParticipant> participants = new ArrayList<>();
    private List<BackendUserAccess> accessList = new ArrayList<>();
    private String personalisedInvitation;
    
    public Vote(
        EncryptedString title,
        EncryptedString description,
        Date deadline,
        EncryptedString organizerName,
        VoteNotificationSettings notificationSettings,
        VoteConfig voteConfig,
        List<VoteOption> options,
        List<VoteParticipant> participants,
        boolean isNotified
    )
    {
        this.title = title;
        this.description = description;
        this.deadline = deadline;
        this.organizerName = organizerName;
        this.notificationSettings = notificationSettings;
        this.options = options;
        this.participants = participants;
        this.voteConfig = voteConfig;
        this.isNotified = isNotified;
    }
    
    public Vote(
        EncryptedString title,
        EncryptedString description,
        Date deadline,
        EncryptedString organizerName,
        VoteNotificationSettings notificationSettings,
        VoteConfig voteConfig
    )
    {
        this.title = title;
        this.description = description;
        this.deadline = deadline;
        this.organizerName = organizerName;
        this.notificationSettings = notificationSettings;
        this.voteConfig = voteConfig;
    }
    
    @CreationTimestamp
    @Column(updatable = false)
    public Timestamp getCreationTime() {
        return creationTime;
    }
    
    @UpdateTimestamp
    public Timestamp getLastUpdated() {
        return lastUpdated;
    }
    
    @Version
    public Integer getVersion() {
        return version;
    }
    
    @Id
    @Column(name = "id")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    public Integer getId() {
        return id;
    }
    
    @Column(name = "title")
    @Convert(converter = DBEncryptedStringConverter.class)
    public EncryptedString getTitle() {
        return title;
    }
    
    @Column(name = "description")
    @Convert(converter = DBEncryptedStringConverter.class)
    public EncryptedString getDescription() {
        return description;
    }
    
    @Column(name = "deadline")
    public Date getDeadline() {
        return deadline;
    }
    
    @Column(name = "organizerName")
    @Convert(converter = DBEncryptedStringConverter.class)
    public EncryptedString getOrganizerName() {
        return organizerName;
    }
    
    @Embedded
    public VoteNotificationSettings getNotificationSettings() {
        return notificationSettings;
    }
    
    @Column(name = "userId")
    public Integer getUserId() {
        return userId;
    }
    
    @OneToMany(fetch = FetchType.LAZY, mappedBy = "vote", orphanRemoval = true, cascade = CascadeType.ALL)
    public List<VoteOption> getOptions() {
        return options;
    }
    
    @OneToMany(fetch = FetchType.LAZY, mappedBy = "vote", orphanRemoval = true, cascade = CascadeType.ALL)
    public List<VoteParticipant> getParticipants() {
        return participants;
    }
    
    @Embedded
    public VoteConfig getVoteConfig() {
        return voteConfig;
    }
    
    @Column(name = "isNotified")
    public boolean isNotified() {
        return isNotified;
    }
    
    @Column(name = "participationToken")
    public String getParticipationToken() {
        return participationToken;
    }
    
    @Column(name = "editToken")
    public String getEditToken() {
        return editToken;
    }
    
    public VoteParticipant getParticipantById(long id) {
        for(VoteParticipant member : this.getParticipants()) {
            if(id == member.getId()) {
                return member;
            }
        }
        return null;
    }
    
    public VoteOption getOptionById(long id) {
        for(VoteOption voteOption : this.getOptions()) {
            if(voteOption.getId() != null && id == voteOption.getId()) {
                return voteOption;
            }
        }
        return null;
    }
    
    @Transient
    public List<String> getConsigneeList() {
        return consigneeList;
    }
    @Transient
    public List<Integer> getUserConsignees() {
        return userConsignees;
    }
    @Transient
    public String getPersonalisedInvitation(){return personalisedInvitation;}
    @OneToMany(fetch = FetchType.LAZY, mappedBy = "voteId", cascade = CascadeType.ALL)
    public List<BackendUserAccess> getAccessList() {
        return accessList;
    }
    
    /**
     * checks if the VoteDecisions have the same size and voteOptionId's
     * than the corresponding VoteOptions in this Vote
     */
    public boolean validateDecisionsForParticipant(VoteParticipant voteParticipant) {
        List<Integer> optionIdList = new ArrayList<>();
        if(this.options.size() != voteParticipant.getVoteDecisions().size()) {
            return false;
        }
        
        for(VoteOption voteOption : this.getOptions()) {
            for(VoteDecision decision : voteParticipant.getVoteDecisions()) {
                this.decisionTypeVerification(decision);
                
                if(decision.getVoteOption() == null) {
                    return false;
                } else if(Objects.equals(voteOption.getId(), decision.getVoteOption().getId()) &&
                          !optionIdList.contains(voteOption.getId()))
                {
                    optionIdList.add(voteOption.getId());
                }
            }
        }
        return optionIdList.size() == this.getOptions().size();
    }
    
    /**
     * checks if the given VoteDecision fits the DecisionType in this Vote
     */
    private void decisionTypeVerification(VoteDecision decision) {
        switch(this.voteConfig.getDecisionType()) {
            case EXTENDED:
                if(decision.getParticipants() != null) {
                    throw new MalformedException("Decision does not fit to DecisionType");
                }
                return;
            case DEFAULT:
                if(decision.getDecision() == Decision.ACCEPT_IF_NECESSARY || decision.getParticipants() != null) {
                    throw new MalformedException("Decision does not fit to DecisionType");
                }
                return;
            case NUMBER:
                if(decision.getDecision() != null || decision.getParticipants() < 0 ||
                   decision.getParticipants() > 255)
                {
                    throw new MalformedException("Decision does not fit to DecisionType");
                }
                return;
        }
    }
}
