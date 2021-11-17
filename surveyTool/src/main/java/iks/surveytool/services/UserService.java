package iks.surveytool.services;

import iks.surveytool.components.Mapper;
import iks.surveytool.dtos.UserDTO;
import iks.surveytool.entities.User;
import iks.surveytool.repositories.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class UserService {
    private final UserRepository userRepository;
    private final Mapper mapper;

    public Long saveUser(User newUser) {
        User savedUser = userRepository.save(newUser);
        return savedUser.getId();
    }

    public Optional<User> findUserById(Long id) {
        return userRepository.findById(id);
    }

    public List<User> findParticipatingUsersBySurveyId(Long surveyId) {
        return userRepository.findParticipatingUsersBySurveyId(surveyId);
    }

    public List<UserDTO> createParticipatingUserDtosBySurveyId(Long surveyId) {
        List<User> users = findParticipatingUsersBySurveyId(surveyId);
        return mapper.toParticipatingUserDtos(users);
    }

    public UserDTO createUserDtoById(Long userId) {
        Optional<User> userOptional = findUserById(userId);
        if (userOptional.isPresent()) {
            User user = userOptional.get();
            return mapper.toUserDto(user);
        }
        return null;
    }
}
