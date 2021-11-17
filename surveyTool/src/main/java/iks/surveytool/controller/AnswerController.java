package iks.surveytool.controller;

import iks.surveytool.dtos.AnswerDTO;
import iks.surveytool.entities.Answer;
import iks.surveytool.services.AnswerService;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import java.util.Arrays;
import java.util.List;

@Controller
@CrossOrigin(origins = "*")
public class AnswerController {

    private final AnswerService answerService;

    public AnswerController(AnswerService answerService) {
        this.answerService = answerService;
    }

    @PostMapping("/postAnswers")
    public ResponseEntity<List<AnswerDTO>> postAnswers(@RequestBody AnswerDTO[] answerDTOs) {
        List<AnswerDTO> answerDTOList = Arrays.asList(answerDTOs);
        List<Answer> answerList = answerService.createAnswersFromDtos(answerDTOList);
        List<Answer> savedAnswers = answerService.saveListOfAnswers(answerList);
        List<AnswerDTO> savedAnswerDTOs = answerService.createAnswerDtos(savedAnswers);
        return ResponseEntity.ok(savedAnswerDTOs);
    }

    @GetMapping("/getAnswersByQuestionId")
    public ResponseEntity<List<AnswerDTO>> getAnswersByQuestionId(@RequestParam Long questionId) {
        List<AnswerDTO> answerDTOs = answerService.createAnswerDtos(questionId);
        return ResponseEntity.ok(answerDTOs);
    }
}

