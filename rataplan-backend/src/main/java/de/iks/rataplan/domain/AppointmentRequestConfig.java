package de.iks.rataplan.domain;

import javax.persistence.Column;
import javax.persistence.Embedded;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;

@Entity
@Table(name = "appointmentRequestConfig")
public class AppointmentRequestConfig {
	
	private Integer id;
	private AppointmentConfig appointmentConfig;
	private DecisionType decisionType = DecisionType.DEFAULT;
	private String password = null;
	private String adminPassword = null;

	public AppointmentRequestConfig() {
		//Nothing to do here
	}
	
	public AppointmentRequestConfig(Integer id, AppointmentConfig appointmentConfig, DecisionType decisionType,
			String password, String adminPassword) {
		this.id = id;
		this.appointmentConfig = appointmentConfig;
		this.decisionType = decisionType;
		this.password = password;
		this.adminPassword = adminPassword;
	}

	public AppointmentRequestConfig(AppointmentConfig appointmentConfig, DecisionType decisionType) {
		this.appointmentConfig = appointmentConfig;
		this.decisionType = decisionType;
	}
	
	public AppointmentRequestConfig(AppointmentConfig appointmentConfig, DecisionType decisionType, String password, String adminPassword) {
		this(appointmentConfig, decisionType);
		this.password = password;
		this.adminPassword = adminPassword;
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
	
	@Embedded
	public AppointmentConfig getAppointmentConfig() {
		return appointmentConfig;
	}

	public void setAppointmentConfig(AppointmentConfig appointmentConfig) {
		this.appointmentConfig = appointmentConfig;
	}
	
	@Column(name = "decisionType")
	public DecisionType getDecisionType() {
		return decisionType;
	}

	public void setDecisionType(DecisionType decisionType) {
		this.decisionType = decisionType;
	}
	
	@JsonIgnore
	@Column(name = "password")
	public String getPassword() {
		return this.password;
	}
	
	@JsonProperty
	public void setPassword(String password) {
		this.password = password;
	}
	
	@JsonIgnore
	@Column(name = "adminPassword")
	public String getAdminPassword() {
		return this.adminPassword;
	}
	
	@JsonProperty
	public void setAdminPassword(String adminPassword) {
		this.adminPassword = adminPassword;
	}

	@Override
	public String toString() {
		StringBuilder builder = new StringBuilder();
		builder.append("AppointmentRequestConfig [appointmentType=");
		builder.append(appointmentConfig);
		builder.append(", decisionType=");
		builder.append(decisionType);
		builder.append("]");
		return builder.toString();
	}
}
