package iks.surveytool.dtos;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.UNPROCESSABLE_ENTITY)
public class DTOValidationException extends Exception {
    public final String field;
    public final String reason;
    public DTOValidationException(String field, String reason) {
        super(String.format("%s: %s", field, reason));
        this.field = field;
        this.reason = reason;
    }
    public DTOValidationException(String field, String reason, Throwable cause) {
        super(String.format("%s: %s", field, reason), cause);
        this.field = field;
        this.reason = reason;
    }
}