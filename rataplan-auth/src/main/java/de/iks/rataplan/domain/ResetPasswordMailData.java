package de.iks.rataplan.domain;

public class ResetPasswordMailData {

    private String mail;
    private String token;

    public String getMail() {
        return mail;
    }

    public void setMail(String mail) {
        this.mail = mail;
    }

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }
}
