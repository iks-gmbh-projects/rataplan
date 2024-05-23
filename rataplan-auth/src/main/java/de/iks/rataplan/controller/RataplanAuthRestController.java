package de.iks.rataplan.controller;

import de.iks.rataplan.domain.*;
import de.iks.rataplan.dto.FeedbackDTO;
import de.iks.rataplan.dto.UserDTO;
import de.iks.rataplan.exceptions.RataplanAuthException;
import de.iks.rataplan.exceptions.UserDeletionException;
import de.iks.rataplan.service.*;
import de.iks.rataplan.utils.CookieBuilder;
import lombok.RequiredArgsConstructor;

import lombok.extern.slf4j.Slf4j;

import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import de.iks.rataplan.exceptions.InvalidTokenException;

import org.springframework.web.client.ResourceAccessException;

import java.util.List;

@RequiredArgsConstructor
@RestController
@RequestMapping("/v1")
@Slf4j
public class RataplanAuthRestController {
    
    private final UserService userService;
    
    private final MailService mailService;
    
    private final JwtTokenService jwtTokenService;
    
    private final FeedbackService feedbackService;
    
    public static final String JWT_COOKIE_NAME = "jwttoken";
    
    private final CookieBuilder cookieBuilder;
    
    @RequestMapping(value = "*", method = RequestMethod.OPTIONS)
    public ResponseEntity<?> handle() {
        return new ResponseEntity<>(HttpStatus.OK);
    }
    
    @PostMapping("/users/register")
    public ResponseEntity<?> registerUser(@RequestBody UserDTO user) {
        UserDTO userDTO = userService.registerUser(user);
        sendAccountConfirmationEmail(userDTO);
        return new ResponseEntity<>(HttpStatus.CREATED);
    }
    
    @PostMapping("/confirm-account")
    public ResponseEntity<Boolean> confirmAccount(@RequestBody String token) {
        Boolean accountConfirmed = userService.confirmAccount(token);
        return new ResponseEntity<>(accountConfirmed, HttpStatus.OK);
    }
    
    @PostMapping("/resend-confirmation-email")
    ResponseEntity<Boolean> resendConfirmationEmail(@RequestBody String email) {
        try {
            UserDTO userDTO = userService.validateResendConfirmationEmailRequest(email);
            if(userDTO == null) return new ResponseEntity<>(false, HttpStatus.FORBIDDEN);
            sendAccountConfirmationEmail(userDTO);
            return new ResponseEntity<>(true, HttpStatus.OK);
        } catch(NullPointerException e) {
            return new ResponseEntity<>(false, HttpStatus.FORBIDDEN);
        }
    }
    
    private void sendAccountConfirmationEmail(UserDTO userDTO) {
        String jwt = jwtTokenService.generateAccountConfirmationToken(userDTO);
        ConfirmAccountMailData confirmAccountMailData = new ConfirmAccountMailData(jwt, userDTO.getMail());
        mailService.sendAccountConfirmationEmail(confirmAccountMailData);
    }
    
    @PostMapping("/users/mailExists")
    public boolean checkIfMailExists(@RequestBody String mail) {
        return userService.checkIfMailExists(mail);
    }
    
    @PostMapping("/users/usernameExists")
    public boolean checkIfUsernameExists(@RequestBody String username) {
        return userService.checkIfUsernameExists(username);
    }
    
    @GetMapping("/users/search")
    public ResponseEntity<List<Integer>> searchUsers(
        @RequestHeader(value = JWT_COOKIE_NAME, required = false) String tokenHeader,
        @CookieValue(value = JWT_COOKIE_NAME, required = false) String tokenCookie,
        @RequestParam(value = "q", required = false) String query
    ) {
        validateTokenOrThrow(tokenCookie, tokenHeader);
        return ResponseEntity.ok(userService.searchUsers(query));
    }
    
    @PostMapping("/users/login")
    public ResponseEntity<UserDTO> loginUser(@RequestBody UserDTO user) {
        UserDTO userDTO = userService.loginUser(user);
        HttpHeaders responseHeaders = createResponseHeaders(userDTO);
        
        return new ResponseEntity<>(userDTO, responseHeaders, HttpStatus.OK);
    }
    
    @GetMapping("/users/logout")
    public ResponseEntity<Boolean> logoutUser(
        @CookieValue(value = JWT_COOKIE_NAME) String tokenCookie
    )
    {
        HttpHeaders responseHeaders = new HttpHeaders();
        responseHeaders.add("Set-Cookie", cookieBuilder.generateCookieValue(tokenCookie, true));
        
        return ResponseEntity.ok().headers(responseHeaders).body(true);
    }
    
    @DeleteMapping("/users/profile")
    public ResponseEntity<?> deleteUserData(
        @RequestHeader(value = JWT_COOKIE_NAME, required = false) String tokenHeader,
        @CookieValue(value = JWT_COOKIE_NAME, required = false) String tokenCookie,
        @RequestBody DeleteUserRequest request
    )
    {
        String token = validateTokenOrThrow(tokenCookie, tokenHeader);
        String username = jwtTokenService.getUsernameFromToken(token);
        User dbUser = userService.getUserFromUsername(username);
        userService.verifyPasswordOrThrow(dbUser, request.getPassword());
        try {
            userService.deleteUser(dbUser, request);
        } catch(UserDeletionException | ResourceAccessException ex) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(ex.getMessage());
        }
        
