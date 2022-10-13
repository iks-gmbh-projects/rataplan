package de.iks.rataplan.restservice;

import com.sendgrid.Email;
import de.iks.rataplan.domain.*;
import org.springframework.http.ResponseEntity;

public interface AuthService {
    ResponseEntity<AuthUser> getUserData(String token);

    ResponseEntity<AuthUser> registerUser(AuthUser authUser);

    ResponseEntity<Boolean> checkIfMailExists(String mail);

    ResponseEntity<Boolean> checkIfUsernameExists(String username);

    ResponseEntity<AuthUser> loginUser(AuthUser authUser);

    ResponseEntity<Boolean> changePassword(String token, PasswordChange passwords);

    ResponseEntity<Boolean> changeEmail(String token , String email);

    ResponseEntity<AuthToken> saveAuthTokenToUserWithMail(String mail);

    ResponseEntity<Boolean> resetPassword(ResetPasswordData resetPasswordData);
}
