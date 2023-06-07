package de.iks.rataplan.dto;

import de.iks.rataplan.domain.VoteConfig;
import de.iks.rataplan.exceptions.MalformedException;

import java.io.Serializable;
import java.sql.Date;
import java.util.ArrayList;
import java.util.List;


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
    private List<String> consigneeList;
    private List<VoteOptionDTO> options;
    private List<VoteParticipantDTO> participants;

    public VoteDTO() {}
    
    public VoteDTO(Integer id, String title, String description, Date deadline, String organizerName,  String organizerMail, VoteConfig voteConfig) {
        this(title, description, deadline, organizerName, organizerMail, voteConfig);
        this.id = id;
    }
    
    public VoteDTO(String title, String description, Date deadline, String organizerName, String organizerMail, VoteConfig voteConfig, List<String> consigneeList) {
        this(title, description, deadline, organizerName, organizerMail, voteConfig);
        this.consigneeList = consigneeList;
    }
    
    public VoteDTO(String title, String description, Date deadline, String organizerName, String organizerMail, VoteConfig voteConfig) {
        this.title = title;
        this.description = description;
        this.deadline = deadline;
        this.organizerName = organizerName;
        this.organizerMail = organizerMail;
        this.voteConfig = voteConfig;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getOrganizerName() {
        return organizerName;
    }

    public void setOrganizerName(String organizerName) {
        this.organizerName = organizerName;
    }

    public String getOrganizerMail() {
        return organizerMail;
    }

    public void setOrganizerMail(String organizerMail) {
        this.organizerMail = organizerMail;
    }

    public Date getDeadline() {
        return deadline;
    }

    public void setDeadline(Date deadline) {
        this.deadline = deadline;
    }

    public Integer getUserId() {
        return userId;
    }

    public void setUserId(Integer userId) {
        this.userId = userId;
    }

    public boolean isNotified() {
        return notified;
    }

    public void setNotified(boolean notified) {
        this.notified = notified;
    }

    public String getParticipationToken() {
        return participationToken;
    }

    public void setParticipationToken(String participationToken) {
        this.participationToken = participationToken;
    }

    public VoteConfig getAppointmentRequestConfig() {
        return voteConfig;
    }

    public void setAppointmentRequestConfig(VoteConfig voteConfig) {
        this.voteConfig = voteConfig;
    }

    public List<String> getConsigneeList() {
        return consigneeList;
    }

    public void setConsigneeList(List<String> consigneeList) {
        this.consigneeList = consigneeList;
    }

    public List<VoteOptionDTO> getOptions() {
        return options;
    }

    public void setOptions(List<VoteOptionDTO> options) {
        this.options = options;
    }

    public List<VoteParticipantDTO> getParticipants() {
        return participants;
    }

    public void setParticipants(List<VoteParticipantDTO> participants) {
        this.participants = participants;
    }
    
    @Override
    public String toString() {
        StringBuilder builder = new StringBuilder();
        builder.append("AppointmentRequestDTO [id=");
        builder.append(id);
        builder.append(", title=");
        builder.append(title);
        builder.append(", description=");
        builder.append(description);
        builder.append(", organizerName=");
        builder.append(organizerName);
        builder.append(", organizerMail=");
        builder.append(organizerMail);
        builder.append(", deadline=");
        builder.append(deadline);
        builder.append(", appointmentRequestConfig=");
        builder.append(voteConfig);
        builder.append(", options=");
        builder.append(options);
        builder.append(", participants=");
        builder.append(participants);
        builder.append("]");
        return builder.toString();
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
        if(this.consigneeList == null) this.consigneeList = new ArrayList<>();
        if(this.options == null) this.options = new ArrayList<>();
        if(this.participants == null) this.participants = new ArrayList<>();
    }
}
