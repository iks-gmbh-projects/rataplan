package iks.surveytool.dtos;

import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
public class CheckboxGroupDTO {

    private Long id;
    private boolean multipleSelect;
    private int minSelect;
    private int maxSelect;

    private List<CheckboxDTO> checkboxes;

    public CheckboxGroupDTO(Long id, boolean multipleSelect, int minSelect, int maxSelect, List<CheckboxDTO> checkboxes) {
        this.id = id;
        this.multipleSelect = multipleSelect;
        this.minSelect = minSelect;
        this.maxSelect = maxSelect;
        this.checkboxes = checkboxes;
    }
}
