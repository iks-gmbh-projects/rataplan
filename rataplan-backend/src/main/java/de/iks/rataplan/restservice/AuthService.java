package de.iks.rataplan.restservice;

import de.iks.rataplan.domain.AuthToken;
import de.iks.rataplan.domain.ResetPasswordData;
import org.springframework.http.ResponseEntity;

import de.iks.rataplan.domain.AuthUser;
import de.iks.rataplan.domain.PasswordChange;

public interface AuthService {
    ResponseEntity<AuthUser> getUserData(String token);

    ResponseEntity<AuthUser> registerUser(AuthUser authUser);

    ResponseEntity<Boolean> checkIfMailExists(String mail);

    ResponseEntity<Boolean> checkIfUsernameExists(String username);

    ResponseEntity<AuthUser> loginUser(AuthUser authUser);

    ResponseEntity<Boolean> changePassword(String token, PasswordChange passwords);

    ResponseEntity<AuthToken> saveAuthTokenToUserWithMail(String mail);

    ResponseEntity<Boolean> resetPassword(ResetPasswordData resetPasswordData);
}
