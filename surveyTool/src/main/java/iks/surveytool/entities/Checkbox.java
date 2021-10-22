package iks.surveytool.entities;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import java.util.List;

@Entity
@Getter
@Setter
public class Checkbox extends AbstractEntity {

    @NotNull
    private String text;
    @NotNull
    private boolean hasTextField;

    @JsonIgnore
    @ManyToOne
    @JoinColumn(name = "checkbox_group_id")
    private CheckboxGroup checkboxGroup;

    @OneToMany(cascade = CascadeType.ALL)
    @JoinColumn(name = "checkbox_id")
    private List<Answer> answers;
}
