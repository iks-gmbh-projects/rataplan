package iks.surveytool.entities;

import lombok.*;

import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import javax.persistence.*;
import java.time.LocalDateTime;

@Data
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
@ToString(onlyExplicitlyIncluded = true)
@MappedSuperclass
@EntityListeners(AuditingEntityListener.class)
public abstract class AbstractEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @EqualsAndHashCode.Include
    @ToString.Include
    private Long id;

    @CreationTimestamp
    @Column(updatable = false)
    private LocalDateTime creationTime;

    @UpdateTimestamp
    private LocalDateTime lastUpdated;

    @Version
    private Integer version;
    
    public void resetId() {
        id = null;
    }
    
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