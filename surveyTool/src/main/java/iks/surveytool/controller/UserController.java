package iks.surveytool.controller;

import iks.surveytool.dtos.UserDTO;
import iks.surveytool.entities.User;
import iks.surveytool.services.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Controller
@RequestMapping("/users")
@CrossOrigin(origins = "*")
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;

    @PostMapping
    public ResponseEntity<UserDTO> createUser(@RequestBody UserDTO userDTO) {
        User newUser = userService.createUserFromDto(userDTO);
        if (userService.validate(newUser)) {
            User savedUser = userService.saveUser(newUser);
            UserDTO savedUserDTO = userService.createUserDtoFromUser(savedUser);
            return ResponseEntity.ok(savedUserDTO);
        } else {
            return ResponseEntity.status(HttpStatus.UNPROCESSABLE_ENTITY).build();
        }
    }

    @GetMapping("/surveys/{surveyId}")
    public ResponseEntity<List<UserDTO>> findParticipatingUsersBySurveyId(@PathVariable Long surveyId) {
        List<UserDTO> users = userService.createParticipatingUserDtosBySurveyId(surveyId);
        return ResponseEntity.ok(users);
    }
}
