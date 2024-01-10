package de.iks.rataplan.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class ParticipantDeletionEmailDTO {
    private String email;
    private String voteToken;
}
