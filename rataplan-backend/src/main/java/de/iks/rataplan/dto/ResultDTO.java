package de.iks.rataplan.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.sql.Date;
import java.time.LocalDateTime;
import java.util.Map;

@AllArgsConstructor
@NoArgsConstructor
@Data
public class ResultDTO {
    
    private String username;
    private Map<Integer, Integer> voteOptionAnswers;
    private LocalDateTime lastUpdated;
    
    public ResultDTO(String username, LocalDateTime lastUpdated) {
        this.username = username;
        this.lastUpdated = lastUpdated;
    }
}
