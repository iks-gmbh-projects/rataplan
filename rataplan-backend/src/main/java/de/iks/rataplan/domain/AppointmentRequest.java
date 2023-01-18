package de.iks.rataplan.domain;

import java.io.Serializable;
import java.sql.Date;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import javax.persistence.*;

import de.iks.rataplan.exceptions.MalformedException;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

@Entity
@Table(name = "appointmentRequest")
public class AppointmentRequest implements Serializable {

	private static final long serialVersionUID = 6229127764261785894L;
	
	@CreationTimestamp
	@Column(updatable = false)
	private Instant creationTime;
	@UpdateTimestamp
	private Instant lastUpdated;
	@Version
	private Integer version;
	
	private Integer id;
	private String title;
	private String description;
	private Date deadline;
	private String organizerName;
	private String organizerMail;
	private Integer backendUserId;
	private boolean isExpired = false;
	private String participationToken;
	private String editToken;

	private AppointmentRequestConfig appointmentRequestConfig = new AppointmentRequestConfig();

	private List<String> consigneeList = new ArrayList<>();
	private List<Appointment> appointments = new ArrayList<>();
	private List<AppointmentMember> appointmentMembers = new ArrayList<>();
	private List<BackendUserAccess> accessList = new ArrayList<>();

	public AppointmentRequest(String title, String description, Date deadline, String organizerName,
			String organizerMail, AppointmentRequestConfig appointmentRequestConfig, List<Appointment> appointments,
			List<AppointmentMember> appointmentMembers, boolean isExpired) {
		this.title = title;
		this.description = description;
		this.deadline = deadline;
		this.organizerName = organizerName;
		this.organizerMail = organizerMail;
		this.appointments = appointments;
		this.appointmentMembers = appointmentMembers;
		this.appointmentRequestConfig = appointmentRequestConfig;
		this.isExpired = isExpired;
	}

	public AppointmentRequest(String title, String description, Date deadline, String organizerName,
			String organizerMail, AppointmentRequestConfig appointmentRequestConfig) {
		this.title = title;
		this.description = description;
		this.deadline = deadline;
		this.organizerName = organizerName;
		this.organizerMail = organizerMail;
		this.appointmentRequestConfig = appointmentRequestConfig;
	}

	public AppointmentRequest() {
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
	public String getTitle() {
		return title;
	}

	public void setTitle(String title) {
		this.title = title;
	}

	@Column(name = "description")
	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
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
	public String getOrganizerName() {
		return organizerName;
	}

	public void setOrganizerName(String organizerName) {
		this.organizerName = organizerName;
	}

	@Column(name = "organizerMail")
	public String getOrganizerMail() {
		return organizerMail;
	}

	public void setOrganizerMail(String organizerMail) {
		this.organizerMail = organizerMail;
	}
	
	@Column(name = "backendUserId")
	public Integer getBackendUserId() {
		return backendUserId;
	}

	public void setBackendUserId(Integer backendUserId) {
		this.backendUserId = backendUserId;
	}

	@OneToMany(fetch = FetchType.LAZY, mappedBy = "appointmentRequest", cascade = CascadeType.ALL)
	public List<Appointment> getAppointments() {
		return appointments;
	}

	public void setAppointments(List<Appointment> appointments) {
		this.appointments = appointments;
	}

	@OneToMany(fetch = FetchType.LAZY, mappedBy = "appointmentRequest", orphanRemoval = true, cascade = CascadeType.ALL)
	public List<AppointmentMember> getAppointmentMembers() {
		return appointmentMembers;
	}

	public void setAppointmentMembers(List<AppointmentMember> appointmentMembers) {
		this.appointmentMembers = appointmentMembers;
	}
	
	@OneToOne(cascade = CascadeType.ALL)
	@JoinColumn(name = "appointmentRequestConfigId")
	public AppointmentRequestConfig getAppointmentRequestConfig() {
		return appointmentRequestConfig;
	}
	
	public void setAppointmentRequestConfig(AppointmentRequestConfig appointmentRequestConfig) {
		this.appointmentRequestConfig = appointmentRequestConfig;
	}

	@Column(name = "isExpired")
	public boolean isExpired() {
		return isExpired;
	}

	public void setExpired(boolean isExpired) {
		this.isExpired = isExpired;
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

	public AppointmentMember getAppointmentMemberById(long id) {
		for (AppointmentMember member : this.getAppointmentMembers()) {
			if (id == member.getId()) {
				return member;
			}
		}
		return null;
	}
	
	public Appointment getAppointmentById(long id) {
		for (Appointment appointment : this.getAppointments()) {
			if (appointment.getId() != null && id == appointment.getId()) {
				return appointment;
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
	
	@OneToMany(fetch = FetchType.LAZY, mappedBy = "appointmentRequestId", cascade = CascadeType.ALL)
	public List<BackendUserAccess> getAccessList() {
		return accessList;
	}
	
	public void setAccessList(List<BackendUserAccess> accessList) {
		this.accessList = accessList;
	}
	
	/**
     * checks if the AppointmentDecisions have the same size and appointmentId's
     * than the corresponding Appointments in this AppointmentRequest
     *
     * @param appointmentMember
     * @return
     */
	public boolean validateDecisionsForAppointmentMember(AppointmentMember appointmentMember) {
		List<Integer> appointmentIdList = new ArrayList<>(); 
		if (this.appointments.size() != appointmentMember.getAppointmentDecisions().size()) {
			return false;
		}
		
		for (Appointment appointment : this.getAppointments()) {
			for (AppointmentDecision decision : appointmentMember.getAppointmentDecisions()) {
				this.decisionTypeVerification(decision);
				
				if (decision.getAppointment() == null) {
					return false;
				} else if (Objects.equals(appointment.getId(), decision.getAppointment().getId())
						&& !appointmentIdList.contains(appointment.getId())) {
					appointmentIdList.add(appointment.getId());
				}
			}
		}
		return appointmentIdList.size() == this.getAppointments().size();
	}
	
	/**
	 * checks if the given AppointmentDecision fits the DecisionType in this AppointmentRequest
	 * @param decision
	 */
    private void decisionTypeVerification(AppointmentDecision decision) {
    	switch (this.appointmentRequestConfig.getDecisionType()) {
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
		builder.append("AppointmentRequest [\nid=");
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
		builder.append(",\nappointmentRequestConfig=\n");
		builder.append(appointmentRequestConfig);
		builder.append(",\nappointments=\n");
		builder.append(appointments);
		builder.append(",\nappointmentMembers=\n");
		builder.append(appointmentMembers);
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
