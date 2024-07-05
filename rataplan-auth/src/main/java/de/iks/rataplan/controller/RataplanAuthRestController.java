package de.iks.rataplan.controller;

import de.iks.rataplan.domain.*;
import de.iks.rataplan.dto.FeedbackDTO;
import de.iks.rataplan.dto.UserDTO;
import de.iks.rataplan.exceptions.RataplanAuthException;
import de.iks.rataplan.exceptions.UserDeletionException;
import de.iks.rataplan.service.FeedbackService;
import de.iks.rataplan.service.JwtTokenService;
import de.iks.rataplan.service.MailService;
import de.iks.rataplan.service.UserService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.ResourceAccessException;

import java.nio.file.AccessDeniedException;
import java.util.List;
import java.util.Objects;

@RequiredArgsConstructor
@RestController
@RequestMapping("/v1")
@Slf4j
public class RataplanAuthRestController {
    
    private final UserService userService;
    
    private final MailService mailService;
    
    private final JwtTokenService jwtTokenService;
    
    private final FeedbackService feedbackService;
    
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
    public ResponseEntity<Boolean> confirmAccount(@AuthenticationPrincipal Jwt token) {
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
        @RequestParam(value = "q", required = false) String query
    )
    {
        return ResponseEntity.ok(userService.searchUsers(query));
    }
    
    @DeleteMapping("/users/profile")
    public ResponseEntity<?> deleteUserData(
        @AuthenticationPrincipal Jwt token,
        @RequestBody DeleteUserRequest request
    )
    {
        String username = token.getSubject();
        User dbUser = userService.getUserFromUsername(username);
        userService.verifyPasswordOrThrow(dbUser, request.getPassword());
        try {
            userService.deleteUser(dbUser, request);
        } catch(UserDeletionException | ResourceAccessException ex) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(ex.getMessage());
        }
        
        return ResponseEntity.status(HttpStatus.OK).build();
    }
    
    @GetMapping("/users/profile")
    public ResponseEntity<UserDTO> getUserData(
        @AuthenticationPrincipal Jwt token
    )
    {
        String username = token.getSubject();
        UserDTO userDTO = userService.getUserDTOFromUsername(username);
        return ResponseEntity.ok(userDTO);
    }
    
    @PostMapping(
        value = "/users/profile/updateProfileDetails",
        consumes = MediaType.APPLICATION_JSON_VALUE,
        produces = MediaType.APPLICATION_JSON_VALUE
    )
    public ResponseEntity<Boolean> updateProfileDetails(
        @AuthenticationPrincipal Jwt token,
        @RequestBody UserDTO userDTO
    )
    {
        userDTO.setUsername(token.getSubject());
        if(userService.checkIfUsernameExists(userDTO.getUsername())) {
            boolean success = userService.updateProfileDetails(userDTO);
            return new ResponseEntity<>(success, HttpStatus.OK);
        } else {
            return new ResponseEntity<>(false, HttpStatus.UNAUTHORIZED);
        }
    }
    
    @PostMapping("/users/profile/changePassword")
    public ResponseEntity<Boolean> changePassword(
        @AuthenticationPrincipal Jwt token,
        @RequestBody PasswordChange passwords
    )
    {
        String username = token.getSubject();
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
    public ResponseEntity<Boolean> resetPassword(
        @AuthenticationPrincipal Jwt token,
        @RequestBody String password
    ) throws AccessDeniedException
    {
        User user = userService.getUserFromEmail(token.getSubject());
        if(user == null) throw new NullPointerException("Benutzer nicht gefunden");
        if(Objects.requireNonNull(token.getIssuedAt()).isAfter(user.getLastUpdated().toInstant())) {
            boolean success = this.userService.changePasswordByToken(user, password);
            return new ResponseEntity<>(success, HttpStatus.OK);
        } else throw new AccessDeniedException("Token Expired");
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