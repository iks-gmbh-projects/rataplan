package de.iks.rataplan.dto;

import java.io.Serializable;
import java.sql.Date;
import java.util.ArrayList;
import java.util.List;

import de.iks.rataplan.domain.AppointmentRequestConfig;

public class AppointmentRequestDTO implements Serializable {

    private static final long serialVersionUID = 8169186536220940206L;

    private Integer id;
    private String title;
    private String description;
    private String organizerMail;
    private Date deadline;
    private Integer backendUserId;
    private boolean expired;
    
    private AppointmentRequestConfig appointmentRequestConfig = new AppointmentRequestConfig();

    private List<String> consigneeList = new ArrayList<>();
	private List<AppointmentDTO> appointments = new ArrayList<>();
    private List<AppointmentMemberDTO> appointmentMembers = new ArrayList<>();

    public AppointmentRequestDTO() {
        //Nothing to do here
    }

    public AppointmentRequestDTO(Integer id, String title, String description, Date deadline, String organizerMail, AppointmentRequestConfig appointmentRequestConfig) {
    	this(title, description, deadline, organizerMail, appointmentRequestConfig);
    	this.id = id;
    }

    public AppointmentRequestDTO(String title, String description, Date deadline, String organizerMail, AppointmentRequestConfig appointmentRequestConfig, List<String> consigneeList) {
    	this(title, description, deadline, organizerMail, appointmentRequestConfig);
    	this.consigneeList = consigneeList;
    }
    
    public AppointmentRequestDTO(String title, String description, Date deadline, String organizerMail, AppointmentRequestConfig appointmentRequestConfig) {
        this.title = title;
        this.description = description;
        this.deadline = deadline;
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

    public String getOrganizerMail() {
        return organizerMail;
    }

    public void setOrganizerMail(String organizermail) {
        this.organizerMail = organizermail;
    }

    public Date getDeadline() {
        return deadline;
    }

    public void setDeadline(Date deadline) {
        this.deadline = deadline;
    }
    
    public List<String> getConsigneeList() {
    	return this.consigneeList;
    }
    
    public void setConsigneeList(List<String> consigneeList) {
    	this.consigneeList = consigneeList;
    }

    public Integer getBackendUserId() {
		return backendUserId;
	}

	public void setBackendUserId(Integer backendUserId) {
		this.backendUserId = backendUserId;
	}

	public List<AppointmentDTO> getAppointments() {
        return appointments;
    }

    public void setAppointments(List<AppointmentDTO> appointments) {
        this.appointments = appointments;
    }

    public List<AppointmentMemberDTO> getAppointmentMembers() {
        return appointmentMembers;
    }

    public void setAppointmentMembers(List<AppointmentMemberDTO> appointmentMembers) {
        this.appointmentMembers = appointmentMembers;
    }

	public AppointmentRequestConfig getAppointmentRequestConfig() {
		return appointmentRequestConfig;
	}

	public void setAppointmentRequestConfig(AppointmentRequestConfig appointmentRequestConfig) {
		this.appointmentRequestConfig = appointmentRequestConfig;
	}
	
	public boolean isExpired() {
		return expired;
	}

	public void setExpired(boolean expired) {
		this.expired = expired;
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
		builder.append(", organizerMail=");
		builder.append(organizerMail);
		builder.append(", deadline=");
		builder.append(deadline);
		builder.append(", appointmentRequestConfig=");
		builder.append(appointmentRequestConfig);
		builder.append(", appointments=");
		builder.append(appointments);
		builder.append(", appointmentMember=");
		builder.append(appointmentMembers);
		builder.append("]");
		return builder.toString();
	}
}