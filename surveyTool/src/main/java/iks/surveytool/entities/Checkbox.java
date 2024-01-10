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

    @ManyToOne(cascade = {CascadeType.DETACH, CascadeType.MERGE, CascadeType.PERSIST, CascadeType.REFRESH})
    @JoinColumn(name = "checkboxGroupId", nullable = false)
    private CheckboxGroup checkboxGroup;

    @ManyToMany(mappedBy = "checkboxes", cascade = CascadeType.ALL)
    private List<Answer> answers;

    public Checkbox(EncryptedString text, boolean hasTextField) {
        this.text = text;
        this.hasTextField = hasTextField;
    }

    void validate() throws InvalidEntityException {
        if(this.text == null) throw new InvalidEntityException("Response option is empty", this);
        if(this.text.getString().length() > 255) throw new InvalidEntityException("Response option too long", this);
    }
}
