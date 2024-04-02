package de.iks.rataplan.dto;

import de.iks.rataplan.domain.VoteOption;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;
import java.util.List;
import java.util.Map;

@AllArgsConstructor
@NoArgsConstructor
@Data
public class ResultDTO {
    
    private String username;
    private Map<Integer,VoteAnswerDTO> voteOptionAnswers;
    
    
    public ResultDTO(String username){
        this.username = username;
    }
    //username
    //vote answer
    //vote to which they answered
}
