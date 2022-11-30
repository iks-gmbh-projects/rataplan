package de.iks.rataplan.service;

import de.iks.rataplan.domain.DeleteUserRequest;
import de.iks.rataplan.domain.EmailChange;
import de.iks.rataplan.exceptions.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataAccessException;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import de.iks.rataplan.domain.PasswordChange;
import de.iks.rataplan.domain.User;
import de.iks.rataplan.repository.UserRepository;

@Service
@Transactional
public class UserServiceImpl implements UserService {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private BCryptPasswordEncoder passwordEncoder;

    @Autowired
    private SurveyToolMessageService surveyToolMessageService;

    @Override
    public User registerUser(User user) {
        user.trimUserCredentials();
        if (userRepository.findOneByUsername(user.getUsername()) != null) {
            throw new UsernameAlreadyInUseException("The username \"" + user.getUsername() + "\" is already in use!");
        } else if (userRepository.findOneByMail(user.getMail()) != null) {
            throw new MailAlreadyInUseException("The email \"" + user.getMail() + "\" is already in use!");
        } else {
            user.setPassword(passwordEncoder.encode(user.getPassword()));
            user.setId(null);
            return userRepository.saveAndFlush(user);
        }
    }

    @Override
    public boolean checkIfMailExists(String mail) {

        return this.userRepository.existsByMail(mail);
    }

    @Override
    public boolean checkIfUsernameExists(String username) {

        return this.userRepository.existsByUsername(username);
    }


    @Override
    public User loginUser(User user) {
        user.trimUserCredentials();
        User dbUser;
        if (user.getUsername() != null) {
            dbUser = userRepository.findOneByUsername(user.getUsername());
        } else {
            dbUser = userRepository.findOneByMail(user.getMail());
        }

        if (dbUser != null && passwordEncoder.matches(user.getPassword(), dbUser.getPassword())) {
            return dbUser;
        } else {
            throw new WrongCredentialsException("These credentials have no match!");
        }
    }

    @Override
    public User getUserData(String username) {
        User dbUser;
        if (username != null) {
            dbUser = userRepository.findOneByUsername(username);
            return dbUser;
        }
        throw new InvalidTokenException("Token is not allowed to get data.");
    }

    @Override
    public Boolean changePassword(String username, PasswordChange passwords) {
        User user = this.getUserData(username);
        if (user != null && passwordEncoder.matches(passwords.getOldPassword(), user.getPassword())) {
            user.setPassword(passwordEncoder.encode(passwords.getNewPassword()));
            userRepository.saveAndFlush(user);
            return true;
        } else {
            return false;
        }
    }

    @Override
    public Boolean changeEmail(String username, String email) {
        User user = this.getUserData(username);
        if (user != null) {
            user.setMail(email);
            userRepository.saveAndFlush(user);
            return true;
        }
        return false;
    }

    @Override
    public Boolean changeDisplayName(String username, String displayName) {
        User user = this.getUserData(username);
        if (user != null) {
            user.setDisplayname(displayName);
            userRepository.saveAndFlush(user);
            return true;
        }
        return false;
    }

    @Override
    public Boolean changePasswordByToken(User user, String password) {
        if (user != null) {
            user.setPassword(passwordEncoder.encode(password));
            userRepository.saveAndFlush(user);
            return true;
        } else {
            return false;
        }
    }

    @Override
    public User getUserFromId(int id) {
        return userRepository.findById(id);
    }

    @Override
    public void deleteUser(User user, DeleteUserRequest request) throws UserDeletionException {
        ResponseEntity<?> surveyToolResponse;
        switch (request.getSurveyToolChoice()) {
        default:
        case DELETE:
            surveyToolResponse = surveyToolMessageService.deleteUserData(user.getId());
            break;
        case ANONYMIZE:
            surveyToolResponse = surveyToolMessageService.anonymizeUserData(user.getId());
            break;
        }
        if (!surveyToolResponse.getStatusCode().is2xxSuccessful()) {
            throw new UserDeletionException(
                "SurveyTool returned " +
                    surveyToolResponse.getStatusCode() +
                    " " +
                    (surveyToolResponse.hasBody() ?
                        surveyToolResponse.getBody() :
                        ""
                    )
            );
        }
        try {
            userRepository.delete(user);
        } catch(DataAccessException ex) {
            throw new UserDeletionException(ex);
        }
    }
}
