package iks.surveytool.builder;

import iks.surveytool.entities.CheckboxGroup;

import java.util.ArrayList;

public class CheckboxGroupBuilder {
    public CheckboxGroup createCheckboxGroup(Long id,
                                             boolean multipleSelect,
                                             int minSelect,
                                             int maxSelect) {
        CheckboxGroup newCheckboxGroup = new CheckboxGroup();
        newCheckboxGroup.setId(id);
        newCheckboxGroup.setMultipleSelect(multipleSelect);
        newCheckboxGroup.setMinSelect(minSelect);
        newCheckboxGroup.setMaxSelect(maxSelect);
        newCheckboxGroup.setCheckboxes(new ArrayList<>());
        return newCheckboxGroup;
    }
}
