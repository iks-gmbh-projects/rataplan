package de.iks.rataplan.dto;

import de.iks.rataplan.exceptions.MalformedException;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

@Data
@NoArgsConstructor
public class VoteParticipantDTO implements Serializable {

    private static final long serialVersionUID = 359333166152845707L;

    private Integer id;
    private Integer userId;
    private Integer voteId;
    private String name;
    private List<VoteDecisionDTO> decisions = new ArrayList<>();

    public VoteParticipantDTO(String name) {
        this.name = name;
    }

    public void assertAddValid() {
        if(voteId == null ||
            name == null || name.trim().isEmpty() ||
            decisions == null || decisions.isEmpty()
        ) throw new MalformedException("Missing or invalid fields");
        decisions.forEach(VoteDecisionDTO::assertAddValid);
    }
    public void assertUpdateValid() {
        if((name != null && name.trim().isEmpty()) ||
            (decisions != null && decisions.isEmpty())
        ) throw new MalformedException("Missing or invalid fields");
        if(decisions != null) decisions.forEach(VoteDecisionDTO::assertAddValid);
    }
}
