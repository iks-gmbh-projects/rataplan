package iks.surveytool.controller;

import iks.surveytool.dtos.CompleteSurveyDTO;
import iks.surveytool.dtos.DTOValidationException;
import iks.surveytool.dtos.SurveyOverviewDTO;
import iks.surveytool.entities.InvalidEntityException;
import iks.surveytool.services.SurveyService;
import lombok.RequiredArgsConstructor;

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
    
    @PostMapping
    public ResponseEntity<SurveyOverviewDTO> createSurvey(
        @RequestBody CompleteSurveyDTO surveyDTO,
        @AuthenticationPrincipal Jwt jwttoken
    ) throws InvalidEntityException, DTOValidationException
    {
        surveyDTO.valid();
        return surveyService.createSurvey(surveyDTO, jwttoken);
    }

    @PutMapping(params = {"accessId"})
    public ResponseEntity<SurveyOverviewDTO> editSurvey(
        @RequestParam String accessId,
        @RequestBody CompleteSurveyDTO surveyDTO,
        @AuthenticationPrincipal Jwt jwttoken
    ) throws InvalidEntityException, DTOValidationException
    {
        surveyDTO.valid();
        return surveyService.editSurvey(accessId, surveyDTO, jwttoken);
    }

    @GetMapping(params = {"accessId"})
    public ResponseEntity<? extends SurveyOverviewDTO> findSurveyByAccessId(
        @RequestParam String accessId,
        @AuthenticationPrincipal Jwt jwttoken
    ) {
        return surveyService.getSurveyByAccessId(accessId, jwttoken);
    }

    @GetMapping(params = {"participationId"})
    public ResponseEntity<? extends SurveyOverviewDTO> findSurveyByParticipationId(@RequestParam String participationId) {
        return surveyService.getSurveyByParticipationId(participationId);
    }

    @GetMapping
    public ResponseEntity<List<SurveyOverviewDTO>> findOpenAccessSurveys() {
        return surveyService.getCurrentOpenSurveys();
    }

    @GetMapping("/own")
    public ResponseEntity<List<SurveyOverviewDTO>> findMySurveys(
        @AuthenticationPrincipal Jwt jwtToken
    ) {
        return surveyService.getUserSurveys(jwtToken);
    }
}