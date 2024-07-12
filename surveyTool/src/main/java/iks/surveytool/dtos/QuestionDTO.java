package iks.surveytool.dtos;

import iks.surveytool.domain.QuestionType;
import lombok.*;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;

@Data
@EqualsAndHashCode(callSuper = true)
@ToString(callSuper = true)
@NoArgsConstructor
public class QuestionDTO extends AbstractDTO {
    private QuestionType type;
    private int rank;
    private String text;
    private Boolean required;
    private Integer minSelect;
    private Integer maxSelect;

    private List<ChoiceDTO> choices = new ArrayList<>();

    public QuestionDTO(Long id, String text, boolean required) {
        super(id);
        type = QuestionType.OPEN;
        this.text = text;
        this.required = required;
    }
    
    public QuestionDTO(Long id, String text, int minSelect, int maxSelect, Collection<? extends ChoiceDTO> choices) {
        super(id);
        type = QuestionType.CHOICE;
        this.text = text;
        this.minSelect = minSelect;
        this.maxSelect = maxSelect;
        this.choices = new ArrayList<>(choices);
    }
    
    public QuestionDTO(Long id, String text, int minSelect, int maxSelect) {
        this(id, text, minSelect, maxSelect, List.of());
    }
    
    public QuestionDTO(Long id, String text, int minSelect, int maxSelect, ChoiceDTO ...choices) {
        this(id, text, minSelect, maxSelect, Arrays.asList(choices));
    }
    
    @Override
    public void resetId() {
        super.resetId();
        choices.forEach(AbstractDTO::resetId);
    }
    
    @Override
    public void trimAndNull() {
        text = trimAndNull(text);
        if(choices != null) choices.forEach(ChoiceDTO::trimAndNull);
    }
    
    @Override
    public void valid() throws DTOValidationException {
        if(type == null) throw new DTOValidationException("QuestionDTO.type", "null");
        if(this.text != null && this.text.isBlank()) throw new DTOValidationException("QuestionDTO.text", "blank non-null");
        switch(type) {
            case OPEN:
                if(required == null) throw new DTOValidationException("QuestionDTO.required", "null");
                break;
            case CHOICE:
                if(minSelect == null) throw new DTOValidationException("QuestionDTO.minSelect", "null");
                if(maxSelect == null) throw new DTOValidationException("QuestionDTO.maxSelect", "null");
                if(choices == null) throw new DTOValidationException("QuestionDTO.choices", "null");
                if(choices.isEmpty()) throw new DTOValidationException("QuestionDTO.choices", "empty");
                for(ChoiceDTO choice : choices) choice.valid();
                break;
            default:
                throw new DTOValidationException("QuestionDTO.type", "unknown");
        }
    }
}