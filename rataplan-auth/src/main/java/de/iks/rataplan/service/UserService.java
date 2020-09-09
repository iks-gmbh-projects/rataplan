package de.iks.rataplan.service;

import de.iks.rataplan.domain.PasswordChange;
import de.iks.rataplan.domain.User;

public interface UserService {
    public User registerUser(User user);
    public User loginUser(User user);
    public User getUserData(String username);
    public Boolean changePassword(String token, PasswordChange passwords);
}
