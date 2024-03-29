package iks.surveytool.controller;

import iks.surveytool.domain.AuthUser;
import iks.surveytool.dtos.CompleteSurveyDTO;
import iks.surveytool.dtos.SurveyOverviewDTO;
import iks.surveytool.entities.InvalidEntityException;
import iks.surveytool.services.AuthService;
import iks.surveytool.services.SurveyService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
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
        @CookieValue(name = AuthService.JWT_COOKIE_NAME, required = false) String jwttoken
    ) throws InvalidEntityException {
        if (jwttoken == null) surveyDTO.setUserId(null);
        else {
            final AuthUser user = authService.getUserData(jwttoken);
            if (user == null) return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
            surveyDTO.setUserId(user.getId());
        }
        if(!surveyDTO.valid()) return ResponseEntity.badRequest().build();
        return surveyService.processSurveyDTO(surveyDTO);
    }

    @PutMapping(params = {"accessId"})
    public ResponseEntity<SurveyOverviewDTO> editSurvey(
        @RequestParam String accessId,
        @RequestBody CompleteSurveyDTO surveyDTO,
        @CookieValue(name = AuthService.JWT_COOKIE_NAME, required = false) String jwttoken
    ) throws InvalidEntityException {
        if(!surveyDTO.valid()) return ResponseEntity.badRequest().build();
        return surveyService.processEditSurveyByAccessId(accessId, surveyDTO, jwttoken);
    }

    @GetMapping(params = {"accessId"})
    public ResponseEntity<SurveyOverviewDTO> findSurveyByAccessId(
        @RequestParam String accessId,
        @CookieValue(name = AuthService.JWT_COOKIE_NAME, required = false) String jwttoken
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
        @CookieValue(name = AuthService.JWT_COOKIE_NAME, required = false) String jwtToken
    ) {
        if (jwtToken == null) return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        return surveyService.processMySurveys(jwtToken);
    }
}
