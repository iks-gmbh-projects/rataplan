package iks.surveytool.dtos;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class UserDTO extends AbstractDTO {

    private String name;

    public UserDTO(Long id, String name) {
        super(id);
        this.name = name;
    }
}
