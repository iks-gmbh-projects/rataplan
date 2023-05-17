package de.iks.rataplan.service;

import de.iks.rataplan.domain.DeleteUserRequest;
import de.iks.rataplan.dto.UserDTO;
import de.iks.rataplan.exceptions.*;
import de.iks.rataplan.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataAccessException;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import de.iks.rataplan.domain.PasswordChange;
import de.iks.rataplan.domain.User;

@Service
@Transactional
public class UserServiceImpl implements UserService {


    @Autowired
    private UserRepository userRepository;

    @Autowired
    private BCryptPasswordEncoder passwordEncoder;

    @Autowired
    private SurveyToolMessageService surveyToolMessageService;

    @Autowired
    CryptoServiceImpl cryptoService;

    @Autowired
    private BackendMessageService backendMessageService;

    public UserDTO registerUser(UserDTO userDTO) {
        if (userDTO.getMail().trim().isEmpty() || userDTO.getUsername().trim().isEmpty())
            throw new InvalidUserDataException();
        User user = mapToUser(userDTO);

        if (user.invalidFull()) throw new InvalidUserDataException();
        if (checkIfUsernameExists(userDTO.getUsername())) {
            throw new UsernameAlreadyInUseException("The username \"" + user.getUsername() + "\" is already in use!");
        } else if (checkIfMailExists(userDTO.getMail())) {
            throw new MailAlreadyInUseException("The email \"" + user.getMail() + "\" is already in use!");
        } else {
            user.setPassword(passwordEncoder.encode(user.getPassword()));
            user.setId(null);
            return mapToUserDTO(userRepository.saveAndFlush(user));
        }
    }


    @Override
    public boolean checkIfMailExists(String mail) {
        return this.userRepository.findOneByMailAndEncrypted(cryptoService.encryptDB(mail.toLowerCase().trim()), true).isPresent()
                || this.userRepository.findOneByMailAndEncrypted(mail.toLowerCase().trim(), false).isPresent();
    }

    @Override
    public boolean checkIfUsernameExists(String username) {
        return this.userRepository.findOneByUsernameAndEncrypted(cryptoService.encryptDB(username.toLowerCase().trim()), true).isPresent()
                || this.userRepository.findOneByUsernameAndEncrypted(username.toLowerCase().trim(), false).isPresent();
    }


    @Override
    public UserDTO loginUser(UserDTO userDTO) {
        if (userDTO.invalidLogin()) throw new InvalidUserDataException();

        User dbUser;
        if (userDTO.getUsername() != null) {
            dbUser = getUserFromUsername(userDTO.getUsername());
        } else {
            dbUser = getUserFromEmail(userDTO.getMail());
        }

        if (dbUser != null && passwordEncoder.matches(userDTO.getPassword(), dbUser.getPassword()))
            return dbUser.isEncrypted() ? mapToUserDTO(dbUser) : new UserDTO(dbUser);
        else throw new WrongCredentialsException("These credentials have no match!");

    }

    private User mapToUser(UserDTO userDTO) {
        userDTO.trimUserCredentials();
        User user = new User();
        if (userDTO.getUsername() != null)
            user.setUsername(cryptoService.encryptDB(userDTO.getUsername().toLowerCase()));
        if (userDTO.getMail() != null)
            user.setMail(cryptoService.encryptDB(userDTO.getMail().toLowerCase()));
        if (userDTO.getDisplayname() != null)
            user.setDisplayname(cryptoService.encryptDB(userDTO.getDisplayname().toLowerCase()));
        user.setPassword(userDTO.getPassword());
        user.setEncrypted(true);
        return user;
    }

    public UserDTO mapToUserDTO(User user) {
        UserDTO userDTO = new UserDTO();
        userDTO.setUsername(cryptoService.decryptDB(user.getUsername()));
        userDTO.setMail(cryptoService.decryptDB(user.getMail()));
        userDTO.setId(user.getId());
        userDTO.setDisplayname(cryptoService.decryptDB(user.getDisplayname()));
        return userDTO;
    }

    @Override
    public boolean verifyPassword(User user, String password) {
        return passwordEncoder.matches(password, user.getPassword());
    }

