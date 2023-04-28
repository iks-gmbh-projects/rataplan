package de.iks.rataplan.controller;

import de.iks.rataplan.domain.*;
import de.iks.rataplan.dto.UserDTO;
import de.iks.rataplan.exceptions.RataplanAuthException;
import de.iks.rataplan.exceptions.UserDeletionException;
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
import org.springframework.web.client.ResourceAccessException;

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

    @PostMapping(value = "/users/register", consumes = MediaType.APPLICATION_JSON_UTF8_VALUE)
    public ResponseEntity<UserDTO> registerUser(@RequestBody UserDTO user) {
        UserDTO userDTO = userService.registerUser(user);
        HttpHeaders responseHeaders = createResponseHeaders(userDTO);
        return new ResponseEntity<>(userDTO, responseHeaders, HttpStatus.CREATED);
    }

    @PostMapping(value = "/users/mailExists")
    public boolean checkIfMailExists(@RequestBody String mail) {
        return userService.checkIfMailExists(mail);
    }

    @PostMapping(value = "/users/usernameExists")
    public boolean checkIfUsernameExists(@RequestBody String username) {
        return userService.checkIfUsernameExists(username);
    }

    @PostMapping(value = "/users/login", consumes = MediaType.APPLICATION_JSON_UTF8_VALUE)
    public ResponseEntity<UserDTO> loginUser(@RequestBody UserDTO user) {
        UserDTO userDTO = userService.loginUser(user);
        HttpHeaders responseHeaders = createResponseHeaders(userDTO);

        return new ResponseEntity<>(userDTO, responseHeaders, HttpStatus.OK);
    }

    @GetMapping("/users/logout")
    public ResponseEntity<Boolean> logoutUser(
            @CookieValue(value = JWT_COOKIE_NAME) String tokenCookie
    ) {
        HttpHeaders responseHeaders = new HttpHeaders();
        responseHeaders.add("Set-Cookie", cookieBuilder.generateCookieValue(tokenCookie, true));

        return ResponseEntity.ok().headers(responseHeaders).body(true);
    }

    @DeleteMapping(value = "/users/profile", produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
    public ResponseEntity<?> deleteUserData(
            @RequestHeader(value = JWT_COOKIE_NAME, required = false) String tokenHeader,
            @CookieValue(value = JWT_COOKIE_NAME, required = false) String tokenCookie,
            @RequestBody DeleteUserRequest request
    ) {
        String token = validateTokenOrThrow(tokenCookie, tokenHeader);
        String username = jwtTokenService.getUsernameFromToken(token);
        User dbUser = userService.getUserData(username);
        userService.verifyPasswordOrThrow(dbUser, request.getPassword());
        try {
            userService.deleteUser(dbUser, request);
        } catch (UserDeletionException | ResourceAccessException ex) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(ex.getMessage());
        }

        return logoutUser(token);
    }

    @GetMapping(value = "/users/profile", produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
    public ResponseEntity<UserDTO> getUserData(
            @RequestHeader(value = JWT_COOKIE_NAME, required = false) String tokenHeader,
            @CookieValue(value = JWT_COOKIE_NAME, required = false) String tokenCookie
    ) {
        String token = validateTokenOrThrow(tokenCookie, tokenHeader);
        String username = jwtTokenService.getUsernameFromToken(token);
        UserDTO userDTO = userService.getUserDTOFromUsername(username);
        HttpHeaders responseHeaders = createResponseHeaders(userDTO);

        return new ResponseEntity<>(userDTO, responseHeaders, HttpStatus.OK);
    }

    @PostMapping(value = "/users/profile/updateProfileDetails", consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<Boolean> updateProfileDetails(@RequestHeader(value = JWT_COOKIE_NAME, required = false) String tokenHeader,
                                                        @CookieValue(value = JWT_COOKIE_NAME, required = false) String tokenCookie,
                                                        @RequestBody UserDTO userDTO) {
        String token = validateTokenOrThrow(tokenCookie, tokenHeader);

        if (userService.checkIfUsernameExists(jwtTokenService.getUsernameFromToken((token)))) {
            boolean success = userService.updateProfileDetails(userDTO);
            return new ResponseEntity<>(success, HttpStatus.OK);
        } else {
            return new ResponseEntity<>(false, HttpStatus.UNAUTHORIZED);
        }

    }

    @PostMapping(value = "/users/profile/changePassword", consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<Boolean> changePassword(
            @RequestHeader(value = JWT_COOKIE_NAME, required = false) String tokenHeader,
            @CookieValue(value = JWT_COOKIE_NAME, required = false) String tokenCookie,
            @RequestBody PasswordChange passwords
    ) {
        String token = validateTokenOrThrow(tokenCookie, tokenHeader);
        String username = jwtTokenService.getUsernameFromToken(token);
        boolean success = this.userService.changePassword(username, passwords);

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

    @PostMapping(value = "/users/resetPassword", consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
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

    private String validateTokenOrThrow(String cookieToken, String headerToken) throws RataplanAuthException {
        String token;
        if (headerToken == null) token = cookieToken;
        else if (cookieToken == null) token = headerToken;
        else if (cookieToken.equals(headerToken)) token = cookieToken;
        else throw new RataplanAuthException("Different tokens provided by cookie and header.");
        if (!jwtTokenService.isTokenValid(token)) {
            throw new InvalidTokenException("Invalid token");
        }
        return token;
    }

    private HttpHeaders createResponseHeaders(UserDTO user) {
        HttpHeaders responseHeaders = new HttpHeaders();

        String token = jwtTokenService.generateToken(user);
        responseHeaders.set(JWT_COOKIE_NAME, token);
        responseHeaders.add("Set-Cookie", cookieBuilder.generateCookieValue(token, false));

        return responseHeaders;
    }
}
