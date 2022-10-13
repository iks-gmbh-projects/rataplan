package de.iks.rataplan.domain;


import lombok.*;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@ToString

public class EmailChange {
    private String token;
    private String email;
}


