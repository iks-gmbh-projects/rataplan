package iks.surveytool.services;

import iks.surveytool.components.Mapper;
import iks.surveytool.dtos.UserDTO;
import iks.surveytool.entities.User;
import iks.surveytool.repositories.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class UserService {
    private final UserRepository userRepository;
    private final Mapper mapper;

    public ResponseEntity<UserDTO> processUserDTO(UserDTO userDTO) {
        User newUser = mapUserToEntity(userDTO);
        if (validate(newUser)) {
            User savedUser = saveUser(newUser);
            UserDTO savedUserDTO = mapUserToDTO(savedUser);
            return ResponseEntity.ok(savedUserDTO);
        } else {
            return ResponseEntity.status(HttpStatus.UNPROCESSABLE_ENTITY).build();
        }
    }

    private User mapUserToEntity(UserDTO userDTO) {
        return mapper.createUserFromDTO(userDTO);
    }

    private boolean validate(User newUser) {
        String name = newUser.getName();
        return name != null && name.length() <= 255;
    }

    private User saveUser(User newUser) {
        return userRepository.save(newUser);
    }

    private UserDTO mapUserToDTO(User user) {
        return mapper.toUserDTO(user);
    }

    public ResponseEntity<List<UserDTO>> processParticipatingUsersBySurveyId(Long surveyId) {
        List<UserDTO> users = mapParticipatingUsersToDTOBySurveyId(surveyId);
        return ResponseEntity.ok(users);
    }

    private List<UserDTO> mapParticipatingUsersToDTOBySurveyId(Long surveyId) {
        List<User> users = findParticipatingUsersBySurveyId(surveyId);
        return mapper.toParticipatingUserDTOList(users);
    }

    private List<User> findParticipatingUsersBySurveyId(Long surveyId) {
        return userRepository.findParticipatingUsersBySurveyId(surveyId);
    }
}
