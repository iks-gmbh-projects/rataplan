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

    public boolean sendForgotPasswordMail(String mail) {

        AuthToken response = authService.saveAuthTokenToUserWithMail(mail).getBody();

        ResetPasswordMailData resetPasswordMailData = new ResetPasswordMailData();
        resetPasswordMailData.setMail(mail);
        resetPasswordMailData.setToken(response.getToken());

        mailServiceImplSendGrid.sendMailForResetPassword(resetPasswordMailData);

        return true;
    }
}
