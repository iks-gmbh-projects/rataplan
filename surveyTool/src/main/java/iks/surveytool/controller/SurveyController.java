package iks.surveytool.controller;

import iks.surveytool.domain.AuthUser;
import iks.surveytool.dtos.CompleteSurveyDTO;
import iks.surveytool.dtos.SurveyOverviewDTO;
import iks.surveytool.entities.InvalidEntityException;
import iks.surveytool.dtos.DTOValidationException;
import iks.surveytool.services.AuthService;
import iks.surveytool.services.SurveyService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Controller
@RequestMapping("/surveys")
@RequiredArgsConstructor
public class SurveyController {

    private final SurveyService surveyService;

    private final AuthService authService;

    @PostMapping
    public ResponseEntity<SurveyOverviewDTO> createSurvey(
        @RequestBody CompleteSurveyDTO surveyDTO,
        @AuthenticationPrincipal Jwt jwttoken
    ) throws InvalidEntityException, DTOValidationException
    {
        if (jwttoken == null) surveyDTO.setUserId(null);
        else {
            final AuthUser user = authService.getUserData(jwttoken);
            if (user == null) return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
            surveyDTO.setUserId(user.getId());
        }
        surveyDTO.valid();
        return surveyService.processSurveyDTO(surveyDTO);
    }

    @PutMapping(params = {"accessId"})
    public ResponseEntity<SurveyOverviewDTO> editSurvey(
        @RequestParam String accessId,
        @RequestBody CompleteSurveyDTO surveyDTO,
        @AuthenticationPrincipal Jwt jwttoken
    ) throws InvalidEntityException, DTOValidationException
    {
        surveyDTO.valid();
        return surveyService.processEditSurveyByAccessId(accessId, surveyDTO, jwttoken);
    }

    @GetMapping(params = {"accessId"})
    public ResponseEntity<SurveyOverviewDTO> findSurveyByAccessId(
        @RequestParam String accessId,
        @AuthenticationPrincipal Jwt jwttoken
    ) {
        return surveyService.processSurveyByAccessId(accessId, jwttoken);
    }

    @GetMapping(params = {"participationId"})
    public ResponseEntity<SurveyOverviewDTO> findSurveyByParticipationId(@RequestParam String participationId) {
        return surveyService.processSurveyByParticipationId(participationId);
    }

    @GetMapping
    public ResponseEntity<List<SurveyOverviewDTO>> findOpenAccessSurveys() {
        return surveyService.processOpenAccessSurveys();
    }

    @GetMapping("/own")
    public ResponseEntity<List<SurveyOverviewDTO>> findMySurveys(
        @AuthenticationPrincipal Jwt jwtToken
    ) {
        return surveyService.processMySurveys(jwtToken);
    }
}