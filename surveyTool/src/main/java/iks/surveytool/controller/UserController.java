package iks.surveytool.controller;

import iks.surveytool.dtos.UserDTO;
import iks.surveytool.services.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Controller
@RequestMapping("/users")
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;

    @PostMapping
    public ResponseEntity<UserDTO> createUser(@RequestBody UserDTO userDTO) {
        return userService.processUserDTO(userDTO);
    }

    @GetMapping("/surveys/{surveyId}")
    public ResponseEntity<List<UserDTO>> findParticipatingUsersBySurveyId(@PathVariable Long surveyId) {
        return userService.processParticipatingUsersBySurveyId(surveyId);
    }
}
