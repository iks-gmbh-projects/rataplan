package de.iks.rataplan.controller;

import de.iks.rataplan.domain.*;
import de.iks.rataplan.exceptions.RataplanAuthException;
import de.iks.rataplan.service.AuthTokenService;
import de.iks.rataplan.service.MailService;
import de.iks.rataplan.utils.CookieBuilder;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import de.iks.rataplan.exceptions.InvalidTokenException;
import de.iks.rataplan.service.JwtTokenService;
import de.iks.rataplan.service.UserService;

@RequiredArgsConstructor
@RestController
@RequestMapping("/v1")
public class RataplanAuthRestController {


    private final UserService userService;


    private final AuthTokenService authTokenService;

    private final MailService mailService;

    private final JwtTokenService jwtTokenService;

    private static final String JWT_COOKIE_NAME = "jwttoken";

    private final CookieBuilder cookieBuilder;

    @RequestMapping(value = "*", method = RequestMethod.OPTIONS, produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
    public ResponseEntity<?> handle() {
        return new ResponseEntity<>(HttpStatus.OK);
    }

    @RequestMapping(value = "/users/register", method = RequestMethod.POST, consumes = MediaType.APPLICATION_JSON_UTF8_VALUE)
    public ResponseEntity<User> registerUser(@RequestBody User user) {
        User dbUser = userService.registerUser(user);

        HttpHeaders responseHeaders = createResponseHeaders(dbUser);

        return new ResponseEntity<>(dbUser, responseHeaders, HttpStatus.CREATED);
    }

    @RequestMapping(value = "/users/mailExists", method = RequestMethod.POST)
    public boolean checkIfMailExists(@RequestBody String mail) {

        return userService.checkIfMailExists(mail);
    }

    @RequestMapping(value = "/users/usernameExists", method = RequestMethod.POST)
    public boolean checkIfUsernameExists(@RequestBody String username) {

        return userService.checkIfUsernameExists(username);
    }

    @RequestMapping(value = "/users/login", method = RequestMethod.POST, consumes = MediaType.APPLICATION_JSON_UTF8_VALUE)
    public ResponseEntity<User> loginUser(@RequestBody User user) {
        User dbUser = userService.loginUser(user);

        HttpHeaders responseHeaders = createResponseHeaders(dbUser);

        return new ResponseEntity<>(dbUser, responseHeaders, HttpStatus.OK);
    }

    @GetMapping("/users/logout")
    public ResponseEntity<Boolean> logoutUser(
            @CookieValue(value = JWT_COOKIE_NAME) String tokenCookie
    ) {
        HttpHeaders responseHeaders = new HttpHeaders();
        responseHeaders.add("Set-Cookie", cookieBuilder.generateCookieValue(tokenCookie, true));
        return ResponseEntity.ok().headers(responseHeaders).body(true);
    }

    @RequestMapping(value = "/users/profile", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
    public ResponseEntity<User> getUserData(
            @RequestHeader(value = JWT_COOKIE_NAME, required = false) String tokenHeader,
            @CookieValue(value = JWT_COOKIE_NAME, required = false) String tokenCookie
    ) {
        String token;
        if (tokenHeader == null) token = tokenCookie;
        else if (tokenCookie == null) token = tokenHeader;
        else if (tokenCookie.equals(tokenHeader)) token = tokenCookie;
        else throw new RataplanAuthException("Different tokens provided by cookie and header.");

        if (!jwtTokenService.isTokenValid(token)) {
            throw new InvalidTokenException("Invalid token");
        }
        String username = jwtTokenService.getUsernameFromToken(token);
        User dbUser = userService.getUserData(username);

        HttpHeaders responseHeaders = createResponseHeaders(dbUser);
        return new ResponseEntity<>(dbUser, responseHeaders, HttpStatus.OK);
    }

    @RequestMapping(value = "/users/profile/changePassword", method = RequestMethod.POST, consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<Boolean> changePassword(
            @RequestHeader(value = JWT_COOKIE_NAME, required = false) String tokenHeader,
            @CookieValue(value = JWT_COOKIE_NAME, required = false) String tokenCookie,
            @RequestBody PasswordChange passwords
    ) {
        String token;
        if (tokenHeader == null) token = tokenCookie;
        else if (tokenCookie == null) token = tokenHeader;
        else if (tokenCookie.equals(tokenHeader)) token = tokenCookie;
        else throw new RataplanAuthException("Different tokens provided by cookie and header.");

        if (!jwtTokenService.isTokenValid(token)) {
            throw new InvalidTokenException("Invalid token");
        }
        String username = jwtTokenService.getUsernameFromToken(token);
        boolean success = this.userService.changePassword(username, passwords);
        return new ResponseEntity<>(success, HttpStatus.OK);
    }

    @RequestMapping(value = "/users/profile/changeEmail", method = RequestMethod.POST, consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<Boolean> changeEmail(
            @RequestHeader(value = JWT_COOKIE_NAME, required = false) String tokenHeader,
            @CookieValue(value = JWT_COOKIE_NAME, required = false) String tokenCookie,
            @RequestBody String email
    ) {
        String token;
        if (tokenHeader == null) token = tokenCookie;
        else if (tokenCookie == null) token = tokenHeader;
        else if (tokenCookie.equals(tokenHeader)) token = tokenCookie;
        else throw new RataplanAuthException("Different tokens provided by cookie and header.");

        if (!jwtTokenService.isTokenValid(token)) {
            throw new InvalidTokenException("Invalid token");
        }
        String username = jwtTokenService.getUsernameFromToken(token);
        boolean success = this.userService.changeEmail(username, email);
        return new ResponseEntity<>(success, HttpStatus.OK);
    }

    @RequestMapping(value = "/users/profile/changeDisplayName", method = RequestMethod.POST, consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<Boolean> changeDisplayName(@RequestHeader(value = JWT_COOKIE_NAME, required = true) String token,
                                                     @RequestBody String displayName) {
        if (!jwtTokenService.isTokenValid(token)) {
            throw new InvalidTokenException("Invalid token");
        }
        String username = jwtTokenService.getUsernameFromToken(token);
        boolean success = this.userService.changeDisplayName(username, displayName);

        return new ResponseEntity<>(success, HttpStatus.OK);
    }

    @PostMapping("/users/forgotPassword")
    public boolean sendForgotPasswordMail(@RequestBody String mail) {
        AuthToken response = authTokenService.saveAuthTokenToUserWithMail(mail);

        ResetPasswordMailData resetPasswordMailData = new ResetPasswordMailData();
        resetPasswordMailData.setMail(mail);
        resetPasswordMailData.setToken(response.getToken());

        mailService.sendMailForResetPassword(resetPasswordMailData);
        return true;
    }

    @RequestMapping(value = "/users/resetPassword", method = RequestMethod.POST, consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<Boolean> resetPassword(@RequestBody ResetPasswordData resetPasswordData) {
        if (this.authTokenService.verifyAuthToken(resetPasswordData.getToken())) {
            int userId = this.authTokenService.getIdFromAuthToken(resetPasswordData.getToken());
            User user = this.userService.getUserFromId(userId);
            boolean success = this.userService.changePasswordByToken(user, resetPasswordData.getPassword());
            this.authTokenService.deleteById(userId);

            return new ResponseEntity<>(success, HttpStatus.OK);
        }
        return new ResponseEntity<>(false, HttpStatus.UNAUTHORIZED);
    }

    private HttpHeaders createResponseHeaders(User user) {
        HttpHeaders responseHeaders = new HttpHeaders();

        String token = jwtTokenService.generateToken(user);
        responseHeaders.set(JWT_COOKIE_NAME, token);
        responseHeaders.add("Set-Cookie", cookieBuilder.generateCookieValue(token, false));

        return responseHeaders;
    }

}
