package iks.surveytool.controller;

import iks.surveytool.domain.AuthUser;
import iks.surveytool.dtos.SurveyResponseDTO;
import iks.surveytool.services.AuthService;
import iks.surveytool.services.SurveyResponseService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Controller
@RequestMapping("/responses")
@RequiredArgsConstructor
public class SurveyResponseController {
    private final SurveyResponseService responseService;
    private final AuthService authService;

    @PostMapping
    public ResponseEntity<SurveyResponseDTO> addAnswers(
            @RequestBody SurveyResponseDTO surveyResponseDTO,
            @CookieValue(name = AuthService.JWT_COOKIE_NAME, required = false) String jwttoken
    ) {
        if(jwttoken == null) surveyResponseDTO.setUserId(null);
        else {
            final ResponseEntity<AuthUser> userEntity = authService.getUserData(jwttoken);
            if(!userEntity.getStatusCode().is2xxSuccessful()) {
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
            }
            final AuthUser user = userEntity.getBody();
            if(user == null) return ResponseEntity.internalServerError().build();
            surveyResponseDTO.setUserId(user.getId());
        }
        return responseService.processSurveyResponseDTOs(surveyResponseDTO);
    }

    @GetMapping("/survey/{accessId}")
    public ResponseEntity<List<SurveyResponseDTO>> findAnswersBySurveyId(
            @PathVariable String accessId,
            @CookieValue(name = AuthService.JWT_COOKIE_NAME, required = false) String jwtToken
    ) {
        return responseService.processSurveyResponseDTOs(accessId, jwtToken);
    }
}

