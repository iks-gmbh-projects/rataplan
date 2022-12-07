package de.iks.rataplan.domain;

import lombok.*;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@ToString
public class DeleteUserRequest {
    public enum Method {
        DELETE,
        ANONYMIZE
    }
    private Method surveyToolChoice;
    private Method backendChoice;
    private String password;
}
