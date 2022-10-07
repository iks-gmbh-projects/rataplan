package iks.surveytool.entities;

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
    private String text;
    @NotNull
    private boolean hasTextField;

    @ManyToOne
    @JoinColumn(name = "checkboxGroupId", nullable = false)
    private CheckboxGroup checkboxGroup;

    @ManyToMany(mappedBy = "checkboxes", cascade = CascadeType.ALL)
    private List<Answer> answers;

    public Checkbox(String text, boolean hasTextField) {
        this.text = text;
        this.hasTextField = hasTextField;
    }

    boolean validate() {
        return this.text != null && this.text.length() <= 255;
    }
}
