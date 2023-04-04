package de.iks.rataplan.domain;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.sun.istack.Nullable;
import de.iks.rataplan.service.CryptoServiceImpl;
import lombok.Getter;
import lombok.Setter;
import org.springframework.beans.factory.annotation.Autowired;

import javax.persistence.*;

import static de.iks.rataplan.domain.User.trimAndNull;


@Getter
@Setter
public class UserDTO  {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    private String username;
    private String displayname;
    private String mail;
    @Nullable
    private String password;




    public UserDTO (){

    }
    public UserDTO(int id, String userName, String displayName, String mail) {
        this.id = id;
        this.username = userName;
        this.displayname = displayName;
        this.mail = mail;
    }

    public UserDTO (User user){
        this(user.getId(),user.getUsername(),user.getDisplayname(),user.getMail());
    }




}
