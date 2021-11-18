package iks.surveytool.controller;

import iks.surveytool.dtos.UserDTO;
import iks.surveytool.entities.User;
import iks.surveytool.services.UserService;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import java.util.List;

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

    @GetMapping("/users")
    public ResponseEntity<UserDTO> getUserById(@RequestParam Long id) {
        UserDTO userDTO = userService.createUserDtoById(id);
        return ResponseEntity.ok(userDTO);
    }

    @PostMapping("/postUser")
    public ResponseEntity<UserDTO> postUser(@RequestBody UserDTO userDTO) {
        User newUser = userService.createUserFromDto(userDTO);
        User savedUser = userService.saveUser(newUser);
        UserDTO savedUserDTO = userService.createUserDtoFromUser(savedUser);
        return ResponseEntity.ok(savedUserDTO);
    }
}
