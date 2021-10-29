package iks.surveytool.controller;

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
    public ResponseEntity<List<Answer>> saveAnswersForQuestionGroup(@RequestBody Answer[] answers) {
        List<Answer> answerList = Arrays.asList(answers);
        answerService.saveListOfAnswers(answerList);
        return ResponseEntity.ok(answerList);
    }

    @GetMapping("/getAnswers")
    public ResponseEntity<List<Answer>> getAnswersByQuestionId(@RequestParam Long questionId) {
        List<Answer> answers = answerService.findAnswersByQuestionId(questionId);
        return ResponseEntity.ok(answers);
    }
}

