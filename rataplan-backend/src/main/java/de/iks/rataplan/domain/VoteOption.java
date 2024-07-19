package de.iks.rataplan.domain;

import de.iks.rataplan.mapping.crypto.DBEncryptedStringConverter;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;
import org.springframework.format.annotation.DateTimeFormat;

import javax.persistence.*;
import java.io.Serializable;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

@Entity
@Table(name = "voteOption")
@NoArgsConstructor
@Getter
@Setter
@ToString(exclude = "vote")
public class VoteOption implements Serializable {
    
    private static final long serialVersionUID = 1722350279433794595L;
    
    @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME)
    private Timestamp creationTime;
    @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME)
    private Timestamp lastUpdated;
    private Integer version;
    
    private Integer id;
    @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME)
    private Timestamp startDate;
    @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME)
    private Timestamp endDate;
    private EncryptedString description;
    private EncryptedString url;
    
    private Vote vote;
    private List<VoteDecision> voteDecisions = new ArrayList<>();
    
    //    private boolean participantLimitActive;
    private Integer participantLimit;
    
    public VoteOption(
        Timestamp startDate, EncryptedString description, Vote vote, Integer participantLimit
    )
    {
        this.startDate = startDate;
        this.description = description;
        this.vote = vote;
        this.participantLimit = participantLimit;
    }
    
    public VoteOption(
        EncryptedString description, Vote vote, Integer participantLimit
    )
    {
        this.description = description;
        this.vote = vote;
        this.participantLimit = participantLimit;
    }
    public VoteOption(Timestamp startDate, EncryptedString description, Vote vote) {
        this.startDate = startDate;
        this.description = description;
        this.vote = vote;
    }
    
    public VoteOption(EncryptedString description, Vote vote) {
        this.description = description;
        this.vote = vote;
    }
    
    public VoteOption(Vote vote) {
        this.vote = vote;
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
    
    @Column(name = "startDate")
    public Timestamp getStartDate() {
        return startDate;
    }
    
    @Column(name = "endDate")
    public Timestamp getEndDate() {
        return endDate;
    }
    
    @Column(name = "description")
    @Convert(converter = DBEncryptedStringConverter.class)
    public EncryptedString getDescription() {
        return description;
    }
    
    @Column(name = "url")
    @Convert(converter = DBEncryptedStringConverter.class)
    public EncryptedString getUrl() {
        return url;
    }
    
    @ManyToOne
    @JoinColumn(name = "voteId", nullable = false)
    public Vote getVote() {
        return this.vote;
    }
    
    @OneToMany(
        fetch = FetchType.LAZY, mappedBy = "voteDecisionId.voteOption", orphanRemoval = true, cascade = CascadeType.ALL
    )
    public List<VoteDecision> getVoteDecisions() {
        return voteDecisions;
    }
    
    public boolean assertInvalid() {
        if(endDate != null && startDate == null) return true;
        if(this.participantLimit != null)
            if(this.participantLimit <= 0) return true;
        return (this.url == null && this.startDate == null && this.endDate == null && this.description == null) ||
                    !this.voteDecisions.isEmpty();
    }
    
    public boolean assertConfigEqual(VoteOption voteOption) {
        return Objects.equals(voteOption.getId(), this.id) &&
               this.vote.isStartTime() == voteOption.vote.isStartTime() &&
               this.vote.isEndTime() == voteOption.getVote().isEndTime() &&
               this.description == voteOption.description && this.url == voteOption.url &&
               Objects.equals(this.vote.getId(), voteOption.getId());
    }
}