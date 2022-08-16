package de.iks.rataplan.controller;

import de.iks.rataplan.domain.AuthToken;
import de.iks.rataplan.domain.ResetPasswordData;
import de.iks.rataplan.service.AuthTokenService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import de.iks.rataplan.domain.PasswordChange;
import de.iks.rataplan.domain.User;
import de.iks.rataplan.exceptions.InvalidTokenException;
import de.iks.rataplan.service.JwtTokenService;
import de.iks.rataplan.service.UserService;

@RequiredArgsConstructor
@RestController
@RequestMapping("/v1")
public class RataplanAuthRestController {


    private final UserService userService;


    private final AuthTokenService authTokenService;


    private final JwtTokenService jwtTokenService;

    private static final String JWT_COOKIE_NAME = "jwttoken";

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

    @RequestMapping(value = "/users/profile", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
    public ResponseEntity<User> getUserData(@RequestHeader(value = JWT_COOKIE_NAME) String token) {
        if (!jwtTokenService.isTokenValid(token)) {
            throw new InvalidTokenException("Invalid token");
        }
        String username = jwtTokenService.getUsernameFromToken(token);
        User dbUser = userService.getUserData(username);

        HttpHeaders responseHeaders = createResponseHeaders(dbUser);
        return new ResponseEntity<>(dbUser, responseHeaders, HttpStatus.OK);
    }

    @RequestMapping(value = "/users/profile/changePassword", method = RequestMethod.POST, consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<Boolean> changePassword(@RequestHeader(value = JWT_COOKIE_NAME, required = true) String token,
                                                  @RequestBody PasswordChange passwords) {

        if (!jwtTokenService.isTokenValid(token)) {
            throw new InvalidTokenException("Invalid token");
        }
        String username = jwtTokenService.getUsernameFromToken(token);
        boolean success = this.userService.changePassword(username, passwords);
        return new ResponseEntity<>(success, HttpStatus.OK);
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
        return new ResponseEntity<>(false, HttpStatus.OK);
    }

    @RequestMapping(value = "/users/saveAuthToken", method = RequestMethod.POST)
    public AuthToken createAuthToken(@RequestBody String mail) {

        AuthToken response = authTokenService.saveAuthTokenToUserWithMail(mail);

        return response;
    }

    private HttpHeaders createResponseHeaders(User user) {
        HttpHeaders responseHeaders = new HttpHeaders();

        String token = jwtTokenService.generateToken(user);
        responseHeaders.set("jwttoken", token);

        return responseHeaders;
    }

}
