package iks.surveytool.controller;

import iks.surveytool.dtos.AnswerDTO;
import iks.surveytool.services.AnswerService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Controller
@RequestMapping("/answers")
@CrossOrigin(origins = "*")
@RequiredArgsConstructor
public class AnswerController {

    private final AnswerService answerService;

    @PostMapping
    public ResponseEntity<List<AnswerDTO>> addAnswers(@RequestBody AnswerDTO[] answerDTOs) {
        return answerService.processAnswerDTOs(answerDTOs);
    }

    @GetMapping("/questions/{questionId}")
    public ResponseEntity<List<AnswerDTO>> findAnswersByQuestionId(@PathVariable Long questionId) {
        return answerService.processAnswersByQuestionId(questionId);
    }
}

