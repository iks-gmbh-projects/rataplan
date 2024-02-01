package de.iks.rataplan.dto;

import lombok.Data;

@Data
public class UserDTO {

    private Integer id;
    private String username;
    private String displayname;
    private String mail;
    private String password;

    public UserDTO() {
    }

    public UserDTO(Integer id, String userName, String displayName, String mail) {
        this.id = id;
        this.username = userName;
        this.displayname = displayName;
        this.mail = mail;
    }

    public UserDTO(Integer id, String userName, String displayName, String mail, String password) {
        this.id = id;
        this.username = userName;
        this.displayname = displayName;
        this.mail = mail;
        this.password = password;
    }

    public void trimUserCredentials() {
        username = trimAndNull(username);
        mail = trimAndNull(mail);
        displayname = trimAndNull(displayname);
    }

    public static String trimAndNull(String toTrim) {
        if (toTrim != null) {
            toTrim = toTrim.trim();
            if (toTrim.isEmpty()) {
                return null;
            }
        }
        return toTrim;
    }

    public boolean invalidLogin() {
        return (trimAndNull(username) == null && trimAndNull(mail) == null) || password == null;
    }
    
    public boolean invalidFull() {
        return trimAndNull(username) == null ||
            trimAndNull(mail) == null ||
            trimAndNull(displayname) == null ||
            password == null;
    }
}
