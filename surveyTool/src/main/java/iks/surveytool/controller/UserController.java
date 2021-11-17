package iks.surveytool.controller;

import iks.surveytool.dtos.UserDTO;
import iks.surveytool.entities.User;
import iks.surveytool.services.UserService;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@Controller
@CrossOrigin(origins = "*")
public class UserController {

    private final UserService userService;

    public UserController(UserService userService) {
        this.userService = userService;
    }

    @GetMapping("/getParticipatingUsersBySurveyId")
    public ResponseEntity<List<UserDTO>> getParticipatingUsersBySurveyId(@RequestParam Long surveyId) {
        List<UserDTO> users = userService.createParticipatingUserDtosBySurveyId(surveyId);
        return ResponseEntity.ok(users);
    }

    @PostMapping("/postUser")
    public ResponseEntity<User> postUser(@RequestBody User newUser) {
        Long id = userService.saveUser(newUser);
        Optional<User> userOptional = userService.findUserById(id);
        if (userOptional.isPresent()) {
            return ResponseEntity.ok(userOptional.get());
        }
        return null;
    }
}
