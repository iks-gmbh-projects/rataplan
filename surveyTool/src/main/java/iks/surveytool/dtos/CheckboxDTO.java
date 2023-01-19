package iks.surveytool.dtos;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class CheckboxDTO extends AbstractDTO {

    private String text;
    private boolean hasTextField;

    public CheckboxDTO(Long id, String text, boolean hasTextField) {
        super(id);
        this.text = text;
        this.hasTextField = hasTextField;
    }
    
    @Override
    public boolean valid() {
        return text != null && !text.isBlank();
    }
}
