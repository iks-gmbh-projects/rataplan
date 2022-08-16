package de.iks.rataplan.domain;


import lombok.*;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@ToString
public class ResetPasswordData {

    private String token;
    private String password;

}
