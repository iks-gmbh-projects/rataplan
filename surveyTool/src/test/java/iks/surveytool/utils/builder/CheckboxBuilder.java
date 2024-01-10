package iks.surveytool.utils.builder;

import iks.surveytool.entities.Checkbox;
import iks.surveytool.entities.EncryptedString;

public class CheckboxBuilder {
    public Checkbox createCheckbox(Long id,
                                   String text,
                                   boolean hasTextField) {
        Checkbox newCheckbox = new Checkbox();
        newCheckbox.setId(id);
        newCheckbox.setText(text == null ? null : new EncryptedString(text, false));
        newCheckbox.setHasTextField(hasTextField);
        return newCheckbox;
    }
}
