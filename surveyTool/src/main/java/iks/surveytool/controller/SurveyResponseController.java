package iks.surveytool.controller;

import iks.surveytool.domain.AuthUser;
import iks.surveytool.dtos.SurveyResponseDTO;
import iks.surveytool.services.AuthService;
import iks.surveytool.services.SurveyResponseService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.jwt.Jwt;
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
        @AuthenticationPrincipal Jwt jwttoken
    ) {
        if (jwttoken == null) surveyResponseDTO.setUserId(null);
        else {
            final AuthUser user = authService.getUserData(jwttoken);
            if (user == null) return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
            surveyResponseDTO.setUserId(user.getId());
        }
        surveyResponseDTO.trimAndNull();
        if(!surveyResponseDTO.valid()) return ResponseEntity.badRequest().build();
        return responseService.processSurveyResponseDTOs(surveyResponseDTO);
    }

    @GetMapping("/survey/{accessId}")
    public ResponseEntity<List<SurveyResponseDTO>> findAnswersBySurveyId(
        @PathVariable String accessId,
        @AuthenticationPrincipal Jwt jwtToken
    ) {
        return responseService.processSurveyResponseDTOs(accessId, jwtToken);
    }
}