package de.iks.rataplan.dto;

import de.iks.rataplan.domain.AppointmentConfig;
import de.iks.rataplan.exceptions.MalformedException;

import java.io.Serializable;
import java.sql.Timestamp;

public class AppointmentDTO implements Serializable {

    private static final long serialVersionUID = 1461651856713616814L;

    private Integer id;
    private Integer requestId;
    private Timestamp startDate;
    private Timestamp endDate;
    private String description;
	private String url;

    public AppointmentDTO(Integer id, Integer requestId, Timestamp startDate, Timestamp endDate, String description,
			String url) {
		this.id = id;
		this.requestId = requestId;
		this.startDate = startDate;
		this.endDate = endDate;
		this.description = description;
		this.url = url;
	}
	
    public AppointmentDTO(Timestamp startDate, String description) {
        this.startDate = startDate;
        this.description = description;
    }
    
	public AppointmentDTO(String description) {
		this.description = description;
    }
    
	public AppointmentDTO() {
        //Nothing to do here
    }

	public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public Integer getRequestId() {
        return requestId;
    }

    public void setRequestId(Integer requestId) {
        this.requestId = requestId;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public Timestamp getStartDate() {
        return startDate;
    }

    public void setStartDate(Timestamp startDate) {
        this.startDate = startDate;
    }
    
    public Timestamp getEndDate() {
    	return endDate;
    }
    
    public void setEndDate(Timestamp endDate) {
    	this.endDate = endDate;
    }
    
	public String getUrl() {
		return url;
	}

	public void setUrl(String url) {
		this.url = url;
	}
    
    public void assertValid(AppointmentConfig appointmentConfig) {
        if((startDate == null) == appointmentConfig.isStartDate() ||
            (endDate == null) == (appointmentConfig.isEndDate() || appointmentConfig.isEndTime()) ||
            (description == null || description.trim().isEmpty()) == appointmentConfig.isDescription() ||
            (url == null || url.trim().isEmpty()) == appointmentConfig.isUrl()
        ) throw new MalformedException("Missing or invalid input fields");
    }
}
