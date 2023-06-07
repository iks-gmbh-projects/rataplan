package de.iks.rataplan.dto;

import de.iks.rataplan.domain.VoteOptionConfig;
import de.iks.rataplan.exceptions.MalformedException;

import java.io.Serializable;
import java.sql.Timestamp;

public class VoteOptionDTO implements Serializable {

    private static final long serialVersionUID = 1461651856713616814L;

    private Integer id;
    private Integer voteId;
    private Timestamp startDate;
    private Timestamp endDate;
    private String description;
	private String url;

    public VoteOptionDTO(Integer id, Integer voteId, Timestamp startDate, Timestamp endDate, String description,
			String url) {
		this.id = id;
		this.voteId = voteId;
		this.startDate = startDate;
		this.endDate = endDate;
		this.description = description;
		this.url = url;
	}
	
    public VoteOptionDTO(Timestamp startDate, String description) {
        this.startDate = startDate;
        this.description = description;
    }
    
	public VoteOptionDTO(String description) {
		this.description = description;
    }
    
	public VoteOptionDTO() {
        //Nothing to do here
    }

	public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public Integer getVoteId() {
        return voteId;
    }

    public void setVoteId(Integer voteId) {
        this.voteId = voteId;
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
    
    public void assertValid(VoteOptionConfig voteOptionConfig) {
        if((startDate == null) == voteOptionConfig.isStartDate() ||
            (endDate == null) == (voteOptionConfig.isEndDate() || voteOptionConfig.isEndTime()) ||
            (description == null || description.trim().isEmpty()) == voteOptionConfig.isDescription() ||
            (url == null || url.trim().isEmpty()) == voteOptionConfig.isUrl()
        ) throw new MalformedException("Missing or invalid input fields");
    }
}
