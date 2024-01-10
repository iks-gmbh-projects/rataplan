package de.iks.rataplan.domain;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.sql.Timestamp;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
public class AuthToken {
    private int id;
    private String token;
    private Timestamp createdDateTime;
}
