package de.iks.rataplan.exceptions;

public class UnconfirmedAccountException extends RataplanAuthException {
    public UnconfirmedAccountException(String message) {
        super(message);
    }

}
