package de.iks.rataplan.dto;

import de.iks.rataplan.domain.VoteOption;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;
import java.util.Map;

@AllArgsConstructor
@NoArgsConstructor
@Data
public class ResultsDTO {
    
    private String username;
    private Map<Integer,Integer> voteOptionAnswers;
    
    public ResultsDTO(String username){
        this.username = username;
    }
    //username
    //vote answer
    //vote to which they answered
}
