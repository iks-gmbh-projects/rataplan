package iks.surveytool.controller;

import iks.surveytool.dtos.AnswerDTO;
import iks.surveytool.entities.Answer;
import iks.surveytool.services.AnswerService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import java.util.Arrays;
import java.util.List;

@Controller
@RequestMapping("/answers")
@CrossOrigin(origins = "*")
@RequiredArgsConstructor
public class AnswerController {

    private final AnswerService answerService;

    @PostMapping
    public ResponseEntity<List<AnswerDTO>> addAnswers(@RequestBody AnswerDTO[] answerDTOs) {
        List<AnswerDTO> answerDTOList = Arrays.asList(answerDTOs);
        List<Answer> answerList = answerService.createAnswersFromDTOs(answerDTOList);
        if (answerService.validate(answerList)) {
            List<Answer> savedAnswers = answerService.saveListOfAnswers(answerList);
            List<AnswerDTO> savedAnswerDTOs = answerService.createAnswerDTOs(savedAnswers);
            return ResponseEntity.ok(savedAnswerDTOs);
        } else {
            return ResponseEntity.status(HttpStatus.UNPROCESSABLE_ENTITY).build();
        }
    }

    @GetMapping("/questions/{questionId}")
    public ResponseEntity<List<AnswerDTO>> findAnswersByQuestionId(@PathVariable Long questionId) {
        List<AnswerDTO> answerDTOs = answerService.createAnswerDTOs(questionId);
        return ResponseEntity.ok(answerDTOs);
    }
}

