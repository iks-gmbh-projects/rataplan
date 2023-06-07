package de.iks.rataplan.domain;

import java.io.Serializable;
import java.sql.Date;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import javax.persistence.*;

import de.iks.rataplan.exceptions.MalformedException;
import de.iks.rataplan.mapping.crypto.DBEncryptedStringConverter;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

@Entity
@Table(name = "vote")
public class Vote implements Serializable {

	private static final long serialVersionUID = 6229127764261785894L;

	@CreationTimestamp
	@Column(updatable = false)
	private Instant creationTime;
	@UpdateTimestamp
	private Instant lastUpdated;
	@Version
	private Integer version;

	private Integer id;
	private EncryptedString title;
	private EncryptedString description;
	private Date deadline;
	private EncryptedString organizerName;
	private EncryptedString organizerMail;
	private Integer userId;
	private boolean isNotified = false;
	private String participationToken;
	private String editToken;

	private VoteConfig voteConfig = new VoteConfig();

	private List<String> consigneeList = new ArrayList<>();
	private List<VoteOption> options = new ArrayList<>();
	private List<VoteParticipant> participants = new ArrayList<>();
	private List<BackendUserAccess> accessList = new ArrayList<>();

	public Vote(EncryptedString title, EncryptedString description, Date deadline, EncryptedString organizerName,
							  EncryptedString organizerMail, VoteConfig voteConfig, List<VoteOption> options,
			List<VoteParticipant> participants, boolean isNotified
	) {
		this.title = title;
		this.description = description;
		this.deadline = deadline;
		this.organizerName = organizerName;
		this.organizerMail = organizerMail;
		this.options = options;
		this.participants = participants;
		this.voteConfig = voteConfig;
		this.isNotified = isNotified;
	}

	public Vote(EncryptedString title, EncryptedString description, Date deadline, EncryptedString organizerName,
							  EncryptedString organizerMail, VoteConfig voteConfig
	) {
		this.title = title;
		this.description = description;
		this.deadline = deadline;
		this.organizerName = organizerName;
		this.organizerMail = organizerMail;
		this.voteConfig = voteConfig;
	}

	public Vote() {
		// required for Hibernate
	}
	
	public Instant getCreationTime() {
		return creationTime;
	}
	
	public void setCreationTime(Instant creationTime) {
		this.creationTime = creationTime;
	}
	
	public Instant getLastUpdated() {
		return lastUpdated;
	}
	
	public void setLastUpdated(Instant lastUpdated) {
		this.lastUpdated = lastUpdated;
	}

	public Integer getVersion() {
		return version;
	}

	public void setVersion(Integer version) {
		this.version = version;
	}

	@Id
	@Column(name = "id")
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	public Integer getId() {
		return id;
	}

	public void setId(Integer id) {
		this.id = id;
	}

	@Column(name = "title")
	@Convert(converter = DBEncryptedStringConverter.class)
	public EncryptedString getTitle() {
		return title;
	}

	public void setTitle(EncryptedString title) {
		this.title = title;
	}

	@Column(name = "description")
	@Convert(converter = DBEncryptedStringConverter.class)
	public EncryptedString getDescription() {
		return description;
	}

	public void setDescription(EncryptedString description) {
		this.description = description;
	}

	@Column(name = "deadline")
	public Date getDeadline() {
		return deadline;
	}

	public void setDeadline(Date deadline) {
		this.deadline = deadline;
	}

	@Column(name = "organizerName")
	@Convert(converter = DBEncryptedStringConverter.class)
	public EncryptedString getOrganizerName() {
		return organizerName;
	}

	public void setOrganizerName(EncryptedString organizerName) {
		this.organizerName = organizerName;
	}

	@Column(name = "organizerMail")
	@Convert(converter = DBEncryptedStringConverter.class)
	public EncryptedString getOrganizerMail() {
		return organizerMail;
	}

	public void setOrganizerMail(EncryptedString organizerMail) {
		this.organizerMail = organizerMail;
	}
	
	@Column(name = "userId")
	public Integer getUserId() {
		return userId;
	}

	public void setUserId(Integer userId) {
		this.userId = userId;
	}

	@OneToMany(fetch = FetchType.LAZY, mappedBy = "vote", cascade = CascadeType.ALL)
	public List<VoteOption> getOptions() {
		return options;
	}

	public void setOptions(List<VoteOption> voteOptions) {
		this.options = voteOptions;
	}

	@OneToMany(fetch = FetchType.LAZY, mappedBy = "vote", orphanRemoval = true, cascade = CascadeType.ALL)
	public List<VoteParticipant> getParticipants() {
		return participants;
	}

	public void setParticipants(List<VoteParticipant> voteParticipants) {
		this.participants = voteParticipants;
	}
	
