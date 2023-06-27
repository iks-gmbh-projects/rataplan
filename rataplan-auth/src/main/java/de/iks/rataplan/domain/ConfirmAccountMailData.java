package de.iks.rataplan.domain;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@ToString
@AllArgsConstructor
public class ConfirmAccountMailData {
    private String token;
    private String emailAddress;
}
