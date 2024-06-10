package iks.surveytool.dtos;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
@NoArgsConstructor
public class CheckboxGroupDTO extends AbstractDTO {

    private boolean multipleSelect;
    private int minSelect;
    private int maxSelect;

    private List<CheckboxDTO> checkboxes;

    public CheckboxGroupDTO(Long id, boolean multipleSelect, int minSelect, int maxSelect, List<CheckboxDTO> checkboxes) {
        super(id);
        this.multipleSelect = multipleSelect;
        this.minSelect = minSelect;
        this.maxSelect = maxSelect;
        this.checkboxes = checkboxes;
    }
    
    @Override
    public void trimAndNull() {
        if(checkboxes != null) checkboxes.forEach(CheckboxDTO::trimAndNull);
    }
    
    @Override
    public void valid() throws DTOValidationException {
        if(multipleSelect) {
            final int choiceCount = checkboxes.size();
            if(0 > minSelect) throw new DTOValidationException("CheckBoxGroupDTO.minSelect", "<0");
            if(minSelect > maxSelect) throw new DTOValidationException("CheckBoxGroupDTO.maxSelect", "<minSelect");
            if(maxSelect > choiceCount) throw new DTOValidationException("CheckBoxGroupDTO.maxSelect", ">choiceCount");
            for(CheckboxDTO c : checkboxes) {
                c.valid();
            }
        }
    }
}