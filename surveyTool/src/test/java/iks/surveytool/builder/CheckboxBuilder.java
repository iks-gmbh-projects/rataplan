package iks.surveytool.builder;

import iks.surveytool.entities.Answer;
import iks.surveytool.entities.Checkbox;
import iks.surveytool.entities.CheckboxGroup;

import java.util.ArrayList;
import java.util.List;

public class CheckboxBuilder {
    Long id = 1L;
    String text = "Default Choice";
    boolean hasTextField = false;
    CheckboxGroup checkboxGroup = new CheckboxGroup();
    List<Answer> answers = new ArrayList<>();

    public CheckboxBuilder setId(Long id) {
        this.id = id;
        return this;
    }

    public CheckboxBuilder setText(String text) {
        this.text = text;
        return this;
    }

    public CheckboxBuilder setHasTextField(boolean hasTextField) {
        this.hasTextField = hasTextField;
        return this;
    }

    public CheckboxBuilder setCheckboxGroup(CheckboxGroup checkboxGroup) {
        this.checkboxGroup = checkboxGroup;
        return this;
    }

    public CheckboxBuilder setAnswers(List<Answer> answers) {
        this.answers = answers;
        return this;
    }

    public Checkbox build() {
        Checkbox newCheckbox = new Checkbox();
        newCheckbox.setId(id);
        newCheckbox.setText(text);
        newCheckbox.setHasTextField(hasTextField);
        newCheckbox.setCheckboxGroup(checkboxGroup);
        newCheckbox.setAnswers(answers);
        return newCheckbox;
    }
}
