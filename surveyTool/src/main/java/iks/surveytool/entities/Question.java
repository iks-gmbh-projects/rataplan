package iks.surveytool.entities;

import iks.surveytool.mapping.crypto.DBEncryptedStringConverter;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import java.util.List;

@Entity
@Getter
@Setter
@NoArgsConstructor
public class Question extends AbstractEntity {

    @NotNull
    @Convert(converter = DBEncryptedStringConverter.class)
    private EncryptedString text;
    @NotNull
    private boolean required;
    @NotNull
    private boolean hasCheckbox;

    @ManyToOne(cascade = {CascadeType.DETACH, CascadeType.MERGE, CascadeType.PERSIST, CascadeType.REFRESH})
    @JoinColumn(name = "questionGroupId", nullable = false)
    private QuestionGroup questionGroup;

    @OneToMany(cascade = CascadeType.ALL, mappedBy = "question")
    private List<Answer> answers;

    @OneToOne(mappedBy = "question", cascade = CascadeType.ALL, orphanRemoval = true)
    private CheckboxGroup checkboxGroup;

    public Question(EncryptedString text, boolean required, boolean hasCheckbox) {
        this.text = text;
        this.required = required;
        this.hasCheckbox = hasCheckbox;
    }

    void checkIfComplete() throws InvalidEntityException {
        if (this.hasCheckbox) {
            if(this.checkboxGroup == null) throw new InvalidEntityException("Has Checkbox is true but Checkbox is missing", this);
            checkboxGroup.checkIfComplete();
        }
    }

    void validate() throws InvalidEntityException {
        this.validateData();
        if(this.hasCheckbox) checkboxGroup.validate(this.required);
    }

    private void validateData() throws InvalidEntityException {
        if(this.text == null) throw new InvalidEntityException("Question text missing", this);
        if(this.text.getString().length() > 500) throw new InvalidEntityException("Question text too long", this);
    }
}
