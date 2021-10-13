package iks.surveytool.builder;

import iks.surveytool.entities.Checkbox;
import iks.surveytool.entities.CheckboxGroup;
import iks.surveytool.entities.Question;

import java.util.ArrayList;
import java.util.List;

public class CheckboxGroupBuilder {
    Long id = 1L;
    boolean multipleSelect = false;
    int minSelect = 0;
    int maxSelect = 1;
    Question question = new Question();
    List<Checkbox> checkboxes = new ArrayList<>();

    public CheckboxGroupBuilder setId(Long id) {
        this.id = id;
        return this;
    }

    public CheckboxGroupBuilder setMultipleSelect(boolean multipleSelect) {
        this.multipleSelect = multipleSelect;
        return this;
    }

    public CheckboxGroupBuilder setMinSelect(int minSelect) {
        this.minSelect = minSelect;
        return this;
    }

    public CheckboxGroupBuilder setMaxSelect(int maxSelect) {
        this.maxSelect = maxSelect;
        return this;
    }

    public CheckboxGroupBuilder setQuestion(Question question) {
        this.question = question;
        return this;
    }

    public CheckboxGroupBuilder setCheckboxes(List<Checkbox> checkboxes) {
        this.checkboxes = checkboxes;
        return this;
    }

    public CheckboxGroup build() {
        CheckboxGroup newCheckboxGroup = new CheckboxGroup();
        newCheckboxGroup.setId(id);
        newCheckboxGroup.setMultipleSelect(multipleSelect);
        newCheckboxGroup.setMinSelect(minSelect);
        newCheckboxGroup.setMaxSelect(maxSelect);
        newCheckboxGroup.setQuestion(question);
        newCheckboxGroup.setCheckboxes(checkboxes);
        return newCheckboxGroup;
    }
}
