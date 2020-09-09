package de.iks.rataplan.restservice;

import org.springframework.http.ResponseEntity;

import de.iks.rataplan.domain.AuthUser;
import de.iks.rataplan.domain.PasswordChange;

public interface AuthService {
	ResponseEntity<AuthUser> getUserData(String token);

	ResponseEntity<AuthUser> registerUser(AuthUser authUser);

	ResponseEntity<AuthUser> loginUser(AuthUser authUser);
	
	ResponseEntity<Boolean> changePassword(String token, PasswordChange passwords); 
}
