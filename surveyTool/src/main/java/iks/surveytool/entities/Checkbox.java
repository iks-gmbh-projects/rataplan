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
public class Checkbox extends AbstractEntity {

    @NotNull
    @Convert(converter = DBEncryptedStringConverter.class)
    private EncryptedString text;
    @NotNull
    private boolean hasTextField;

    @ManyToOne
    @JoinColumn(name = "checkboxGroupId", nullable = false)
    private CheckboxGroup checkboxGroup;

    @ManyToMany(mappedBy = "checkboxes", cascade = CascadeType.ALL)
    private List<Answer> answers;

    public Checkbox(EncryptedString text, boolean hasTextField) {
        this.text = text;
        this.hasTextField = hasTextField;
    }

    boolean validate() {
        return this.text != null && this.text.getString().length() <= 255;
    }
}
