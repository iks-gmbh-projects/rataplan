package iks.surveytool.services;

import iks.surveytool.entities.User;
import iks.surveytool.repositories.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
@RequiredArgsConstructor
public class UserService {
    private final UserRepository userRepository;

    public Long saveUser(User newUser) {
        User savedUser = userRepository.save(newUser);
        return savedUser.getId();
    }

    public Optional<User> findUserById(Long id) {
        return userRepository.findById(id);
    }
}
