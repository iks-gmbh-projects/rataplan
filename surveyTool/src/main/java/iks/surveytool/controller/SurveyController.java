package iks.surveytool.controller;

import iks.surveytool.entities.Survey;
import iks.surveytool.services.SurveyService;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;

import java.net.MalformedURLException;
import java.util.Optional;

@Controller
@RequestMapping("/createSurvey")
@CrossOrigin(origins = "http://localhost:4200")
public class SurveyController {

    private final SurveyService surveyService;

    public SurveyController(SurveyService surveyService) {
        this.surveyService = surveyService;
    }

    @PostMapping("")
    public ResponseEntity<Survey> postSurveyForm(@RequestBody Survey survey) throws MalformedURLException {
        Long id = surveyService.addSurvey(survey);
        Optional<Survey> savedSurvey = surveyService.findSurveyById(id);
        return savedSurvey.map(ResponseEntity::ok).orElse(null);
    }

//    @GetMapping("/questions/{id}")
//    public ResponseEntity<Survey> getSurveyAddQuestions(@PathVariable("id") Long id) {
//        Optional<Survey> surveyOptional = surveyService.findSurveyById(id);
//        Survey survey = surveyOptional.orElse(null);
//        return ResponseEntity.ok(survey);
//    }

//    @PostMapping("/questions/addGroup")
//    public String addQuestionGroup(@ModelAttribute("newSurvey") Survey newSurvey,
//                                   @ModelAttribute("newQuestionGroup") QuestionGroup newQuestionGroup,
//                                   RedirectAttributes redirectAttributes) {
//        surveyService.addQuestionGroupToSurvey(newSurvey, newQuestionGroup);
//
//        redirectAttributes.addFlashAttribute("newSurvey", newSurvey);
//
//        return "redirect:/createSurvey/questions";
//    }
//
//    @PostMapping("/questions/addQuestion/{questionGroupIndex}")
//    public String addQuestionToGroup(@ModelAttribute("newSurvey") Survey newSurvey,
//                                     @ModelAttribute("newQuestion") Question newQuestion,
//                                     @ModelAttribute("newCheckboxGroup") CheckboxGroup checkboxGroup,
//                                     @PathVariable("questionGroupIndex") int questionGroupIndex,
//                                     RedirectAttributes redirectAttributes) {
//        surveyService.addQuestionToQuestionGroup(newSurvey, questionGroupIndex, newQuestion, checkboxGroup);
//
//        redirectAttributes.addFlashAttribute("newSurvey", newSurvey);
//
//        return "redirect:/createSurvey/questions";
//    }
//
//    @PostMapping("/questions/addQuestion/{questionGroupIndex}/{questionIndex}")
//    public String addCheckboxToQuestion(@ModelAttribute("newSurvey") Survey newSurvey,
//                                        @ModelAttribute("newCheckbox") Checkbox newCheckbox,
//                                        @PathVariable("questionGroupIndex") int questionGroupIndex,
//                                        @PathVariable("questionIndex") int questionIndex,
//                                        RedirectAttributes redirectAttributes) {
//        surveyService.addCheckboxToQuestion(newSurvey, questionGroupIndex, questionIndex, newCheckbox);
//
//        redirectAttributes.addFlashAttribute("newSurvey", newSurvey);
//
//        return "redirect:/createSurvey/questions";
//    }
//
//    @PostMapping("/save")
//    public String saveSurvey(@ModelAttribute("newSurvey") Survey newSurvey,
//                             SessionStatus status,
//                             Model model,
//                             BindingResult bindingResult) throws MalformedURLException {
//        List<String> errorMessages = surveyService.checkIfAnythingEmpty(newSurvey);
//        if (!errorMessages.isEmpty()) {
//            for (String errorMessage : errorMessages) {
//                ObjectError error = new ObjectError("globalError", errorMessage);
//                bindingResult.addError(error);
//            }
//
//            model.addAttribute("newQuestionGroup", new QuestionGroup());
//            model.addAttribute("newQuestion", new Question());
//            model.addAttribute("newCheckboxGroup", new CheckboxGroup());
//            model.addAttribute("newCheckbox", new Checkbox());
//
//            return "addQuestions";
//        }
//
//        Long surveyID = surveyService.addSurvey(newSurvey);
//
//        // Remove survey as session attribute
//        status.setComplete();
//
//        return "redirect:/createSurvey/" + surveyID + "/final";
//    }
//
//
//    @GetMapping("/{surveyID}/final")
//    public String finalizeCreation(@Valid @PathVariable("surveyID") Long surveyID, Model model) throws MalformedURLException {
//        Optional<Survey> surveyOptional = surveyService.findSurveyById(surveyID);
//        if (surveyOptional.isPresent()) {
//            Survey survey = surveyOptional.get();
//            model.addAttribute("survey", survey);
//
//            URL url = new URL("http", "localhost", 8080, "/answer?surveyUUID=" + survey.getUuid());
//            model.addAttribute("url", url);
//        } else {
//            model.addAttribute("notAvailable",
//                    "Eine Umfrage mit der ID '" + surveyID + "' ist leider nicht vorhanden.");
//        }
//
//        return "finalizeSurvey";
//    }
}
