package iks.surveytool.entities;

public class InvalidSurveyException extends Exception {
    public InvalidSurveyException(String message) {
        super(message);
    }
    
    public InvalidSurveyException(String message, Throwable cause) {
        super(message, cause);
    }
    
    public InvalidSurveyException(Throwable cause) {
        super(cause.getMessage(), cause);
    }
}
