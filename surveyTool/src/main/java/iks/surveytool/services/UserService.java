package iks.surveytool.services;

import iks.surveytool.components.Mapper;
import iks.surveytool.dtos.UserDTO;
import iks.surveytool.entities.User;
import iks.surveytool.repositories.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class UserService {
    private final UserRepository userRepository;
    private final Mapper mapper;

    public User saveUser(User newUser) {
        return userRepository.save(newUser);
    }

    public List<User> findParticipatingUsersBySurveyId(Long surveyId) {
        return userRepository.findParticipatingUsersBySurveyId(surveyId);
    }

    public List<UserDTO> createParticipatingUserDtosBySurveyId(Long surveyId) {
        List<User> users = findParticipatingUsersBySurveyId(surveyId);
        return mapper.toParticipatingUserDtoList(users);
    }

    public UserDTO createUserDtoFromUser(User user) {
        return mapper.toUserDto(user);
    }

    public User createUserFromDto(UserDTO userDTO) {
        return mapper.createUserFromDto(userDTO);
    }

    public boolean validate(User newUser) {
        String name = newUser.getName();
        if (name == null || name.length() > 255) {
            return false;
        } else {
            return true;
        }
    }
}
