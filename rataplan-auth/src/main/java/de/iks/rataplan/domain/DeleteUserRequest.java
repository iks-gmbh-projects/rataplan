package de.iks.rataplan.domain;

import lombok.*;

@Data
public class DeleteUserRequest {
    public enum Method {
        DELETE,
        ANONYMIZE
    }
    private Method surveyToolChoice;
    private Method backendChoice;
    private String password;
}
