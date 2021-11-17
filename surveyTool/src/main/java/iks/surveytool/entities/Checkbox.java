package iks.surveytool.entities;

import com.fasterxml.jackson.annotation.JsonIgnore;
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
    @JoinColumn(name = "checkbox_group_id")
    private CheckboxGroup checkboxGroup;

    @JsonIgnore
    @OneToMany(cascade = CascadeType.ALL)
    @JoinColumn(name = "checkbox_id")
    private List<Answer> answers;

    public Checkbox(String text, boolean hasTextField) {
        this.text = text;
        this.hasTextField = hasTextField;
    }
}
