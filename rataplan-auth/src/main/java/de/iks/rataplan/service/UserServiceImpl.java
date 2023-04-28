package de.iks.rataplan.service;
import de.iks.rataplan.domain.DeleteUserRequest;
import de.iks.rataplan.dto.UserDTO;
import de.iks.rataplan.exceptions.*;
import de.iks.rataplan.repository.RawUserRepository;
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
    private RawUserRepository rawUserRepository;

    @Autowired
    private BCryptPasswordEncoder passwordEncoder;

    @Autowired
    private SurveyToolMessageService surveyToolMessageService;
    @Autowired
    CryptoServiceImpl cryptoService;

    @Autowired
    private BackendMessageService backendMessageService;

    public UserDTO registerUser(UserDTO userDto) {
        User user = mapToUser(userDto);
        user.trimUserCredentials();
        if (user.invalidFull()) throw new InvalidUserDataException();
        if (rawUserRepository.findOneByUsername(user.getUsername()).isPresent() || rawUserRepository.findOneByUsername(userDto.getUsername().trim().toLowerCase()).isPresent()) {
            throw new UsernameAlreadyInUseException("The username \"" + user.getUsername() + "\" is already in use!");
        } else if (rawUserRepository.findOneByMail(user.getMail()).isPresent() || rawUserRepository.findOneByMail(userDto.getMail().toLowerCase().trim()).isPresent()) {
            throw new MailAlreadyInUseException("The email \"" + user.getMail() + "\" is already in use!");
        } else {
            user.setPassword(passwordEncoder.encode(user.getPassword()));
            user.setId(null);
            return mapToUserDTO(rawUserRepository.saveAndFlush(user));
        }
    }


    @Override
    public boolean checkIfMailExists(String mail) {
        return this.rawUserRepository.findOneByMail(cryptoService.encryptDB(mail.toLowerCase().trim())).isPresent()
                || (this.rawUserRepository.findOneByMail(mail.toLowerCase().trim())).isPresent();
    }

    @Override
    public boolean checkIfUsernameExists(String username) {
        return this.rawUserRepository.findOneByUsername(cryptoService.encryptDB(username.toLowerCase().trim())).isPresent()
                || this.rawUserRepository.findOneByUsername(username.toLowerCase().trim()).isPresent();
    }


    @Override
    public UserDTO loginUser(UserDTO userDTO) {
        User user = mapToUser(userDTO);
        user.trimUserCredentials();
        if (user.invalidLogin()) throw new InvalidUserDataException();

        User dbUser = null;
        if (user.getUsername() != null) {
            dbUser = this.rawUserRepository.findOneByUsername(user.getUsername())
                    .orElse(this.rawUserRepository.findOneByUsername(userDTO.getUsername())
                            .orElse(null));
        } else if (user.getMail() != null){
            dbUser = this.rawUserRepository.findOneByMail(user.getMail())
                    .orElse(this.rawUserRepository.findOneByMail(userDTO.getMail())
                            .orElse(null));
        }

        if (dbUser != null && passwordEncoder.matches(user.getPassword(), dbUser.getPassword())) return dbUser.isEncrypted() ?  mapToUserDTO(user) : new UserDTO(user);
        else throw new WrongCredentialsException("These credentials have no match!");

    }

    private User mapToUser(UserDTO userDTO) {
        User user = new User();
        if (userDTO.getUsername() != null) user.setUsername(cryptoService.encryptDB(userDTO.getUsername().toLowerCase().trim()));
        if (userDTO.getMail() != null) user.setMail(cryptoService.encryptDB(userDTO.getMail().toLowerCase().trim()));
        user.setPassword(userDTO.getPassword());
        if (userDTO.getDisplayname() != null) user.setDisplayname(cryptoService.encryptDB(userDTO.getDisplayname().trim()));
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
            dbUser = this.rawUserRepository.findOneByUsername(cryptoService.encryptDB(username.trim().toLowerCase()))
                    .orElse(this.rawUserRepository.findOneByUsername(username)
                            .orElse(null));
            if (dbUser != null) return dbUser.isEncrypted() ? mapToUserDTO(dbUser) : new UserDTO(dbUser);
        }
        throw new InvalidTokenException("Token is not allowed to get data.");
    }

    @Override
    public User getUserData(String username) {
        User dbUser;
        if (username != null) {
            dbUser = this.rawUserRepository.findOneByUsername(cryptoService.encryptDB(username.trim().toLowerCase()))
                    .orElse(rawUserRepository.findOneByUsername(username.toLowerCase().trim())
                            .orElse(null));
            return dbUser;
        }
        throw new InvalidTokenException("Token is not allowed to get data.");
    }

    @Override
    public Boolean changePassword(String username, PasswordChange passwords) {
        User user = this.getUserData(username);
        if (user != null && passwordEncoder.matches(passwords.getOldPassword(), user.getPassword())) {
            user.setPassword(passwordEncoder.encode(passwords.getNewPassword()));
            rawUserRepository.saveAndFlush(user);
            return true;
        } else {
            return false;
        }
    }

    @Override
    public Boolean updateProfileDetails(UserDTO userDTO) {
        User user = getUserData(userDTO.getUsername());
        user.setMail(cryptoService.encryptDB((userDTO.getMail())));
        user.setDisplayname(cryptoService.encryptDB(userDTO.getDisplayname()));
        rawUserRepository.saveAndFlush(user);
        return true;
    }

    @Override
    public Boolean changePasswordByToken(User user, String password) {
        if (user != null) {
            user.setPassword(passwordEncoder.encode(password));
            this.rawUserRepository.saveAndFlush(user);
            return true;
        } else {
            return false;
        }
    }

    @Override
    public User getUserFromId(int id) {
        return rawUserRepository.findOne(id);
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
            this.rawUserRepository.delete(user.getId());
        } catch (DataAccessException ex) {
            throw new UserDeletionException(ex);
        }
    }
}
