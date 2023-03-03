package de.iks.rataplan.domain;

import de.iks.rataplan.exceptions.MalformedException;

import javax.persistence.Column;
import javax.persistence.Embeddable;

@Embeddable
public class AppointmentConfig {
	private boolean startDate;
	private boolean startTime;
	private boolean endDate;
	private boolean endTime;
	private boolean url;
	private boolean description;
	
	public AppointmentConfig() {
		//Nothing to do here
	}
	
	public AppointmentConfig(boolean description, boolean url, boolean startDate, boolean startTime, boolean endDate, boolean endTime) {
		this.description = description;
		this.url = url;
		this.startDate = startDate;
		this.startTime = startTime;
		this.endDate = endDate;
		this.endTime = endTime;
	}
	
	@Column(name = "isStartDate")
	public boolean isStartDate() {
		return startDate;
	}
	
	public void setStartDate(boolean startDate) {
		this.startDate = startDate;
	}
	
	@Column(name = "isEndDate")
	public boolean isEndDate() {
		return endDate;
	}
	
	public void setEndDate(boolean endDate) {
		this.endDate = endDate;
	}
	
	@Column(name = "isStartTime")
	public boolean isStartTime() {
		return startTime;
	}
	
	public void setStartTime(boolean startTime) {
		this.startTime = startTime;
	}
	
	@Column(name = "isEndTime")
	public boolean isEndTime() {
		return endTime;
	}
	
	public void setEndTime(boolean endTime) {
		this.endTime = endTime;
	}
	
	@Column(name = "isUrl")
	public boolean isUrl() {
		return url;
	}
	
	public void setUrl(boolean url) {
		this.url = url;
	}
	
	@Column(name = "isDescription")
	public boolean isDescription() {
		return description;
	}
	
	public void setDescription(boolean description) {
		this.description = description;
	}

	public void assertValid() {
		if(startDate || startTime || endDate || endTime || description || url) return;
		throw new MalformedException("Invalid input fields");
	}
}
