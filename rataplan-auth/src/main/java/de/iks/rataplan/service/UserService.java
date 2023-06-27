package de.iks.rataplan.service;

import de.iks.rataplan.domain.DeleteUserRequest;
import de.iks.rataplan.domain.PasswordChange;
import de.iks.rataplan.domain.User;
import de.iks.rataplan.dto.UserDTO;
import de.iks.rataplan.exceptions.UserDeletionException;
import de.iks.rataplan.exceptions.WrongCredentialsException;

public interface UserService {
    UserDTO registerUser(UserDTO user);
    UserDTO getUserDTOFromUsername(String username);
    boolean checkIfMailExists(String mail);
    boolean checkIfUsernameExists(String username);
    UserDTO loginUser(UserDTO user) throws Exception;
    boolean verifyPassword(User user, String password);
    void verifyPasswordOrThrow(User user, String password) throws WrongCredentialsException;
    User getUserFromUsername(String username);
    Boolean updateProfileDetails(UserDTO userDTO);
    Boolean changePassword(String token, PasswordChange passwords);
    Boolean changePasswordByToken(User user, String password);
    User getUserFromId(int userId);
    void deleteUser(User user, DeleteUserRequest request) throws UserDeletionException;
    User getUserFromEmail (String mail);
    String getDisplayNameFromId(int id);
    Boolean confirmAccount(String token);
    UserDTO validateResendConfirmationEmailRequest(String email);
}
