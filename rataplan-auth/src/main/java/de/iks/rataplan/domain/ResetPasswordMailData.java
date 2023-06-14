package de.iks.rataplan.domain;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ResetPasswordMailData {

    private String mail;
    private String token;

}
