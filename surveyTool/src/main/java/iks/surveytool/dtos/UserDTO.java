package iks.surveytool.dtos;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class UserDTO {

    private Long id;
    private String name;

    public UserDTO(Long id, String name) {
        this.id = id;
        this.name = name;
    }
}
