package de.iks.rataplan.controller;

import javax.servlet.http.HttpServletResponse;

import de.iks.rataplan.domain.*;
import de.iks.rataplan.service.MailServiceImplSendGrid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

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

    @Autowired
    private MailServiceImplSendGrid mailServiceImplSendGrid;

    public FrontendUser registerUser(FrontendUser frontendUser) {

        ResponseEntity<AuthUser> authServiceResponse = authService.registerUser(new AuthUser(frontendUser));
        AuthUser authUser = authServiceResponse.getBody();
        authorizationControllerService.refreshCookie(authServiceResponse.getHeaders().getFirst("jwttoken"));

        frontendUser.setId(authUser.getId());

        BackendUser backendUser = backendUserService.createBackendUser(new BackendUser(authUser.getId()));
        return new FrontendUser(authUser, backendUser);
    }

    public boolean checkIfMailExists(String mail) {

        ResponseEntity<Boolean> authServiceResponse = authService.checkIfMailExists(mail);
        return authServiceResponse.getBody();
    }

    public boolean checkIfUsernameExists(String username) {

        ResponseEntity<Boolean> authServiceResponse = authService.checkIfUsernameExists(username);
        return authServiceResponse.getBody();
    }

    public boolean sendForgotPasswordMail(String mail) {

        AuthToken response = authService.saveAuthTokenToUserWithMail(mail).getBody();

        ResetPasswordMailData resetPasswordMailData = new ResetPasswordMailData();
        resetPasswordMailData.setMail(mail);
        resetPasswordMailData.setToken(response.getToken());

        mailServiceImplSendGrid.sendMailForResetPassword(resetPasswordMailData);

        return true;
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
        this.servletResponse.addHeader("Set-Cookie", this.cookieBuilder.generateCookieValue(null, true));
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
    public boolean changeEmail(String email, String jwtToken) {
        ResponseEntity<Boolean> response = this.authService.changeEmail(jwtToken, email);

        System.out.println(response.toString());

        return response.getBody();
    }

    public boolean resetPassword(ResetPasswordData resetPasswordData) {

        ResponseEntity<Boolean> response = this.authService.resetPassword(resetPasswordData);
        return response.getBody();
    }
}
