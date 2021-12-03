package iks.surveytool.services;

import iks.surveytool.dtos.UserDTO;
import iks.surveytool.entities.User;
import iks.surveytool.repositories.UserRepository;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.modelmapper.TypeToken;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.lang.reflect.Type;
import java.util.List;

@Service
@RequiredArgsConstructor
public class UserService {
    private final UserRepository userRepository;
    private final ModelMapper modelMapper;

    public ResponseEntity<UserDTO> processUserDTO(UserDTO userDTO) {
        User newUser = modelMapper.map(userDTO, User.class);
        if (newUser.validate()) {
            User savedUser = saveUser(newUser);
            UserDTO savedUserDTO = modelMapper.map(savedUser, UserDTO.class);
            return ResponseEntity.ok(savedUserDTO);
        } else {
            return ResponseEntity.status(HttpStatus.UNPROCESSABLE_ENTITY).build();
        }
    }

    private User saveUser(User newUser) {
        return userRepository.save(newUser);
    }

    public ResponseEntity<List<UserDTO>> processParticipatingUsersBySurveyId(Long surveyId) {
        List<UserDTO> users = mapParticipatingUsersToDTOBySurveyId(surveyId);
        return ResponseEntity.ok(users);
    }

    private List<UserDTO> mapParticipatingUsersToDTOBySurveyId(Long surveyId) {
        List<User> users = findParticipatingUsersBySurveyId(surveyId);
        Type userDTOList = new TypeToken<List<UserDTO>>() {
        }.getType();
        return modelMapper.map(users, userDTOList);
    }

    private List<User> findParticipatingUsersBySurveyId(Long surveyId) {
        return userRepository.findParticipatingUsersBySurveyId(surveyId);
    }
}
