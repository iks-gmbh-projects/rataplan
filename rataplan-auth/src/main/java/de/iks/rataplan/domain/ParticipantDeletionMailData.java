package de.iks.rataplan.domain;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@RequiredArgsConstructor
public class ParticipantDeletionMailData {

    private String email;
    private String voteToken;

}
