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
public class Question extends AbstractEntity {

    @NotNull
    private String text;
    @NotNull
    private boolean required;
    @NotNull
    private boolean hasCheckbox;

    @ManyToOne
    @JoinColumn(name = "question_group_id")
    private QuestionGroup questionGroup;

    @JsonIgnore
    @OneToMany(cascade = CascadeType.ALL)
    @JoinColumn(name = "question_id")
    private List<Answer> answers;

    @OneToOne(mappedBy = "question", cascade = CascadeType.ALL)
    private CheckboxGroup checkboxGroup;
}
