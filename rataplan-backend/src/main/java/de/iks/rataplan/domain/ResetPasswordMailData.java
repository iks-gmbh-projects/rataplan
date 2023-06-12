package de.iks.rataplan.domain;

import lombok.Data;

@Data
public class ResetPasswordMailData {
    private String mail;
    private String token;
}
