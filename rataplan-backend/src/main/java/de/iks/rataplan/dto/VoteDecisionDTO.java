package de.iks.rataplan.dto;

import de.iks.rataplan.exceptions.MalformedException;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.sql.Timestamp;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class VoteDecisionDTO implements Serializable {

    private static final long serialVersionUID = -1914437763717575725L;
    
    private Integer optionId;
    private Integer participantId;
    private Integer decision;
    private Integer participants = null;
    private Timestamp lastUpdated;
    
    public void assertAddValid() {
        if(optionId == null || (decision == null && participants == null)) throw new MalformedException("Missing fields");
    }
}
