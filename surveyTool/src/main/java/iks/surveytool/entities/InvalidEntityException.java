package iks.surveytool.entities;

import lombok.Getter;

@Getter
public class InvalidEntityException extends Exception {
    private final AbstractEntity entity;
    public InvalidEntityException(String message, AbstractEntity entity) {
        super(message);
        this.entity = entity;
    }
    
    public InvalidEntityException(String message, Throwable cause, AbstractEntity entity) {
        super(message, cause);
        this.entity = entity;
    }
    
    public InvalidEntityException(Throwable cause, AbstractEntity entity) {
        super(cause.getMessage(), cause);
        this.entity = entity;
    }
}
