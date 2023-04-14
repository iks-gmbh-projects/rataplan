package de.iks.rataplan.dto;

import de.iks.rataplan.domain.User;
import lombok.Getter;
import lombok.Setter;




@Getter
@Setter
public class UserDTO  {

    private int id;
    private String username;
    private String displayname;
    private String mail;
    private String password;

    public UserDTO (){}
    public UserDTO(int id, String userName, String displayName, String mail) {
        this.id = id;
        this.username = userName;
        this.displayname = displayName;
        this.mail = mail;
    }
    public UserDTO(int id, String userName, String displayName, String mail,String password) {
        this.id = id;
        this.username = userName;
        this.displayname = displayName;
        this.mail = mail;
        this.password = password;
    }

    public UserDTO (User user){
        this(user.getId(),user.getUsername(),user.getDisplayname(),user.getMail());
    }
}
