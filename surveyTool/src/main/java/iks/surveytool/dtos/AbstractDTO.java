package iks.surveytool.dtos;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
abstract class AbstractDTO {
    private Long id;

    public AbstractDTO(Long id) {
        this.id = id;
    }
}
