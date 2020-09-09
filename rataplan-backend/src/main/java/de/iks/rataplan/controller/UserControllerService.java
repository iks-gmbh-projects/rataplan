package de.iks.rataplan.controller;

import javax.servlet.http.HttpServletResponse;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import de.iks.rataplan.domain.AuthUser;
import de.iks.rataplan.domain.BackendUser;
import de.iks.rataplan.domain.FrontendUser;
import de.iks.rataplan.domain.PasswordChange;
import de.iks.rataplan.restservice.AuthService;
import de.iks.rataplan.service.BackendUserService;
import de.iks.rataplan.utils.CookieBuilder;

@Service
public class UserControllerService {

	@Autowired
	private AuthService authService;

	@Autowired
	private BackendUserService backendUserService;

	@Autowired
	private HttpServletResponse servletResponse;

	@Autowired
	private CookieBuilder cookieBuilder;

	@Autowired
	private AuthorizationControllerService authorizationControllerService;

	public FrontendUser registerUser(FrontendUser frontendUser) {

		ResponseEntity<AuthUser> authServiceResponse = authService.registerUser(new AuthUser(frontendUser));
		AuthUser authUser = authServiceResponse.getBody();
		authorizationControllerService.refreshCookie(authServiceResponse.getHeaders().getFirst("jwttoken"));

		frontendUser.setId(authUser.getId());

		BackendUser backendUser = backendUserService.createBackendUser(new BackendUser(authUser.getId()));
		return new FrontendUser(authUser, backendUser);
	}

	public FrontendUser loginUser(FrontendUser frontendUser) {

		ResponseEntity<AuthUser> authServiceResponse = authService.loginUser(new AuthUser(frontendUser));
		AuthUser authUser = authServiceResponse.getBody();
		authorizationControllerService.refreshCookie(authServiceResponse.getHeaders().getFirst("jwttoken"));

		BackendUser backendUser = backendUserService.getBackendUserByAuthUserId(authUser.getId());

		if (backendUser == null) {
			backendUser = backendUserService.createBackendUser(new BackendUser(authUser.getId()));
		}

		return new FrontendUser(authUser, backendUser);
	}

	public void logoutUser() {
		this.servletResponse.addCookie(this.cookieBuilder.createJWTCookie(null, true));
	}

	public FrontendUser getUserData(String jwtToken) {

		ResponseEntity<AuthUser> authServiceResponse = authService.getUserData(jwtToken);
		AuthUser authUser = authServiceResponse.getBody();
		authorizationControllerService.refreshCookie(authServiceResponse.getHeaders().getFirst("jwttoken"));

		BackendUser backendUser = backendUserService.getBackendUserByAuthUserId(authUser.getId());

		return new FrontendUser(authUser, backendUser);
	}
	
	public boolean changePassword(PasswordChange passwords, String jwtToken) {

		ResponseEntity<Boolean> response = this.authService.changePassword(jwtToken, passwords);
		return response.getBody();
	}

}
