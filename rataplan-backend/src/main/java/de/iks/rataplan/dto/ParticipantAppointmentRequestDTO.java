package de.iks.rataplan.dto;

import de.iks.rataplan.domain.AppointmentRequestConfig;
import java.io.Serializable;
import java.sql.Date;
import java.util.List;


public class ParticipantAppointmentRequestDTO implements Serializable {

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
    private AppointmentRequestConfig appointmentRequestConfig = new AppointmentRequestConfig();
    private List<String> consigneeList;
    private List<VoteOptionDTO> options;
    private List<AppointmentMemberDTO> appointmentMembers;

    public ParticipantAppointmentRequestDTO() {}
    
    public ParticipantAppointmentRequestDTO(Integer id, String title, String description, Date deadline, String organizerName,  String organizerMail, AppointmentRequestConfig appointmentRequestConfig) {
        this(title, description, deadline, organizerName, organizerMail, appointmentRequestConfig);
        this.id = id;
    }
    
    public ParticipantAppointmentRequestDTO(String title, String description, Date deadline, String organizerName, String organizerMail, AppointmentRequestConfig appointmentRequestConfig, List<String> consigneeList) {
        this(title, description, deadline, organizerName, organizerMail, appointmentRequestConfig);
        this.consigneeList = consigneeList;
    }
    
    public ParticipantAppointmentRequestDTO(String title, String description, Date deadline, String organizerName, String organizerMail, AppointmentRequestConfig appointmentRequestConfig) {
        this.title = title;
        this.description = description;
        this.deadline = deadline;
        this.organizerName = organizerName;
        this.organizerMail = organizerMail;
        this.appointmentRequestConfig = appointmentRequestConfig;
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

    public AppointmentRequestConfig getAppointmentRequestConfig() {
        return appointmentRequestConfig;
    }

    public void setAppointmentRequestConfig(AppointmentRequestConfig appointmentRequestConfig) {
        this.appointmentRequestConfig = appointmentRequestConfig;
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

    public List<AppointmentMemberDTO> getAppointmentMembers() {
        return appointmentMembers;
    }

    public void setAppointmentMembers(List<AppointmentMemberDTO> appointmentMembers) {
        this.appointmentMembers = appointmentMembers;
    }
}
