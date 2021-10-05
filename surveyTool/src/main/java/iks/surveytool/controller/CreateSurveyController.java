package iks.surveytool.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/create")
public class CreateSurveyController {
    @GetMapping("")
    public String start() {
        
        return "createSurvey";
    }
}
