package iks.surveytool.builder;

import iks.surveytool.entities.Checkbox;

public class CheckboxBuilder {
    public Checkbox createCheckbox(Long id,
                                   String text,
                                   boolean hasTextField) {
        Checkbox newCheckbox = new Checkbox();
        newCheckbox.setId(id);
        newCheckbox.setText(text);
        newCheckbox.setHasTextField(hasTextField);
        return newCheckbox;
    }
}
