package de.iks.rataplan.dto;

import de.iks.rataplan.domain.VoteOptionConfig;
import de.iks.rataplan.exceptions.MalformedException;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.sql.Timestamp;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class VoteOptionDTO implements Serializable {

    private static final long serialVersionUID = 1461651856713616814L;

    private Integer id;
    private Integer voteId;
    private Timestamp startDate;
    private Timestamp endDate;
    private String description;
	private String url;
    private boolean participantLimitActive;
    private Integer participantLimit;
	
    public VoteOptionDTO(Timestamp startDate, String description) {
        this.startDate = startDate;
        this.description = description;
    }
    
	public VoteOptionDTO(String description) {
		this.description = description;
    }
    
    public void assertValid(VoteOptionConfig voteOptionConfig) {
        if((startDate == null) == voteOptionConfig.isStartDate() ||
            (endDate == null) == (voteOptionConfig.isEndDate() || voteOptionConfig.isEndTime()) ||
            (description == null || description.trim().isEmpty()) == voteOptionConfig.isDescription() ||
            (url == null || url.trim().isEmpty()) == voteOptionConfig.isUrl()
        ) throw new MalformedException("Missing or invalid input fields");
    }
}
