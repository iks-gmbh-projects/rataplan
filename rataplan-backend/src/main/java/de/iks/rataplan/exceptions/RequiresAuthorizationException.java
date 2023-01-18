package de.iks.rataplan.exceptions;

import de.iks.rataplan.domain.ErrorCode;
import org.springframework.http.HttpStatus;

public class RequiresAuthorizationException extends RataplanException {
    public RequiresAuthorizationException() {
        super("You must be logged in to view this.");
        super.status = HttpStatus.UNAUTHORIZED;
    }
}
