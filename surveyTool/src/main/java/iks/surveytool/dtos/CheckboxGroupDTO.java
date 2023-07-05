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
    public boolean valid() {
        if(!multipleSelect) return true;
        final int choiceCount = checkboxes.size();
        return 0 <= minSelect && minSelect <= maxSelect && maxSelect <= choiceCount && checkboxes.stream().allMatch(CheckboxDTO::valid);
    }
}
