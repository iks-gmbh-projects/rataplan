package de.iks.rataplan.domain;


import lombok.*;

@Data
public class ResetPasswordData {

    private String token;
    private String password;

}
