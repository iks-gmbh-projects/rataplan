package de.iks.rataplan.domain;

import de.iks.rataplan.exceptions.MalformedException;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.Column;
import javax.persistence.Embeddable;

@Embeddable
@EqualsAndHashCode
@Getter
@Setter
@NoArgsConstructor
public class VoteOptionConfig {
	private boolean startDate;
	private boolean startTime;
	private boolean endDate;
	private boolean endTime;
	private boolean url;
	private boolean description;
	
	public VoteOptionConfig(boolean description, boolean url, boolean startDate, boolean startTime, boolean endDate, boolean endTime) {
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
	
	@Column(name = "isEndDate")
	public boolean isEndDate() {
		return endDate;
	}
	
	@Column(name = "isStartTime")
	public boolean isStartTime() {
		return startTime;
	}
	
	@Column(name = "isEndTime")
	public boolean isEndTime() {
		return endTime;
	}
	
	@Column(name = "isUrl")
	public boolean isUrl() {
		return url;
	}
	
	@Column(name = "isDescription")
	public boolean isDescription() {
		return description;
	}
	
	public void assertValid() {
		if(!startDate && (startTime || endDate)) throw new MalformedException("Invalid appointment config");
		if(!startTime && endTime) throw new MalformedException("Invalid appointment config");
		if(startDate || description || url) return;
		throw new MalformedException("Invalid appointment config");
	}
}
