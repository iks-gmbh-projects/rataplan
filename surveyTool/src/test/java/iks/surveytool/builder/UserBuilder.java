package iks.surveytool.builder;

import iks.surveytool.entities.User;

public class UserBuilder {
    public User createUser(Long id, String name) {
        User newUser = new User();
        newUser.setId(id);
        newUser.setName(name);
        return newUser;
    }
}
