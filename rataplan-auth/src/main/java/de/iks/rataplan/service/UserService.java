package de.iks.rataplan.service;

import de.iks.rataplan.domain.DeleteUserRequest;
import de.iks.rataplan.domain.PasswordChange;
import de.iks.rataplan.domain.User;
import de.iks.rataplan.domain.UserDTO;
import de.iks.rataplan.exceptions.UserDeletionException;
import de.iks.rataplan.exceptions.WrongCredentialsException;

public interface UserService {
    UserDTO registerUser(UserDTO user);

    boolean checkIfMailExists(String mail);

    boolean checkIfUsernameExists(String username);

    UserDTO loginUser(User user);
    
    boolean verifyPassword(User user, String password);
    void verifyPasswordOrThrow(User user, String password) throws WrongCredentialsException;

    User getUserData(String username);

    Boolean changePassword(String token, PasswordChange passwords);

    Boolean changeEmail (String token, String email);

    Boolean changeDisplayName(String token, String email);

    Boolean changePasswordByToken(User user, String password);

    User getUserFromId(int userId);

    void deleteUser(User user, DeleteUserRequest request) throws UserDeletionException;
}