    @Override
    public void verifyPasswordOrThrow(User user, String password) throws WrongCredentialsException {
        if (!verifyPassword(user, password)) {
            throw new WrongCredentialsException("These credentials have no match!");
        }
    }

    @Override
    public UserDTO getUserDTOFromUsername(String username) {
        User dbUser;
        if (username != null) {
            dbUser = this.userRepository.findOneByUsernameAndEncrypted(cryptoService.encryptDB(username.trim().toLowerCase()), true)
                    .orElseGet(() -> this.userRepository.findOneByUsernameAndEncrypted(username.trim().toLowerCase(), false)
                            .orElse(null));
            if (dbUser != null) return dbUser.isEncrypted() ? mapToUserDTO(dbUser) : new UserDTO(dbUser);
        }
        throw new InvalidTokenException("Token is not allowed to get data.");
    }

    public User getUserFromEmail(String mail) {
        return this.userRepository.findOneByMailAndEncrypted(cryptoService.encryptDB(mail.trim().toLowerCase()), true)
                .orElseGet(() -> this.userRepository.findOneByMailAndEncrypted(mail.trim().toLowerCase(), false)
                        .orElse(null));
    }

    @Override
    public User getUserFromUsername(String username) {
        User dbUser;
        if (username != null) {
            dbUser = this.userRepository.findOneByUsernameAndEncrypted(cryptoService.encryptDB(username.trim().toLowerCase()), true)
                    .orElseGet(() -> userRepository.findOneByUsernameAndEncrypted(username.toLowerCase().trim(), false)
                            .orElseGet(() -> null));
            return dbUser;
        }
        throw new InvalidTokenException("Token is not allowed to get data.");
    }

    @Override
    public Boolean changePassword(String username, PasswordChange passwords) {
        User user = this.getUserFromUsername(username);
        if (user != null && passwordEncoder.matches(passwords.getOldPassword(), user.getPassword())) {
            user.setPassword(passwordEncoder.encode(passwords.getNewPassword()));
            userRepository.saveAndFlush(user);
            return true;
        } else {
            return false;
        }
    }

    @Override
    public Boolean updateProfileDetails(UserDTO userDTO) {
        User user = getUserFromUsername(userDTO.getUsername());
        if (user != null) {
            if (!checkIfMailExists(userDTO.getMail()) || user.getId().equals(getUserFromEmail(userDTO.getMail()).getId())) {
                user.setMail(cryptoService.encryptDB((userDTO.getMail())).toLowerCase().trim());
                user.setDisplayname(cryptoService.encryptDB(userDTO.getDisplayname()));
                userRepository.saveAndFlush(user);
                return true;
            }
        }
        return false;
    }

    @Override
    public Boolean changePasswordByToken(User user, String password) {
        if (user != null) {
            user.setPassword(passwordEncoder.encode(password));
            this.userRepository.saveAndFlush(user);
            return true;
        } else {
            return false;
        }
    }

    @Override
    public User getUserFromId(int id) {
        return userRepository.findOne(id);
    }

    @Override
    public String getDisplayNameFromId(int id){
        User user = getUserFromId(id);
        String displayName = null;
        if (user != null) {
            displayName = user.isEncrypted() ? cryptoService.decryptDB(user.getDisplayname()) : user.getDisplayname();
        }
        return displayName;
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
        ResponseEntity<?> backendResponse;
        switch (request.getBackendChoice()) {
            default:
            case DELETE:
                backendResponse = backendMessageService.deleteUserData(user.getId());
                break;
            case ANONYMIZE:
                backendResponse = backendMessageService.anonymizeUserData(user.getId());
                break;
        }
        if (!backendResponse.getStatusCode().is2xxSuccessful()) {
            throw new UserDeletionException(
                    "Backend returned " +
                            backendResponse.getStatusCode() +
                            " " +
                            (backendResponse.hasBody() ?
                                    backendResponse.getBody() :
                                    ""
                            )
            );
        }
        try {
            this.userRepository.delete(user.getId());
        } catch (DataAccessException ex) {
            throw new UserDeletionException(ex);
        }
    }
}
