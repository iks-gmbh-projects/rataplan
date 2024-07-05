package iks.surveytool.entities.answer;

import iks.surveytool.entities.AbstractEntity;
import iks.surveytool.entities.InvalidEntityException;
import iks.surveytool.entities.SurveyResponse;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.CascadeType;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.MappedSuperclass;

@Getter
@Setter
@MappedSuperclass
@NoArgsConstructor
public abstract class AbstractAnswer extends AbstractEntity {
    @ManyToOne(cascade = {CascadeType.DETACH, CascadeType.MERGE, CascadeType.PERSIST, CascadeType.REFRESH})
    @JoinColumn(name = "responseId", nullable = false)
    private SurveyResponse response;
    
    public abstract void validate() throws InvalidEntityException;
}