package de.iks.rataplan.domain;


import lombok.*;

@Data
public class EmailChange {
    private String token;
    private String email;
}