	@OneToOne(cascade = CascadeType.ALL)
	@JoinColumn(name = "voteConfigId")
	public VoteConfig getVoteConfig() {
		return voteConfig;
	}
	
	public void setVoteConfig(VoteConfig voteConfig) {
		this.voteConfig = voteConfig;
	}

	@Column(name = "isNotified")
	public boolean isNotified() {
		return isNotified;
	}

	public void setNotified(boolean isNotified) {
		this.isNotified = isNotified;
	}

	@Column(name = "participationToken")
	public String getParticipationToken() {
		return participationToken;
	}

	public void setParticipationToken(String participationToken) {
		this.participationToken = participationToken;
	}

	@Column(name = "editToken")
	public String getEditToken() {
		return editToken;
	}

	public void setEditToken(String editToken) {
		this.editToken = editToken;
	}

	public VoteParticipant getParticipantById(long id) {
		for (VoteParticipant member : this.getParticipants()) {
			if (id == member.getId()) {
				return member;
			}
		}
		return null;
	}
	
	public VoteOption getOptionById(long id) {
		for (VoteOption voteOption : this.getOptions()) {
			if (voteOption.getId() != null && id == voteOption.getId()) {
				return voteOption;
			}
		}
		return null;
	}
	
	@Transient
	public List<String> getConsigneeList() {
		return consigneeList;
	}

	public void setConsigneeList(List<String> consigneeList) {
		this.consigneeList = consigneeList;
	}

	@OneToMany(fetch = FetchType.LAZY, mappedBy = "voteId", cascade = CascadeType.ALL)
	public List<BackendUserAccess> getAccessList() {
		return accessList;
	}

	public void setAccessList(List<BackendUserAccess> accessList) {
		this.accessList = accessList;
	}

	/**
     * checks if the VoteDecisions have the same size and voteOptionId's
     * than the corresponding VoteOptions in this Vote
     *
     * @param voteParticipant
     * @return
     */
	public boolean validateDecisionsForParticipant(VoteParticipant voteParticipant) {
		List<Integer> optionIdList = new ArrayList<>();
		if (this.options.size() != voteParticipant.getVoteDecisions().size()) {
			return false;
		}
		
		for (VoteOption voteOption : this.getOptions()) {
			for (VoteDecision decision : voteParticipant.getVoteDecisions()) {
				this.decisionTypeVerification(decision);
				
				if (decision.getVoteOption() == null) {
					return false;
				} else if (Objects.equals(voteOption.getId(), decision.getVoteOption().getId())
						&& !optionIdList.contains(voteOption.getId())) {
					optionIdList.add(voteOption.getId());
				}
			}
		}
		return optionIdList.size() == this.getOptions().size();
	}
	
	/**
	 * checks if the given VoteDecision fits the DecisionType in this Vote
	 * @param decision
	 */
    private void decisionTypeVerification(VoteDecision decision) {
    	switch (this.voteConfig.getDecisionType()) {
    	case EXTENDED:
    		if (decision.getParticipants() != null) {
    			throw new MalformedException("Decision does not fit to DecisionType");
    		}
    		return;
    	case DEFAULT:
    		if (decision.getDecision() == Decision.ACCEPT_IF_NECESSARY || decision.getParticipants() != null) {
    			throw new MalformedException("Decision does not fit to DecisionType");
    		}
    		return;
    	case NUMBER:
    		if (decision.getDecision() != null || decision.getParticipants() < 0 || decision.getParticipants() > 255) {
    			throw new MalformedException("Decision does not fit to DecisionType");
    		}
    		return;
    	}
    }

	@Override
	public String toString() {
		StringBuilder builder = new StringBuilder();
		builder.append("Vote [\nid=");
		builder.append(id);
		builder.append(",\ntitle=");
		builder.append(title);
		builder.append(",\ndescription=");
		builder.append(description);
		builder.append(",\ndeadline=");
		builder.append(deadline);
		builder.append(",\norganizerName=");
		builder.append(organizerName);
		builder.append(",\norganizerMail=");
		builder.append(organizerMail);
		builder.append(",\nvoteConfig=\n");
		builder.append(voteConfig);
		builder.append(",\noptions=\n");
		builder.append(options);
		builder.append(",\nparticipants=\n");
		builder.append(participants);
		builder.append("\n]");
		return builder.toString();
	}
	
	// Because hibernate is ignoring the Annotations on creationTime, lastUpdated and version for some reason.
	@PrePersist
	@PreUpdate
	public void hibernateStupidity() {
		final Instant now = Instant.now();
		if(this.creationTime == null) this.creationTime = now;
		this.lastUpdated = now;
		if(this.version == null) this.version = 1;
		else this.version++;
	}
}
