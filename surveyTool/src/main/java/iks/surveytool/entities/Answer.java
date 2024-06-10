package iks.surveytool.entities;

import iks.surveytool.mapping.crypto.DBEncryptedStringConverter;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.*;
import java.util.List;
import java.util.Objects;

@Entity
@Getter
@Setter
@NoArgsConstructor
public class Answer extends AbstractEntity {
    
    @Convert(converter = DBEncryptedStringConverter.class)
    private EncryptedString text;

    @ManyToOne(cascade = CascadeType.ALL)
    @JoinColumn(name = "responseId", nullable = false)
    private SurveyResponse response;

    @ManyToOne
    @JoinColumn(name = "questionId", nullable = false)
    private Question question;

    @ManyToMany(cascade = {CascadeType.REFRESH})
    @JoinTable(
            name = "checkboxSelections",
            joinColumns = {
                    @JoinColumn(name = "answerId", referencedColumnName = "id")
            },
            inverseJoinColumns = {
                    @JoinColumn(name = "checkboxId", referencedColumnName = "id")
            }
    )
    private List<Checkbox> checkboxes;

    public Answer(EncryptedString text) {
        this.text = text;
    }

    public void validate() throws InvalidEntityException {
        if (this.response == null || this.question == null) {
            throw new InvalidEntityException("no response or questions", this);
        }
        if(!Objects.equals(this.response.getSurvey().getId(), this.question.getQuestionGroup().getSurvey().getId())) {
            throw new InvalidEntityException("Inconsistent survey id", this);
        }
        if(this.question.isHasCheckbox()) {
            if(this.checkboxes == null) throw new InvalidEntityException("checkbox state inconsistent", this);
            CheckboxGroup grp = this.question.getCheckboxGroup();
            int count = this.checkboxes.size();
            if(grp.isMultipleSelect()) {
                if(grp.getMinSelect() > count || grp.getMaxSelect() < count) throw new InvalidEntityException("multi-selection limitations exceeded", this);
            } else {
                if(count > 1 || (count < 1 && question.isRequired())) throw new InvalidEntityException("single-selection limitations exceeded", this);
            }
        }
        if (!this.question.isHasCheckbox() || this.checkboxes.stream().anyMatch(Checkbox::isHasTextField)) {
            if(!this.checkIfAnswerTextValid()) throw new InvalidEntityException("missing text", this);
        }
    }

    private boolean checkIfAnswerTextValid() {
        if(this.text == null && !this.question.isRequired()) return true;
        return this.text != null && this.text.getString().length() <= 1500;
    }
}