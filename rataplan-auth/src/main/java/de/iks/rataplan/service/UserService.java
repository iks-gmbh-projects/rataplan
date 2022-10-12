package de.iks.rataplan.service;

import de.iks.rataplan.domain.EmailChange;
import de.iks.rataplan.domain.PasswordChange;
import de.iks.rataplan.domain.User;

public interface UserService {
    User registerUser(User user);

    boolean checkIfMailExists(String mail);

    boolean checkIfUsernameExists(String username);

    User loginUser(User user);

    User getUserData(String username);

    Boolean changePassword(String token, PasswordChange passwords);

    Boolean changeEmail (String token, String email);

    Boolean changePasswordByToken(User user, String password);

    User getUserFromId(int userId);
}
