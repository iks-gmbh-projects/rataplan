package iks.surveytool.entities;

import lombok.Data;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import java.util.List;

@Entity
@Data
public class Checkbox {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    @NotNull
    private String text;
    @NotNull
    private boolean hasTextField;

    @ManyToOne
    @JoinColumn(name = "checkbox_group_id")
    private CheckboxGroup checkboxGroup;

    @OneToMany(mappedBy = "checkbox", cascade = CascadeType.ALL)
    private List<Answer> answers;
}
