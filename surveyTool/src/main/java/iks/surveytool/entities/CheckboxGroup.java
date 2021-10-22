package iks.surveytool.entities;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Data;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import java.time.LocalDateTime;
import java.util.List;

@Entity
@Data
public class CheckboxGroup extends AbstractEntity {

    @NotNull
    private boolean multipleSelect;

    private int minSelect;
    private int maxSelect;

    @CreationTimestamp
    @JsonIgnore
    private LocalDateTime creationTime;

    @UpdateTimestamp
    @JsonIgnore
    private LocalDateTime lastUpdated;

    @OneToOne(cascade = CascadeType.ALL)
    @JoinColumn(name = "question_id")
    private Question question;

    @OneToMany(cascade = CascadeType.ALL)
    @JoinColumn(name = "checkbox_group_id")
    private List<Checkbox> checkboxes;
}
