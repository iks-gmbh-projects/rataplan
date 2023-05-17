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

    @OneToMany(cascade = CascadeType.ALL)
    @JoinColumn(name = "questionId", nullable = false, insertable = false, updatable = false)
    private List<Answer> answers;

    @OneToOne(mappedBy = "question", cascade = CascadeType.ALL, orphanRemoval = true)
    private CheckboxGroup checkboxGroup;

    public Question(EncryptedString text, boolean required, boolean hasCheckbox) {
        this.text = text;
        this.required = required;
        this.hasCheckbox = hasCheckbox;
    }

    boolean checkIfComplete() {
        if (this.hasCheckbox) {
            return this.checkboxGroup != null && checkboxGroup.checkIfComplete();
        }
        return true;
    }

    boolean validate() {
        return validateData() && (!this.hasCheckbox || checkboxGroup.validate(this.required));
    }

    private boolean validateData() {
        return this.text != null && this.text.getString().length() <= 500;
    }
}
