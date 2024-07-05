package iks.surveytool.entities;

import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import javax.persistence.*;
import java.time.LocalDateTime;

@Getter
@Setter
@MappedSuperclass
@EntityListeners(AuditingEntityListener.class)
public abstract class AbstractEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @CreationTimestamp
    @Column(updatable = false)
    private LocalDateTime creationTime;

    @UpdateTimestamp
    private LocalDateTime lastUpdated;

    @Version
    private Integer version;
    
    protected void invalid(String message) throws InvalidEntityException {
        throw new InvalidEntityException(message, this);
    }
    protected void invalid(Throwable cause) throws InvalidEntityException {
        throw new InvalidEntityException(cause, this);
    }
    protected void invalid(String message, Throwable cause) throws InvalidEntityException {
        throw new InvalidEntityException(message, cause, this);
    }
}