        return logoutUser(token);
    }
    
    @GetMapping("/users/profile")
    public ResponseEntity<UserDTO> getUserData(
        @RequestHeader(value = JWT_COOKIE_NAME, required = false) String tokenHeader,
        @CookieValue(value = JWT_COOKIE_NAME, required = false) String tokenCookie
    )
    {
        String token = validateTokenOrThrow(tokenCookie, tokenHeader, true);
        String username = jwtTokenService.getUsernameFromToken(token);
        UserDTO userDTO = userService.getUserDTOFromUsername(username);
        HttpHeaders responseHeaders = createResponseHeaders(userDTO);
        
        return new ResponseEntity<>(userDTO, responseHeaders, HttpStatus.OK);
    }
    
    @PostMapping(
        value = "/users/profile/updateProfileDetails",
        consumes = MediaType.APPLICATION_JSON_VALUE,
        produces = MediaType.APPLICATION_JSON_VALUE
    )
    public ResponseEntity<Boolean> updateProfileDetails(
        @RequestHeader(value = JWT_COOKIE_NAME, required = false) String tokenHeader,
        @CookieValue(value = JWT_COOKIE_NAME, required = false) String tokenCookie,
        @RequestBody UserDTO userDTO
    )
    {
        String token = validateTokenOrThrow(tokenCookie, tokenHeader);
        userDTO.setUsername(jwtTokenService.getUsernameFromToken((token)));
        if(userService.checkIfUsernameExists(userDTO.getUsername())) {
            boolean success = userService.updateProfileDetails(userDTO);
            return new ResponseEntity<>(success, HttpStatus.OK);
        } else {
            return new ResponseEntity<>(false, HttpStatus.UNAUTHORIZED);
        }
    }
    
    @PostMapping("/users/profile/changePassword")
    public ResponseEntity<Boolean> changePassword(
        @RequestHeader(value = JWT_COOKIE_NAME, required = false) String tokenHeader,
        @CookieValue(value = JWT_COOKIE_NAME, required = false) String tokenCookie,
        @RequestBody PasswordChange passwords
    )
    {
        String token = validateTokenOrThrow(tokenCookie, tokenHeader);
        String username = jwtTokenService.getUsernameFromToken(token);
        boolean success = this.userService.changePassword(username, passwords);
        
        return new ResponseEntity<>(success, HttpStatus.OK);
    }
    
    @PostMapping("/users/forgotPassword")
    public boolean sendForgotPasswordMail(@RequestBody String mail) {
        if(this.userService.getUserFromEmail(mail) == null) throw new NullPointerException("Email nicht gefunden");
        String token = this.jwtTokenService.generateResetPasswordToken(mail);
       
        ResetPasswordMailData resetPasswordMailData = new ResetPasswordMailData();
        resetPasswordMailData.setMail(mail);
        resetPasswordMailData.setToken(token);
       
        mailService.sendMailForResetPassword(resetPasswordMailData);
        return true;
    }
    
    @PostMapping("/users/resetPassword")
    public ResponseEntity<Boolean> resetPassword(@RequestBody ResetPasswordData resetPasswordData) {
        User user = this.jwtTokenService.getUserFromResetPasswordToken(resetPasswordData.getToken(), userService);
        if(user == null) throw new NullPointerException("Benutzer nicht gefunden");
        
        boolean success = this.userService.changePasswordByToken(user, resetPasswordData.getPassword());
        return new ResponseEntity<>(success, HttpStatus.OK);
    }
    
    private String validateTokenOrThrow(String cookieToken, String headerToken) throws RataplanAuthException {
        return validateTokenOrThrow(cookieToken, headerToken, false);
    }
    private String validateTokenOrThrow(String cookieToken, String headerToken, boolean allowReset) throws
        RataplanAuthException
    {
        String token;
        if(headerToken == null) token = cookieToken;
        else if(cookieToken == null) token = headerToken;
        else if(cookieToken.equals(headerToken)) token = cookieToken;
        else throw new RataplanAuthException("Different tokens provided by cookie and header.");
        if(!jwtTokenService.isTokenValid(token)) {
            throw new InvalidTokenException("Invalid token", allowReset && cookieToken != null);
        }
        return token;
    }
    
    private HttpHeaders createResponseHeaders(UserDTO user) {
        HttpHeaders responseHeaders = new HttpHeaders();
        
        String token = jwtTokenService.generateLoginToken(user);
        responseHeaders.set(JWT_COOKIE_NAME, token);
        responseHeaders.add("Set-Cookie", cookieBuilder.generateCookieValue(token, false));
        
        return responseHeaders;
    }
    
    @GetMapping("/users/displayName/{userId}")
    public ResponseEntity<?> getDisplayName(@PathVariable int userId) {
        String displayName = userService.getDisplayNameFromId(userId);
        if(displayName == null) return ResponseEntity.notFound().build();
        return ResponseEntity.ok(displayName);
    }
    
    @PostMapping("/feedback")
    public ResponseEntity<?> postFeedback(@RequestBody FeedbackDTO feedback) {
        try {
            log.info("incoming feedback: {}", feedback);
            feedbackService.acceptFeedback(feedback);
        } catch(RataplanAuthException ex) {
            return ResponseEntity.badRequest().body(ex.getMessage());
        }
        return ResponseEntity.ok(feedback);
    }
}