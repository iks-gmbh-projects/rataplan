package iks.surveytool.controller;

import iks.surveytool.entities.*;
import iks.surveytool.services.SurveyService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.validation.ObjectError;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.bind.support.SessionStatus;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import javax.validation.Valid;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.List;
import java.util.Optional;

@Controller
@RequestMapping("/createSurvey")
@SessionAttributes("newSurvey")
public class SurveyController {

    private final SurveyService surveyService;

    public SurveyController(SurveyService surveyService) {
        this.surveyService = surveyService;
    }

    @ModelAttribute("newSurvey")
    public Survey getNewSurvey() {
        return new Survey();
    }

    @GetMapping("")
    public String getSurveyForm(@ModelAttribute("newSurvey") Survey newSurvey, Model model) {
        model.addAttribute("newSurvey", newSurvey);
        return "createSurvey";
    }

    @PostMapping("")
    public String postSurveyForm(@Valid @ModelAttribute("newSurvey") Survey newSurvey,
                                 BindingResult bindingResult,
                                 RedirectAttributes redirectAttributes) {
        if (bindingResult.hasErrors()) {
            return "createSurvey";
        } else if (!surveyService.startDateBeforeEndDate(newSurvey.getStartDate(), newSurvey.getEndDate())) {
            bindingResult.rejectValue("endDate",
                    "error.survey.endDate",
                    "Das Enddatum liegt vor dem Startdatum.");
            return "createSurvey";
        } else {
            redirectAttributes.addFlashAttribute("newSurvey", newSurvey);
        }

        return "redirect:createSurvey/questions";
    }

    @GetMapping("/questions")
    public String getAddQuestionsForm(@Valid @ModelAttribute("newSurvey") Survey newSurvey,
                                      Model model) {
        model.addAttribute("newSurvey", newSurvey);
        model.addAttribute("newQuestionGroup", new QuestionGroup());
        model.addAttribute("newQuestion", new Question());
        model.addAttribute("newCheckboxGroup", new CheckboxGroup());
        model.addAttribute("newCheckbox", new Checkbox());
        return "addQuestions";
    }

    @PostMapping("/questions/addGroup")
    public String addQuestionGroup(@ModelAttribute("newSurvey") Survey newSurvey,
                                   @ModelAttribute("newQuestionGroup") QuestionGroup newQuestionGroup,
                                   RedirectAttributes redirectAttributes) {
        surveyService.addQuestionGroupToSurvey(newSurvey, newQuestionGroup);

        redirectAttributes.addFlashAttribute("newSurvey", newSurvey);

        return "redirect:/createSurvey/questions";
    }

    @PostMapping("/questions/addQuestion/{questionGroupIndex}")
    public String addQuestionToGroup(@ModelAttribute("newSurvey") Survey newSurvey,
                                     @ModelAttribute("newQuestion") Question newQuestion,
                                     @ModelAttribute("newCheckboxGroup") CheckboxGroup checkboxGroup,
                                     @PathVariable("questionGroupIndex") int questionGroupIndex,
                                     RedirectAttributes redirectAttributes) {
        surveyService.addQuestionToQuestionGroup(newSurvey, questionGroupIndex, newQuestion, checkboxGroup);

        redirectAttributes.addFlashAttribute("newSurvey", newSurvey);

        return "redirect:/createSurvey/questions";
    }

    @PostMapping("/questions/addQuestion/{questionGroupIndex}/{questionIndex}")
    public String addCheckboxToQuestion(@ModelAttribute("newSurvey") Survey newSurvey,
                                        @ModelAttribute("newCheckbox") Checkbox newCheckbox,
                                        @PathVariable("questionGroupIndex") int questionGroupIndex,
                                        @PathVariable("questionIndex") int questionIndex,
                                        RedirectAttributes redirectAttributes) {
        surveyService.addCheckboxToQuestion(newSurvey, questionGroupIndex, questionIndex, newCheckbox);

        redirectAttributes.addFlashAttribute("newSurvey", newSurvey);

        return "redirect:/createSurvey/questions";
    }

    @PostMapping("/save")
    public String saveSurvey(@ModelAttribute("newSurvey") Survey newSurvey,
                             SessionStatus status,
                             Model model,
                             BindingResult bindingResult) throws MalformedURLException {
        List<String> errorMessages = surveyService.checkIfAnythingEmpty(newSurvey);
        if (!errorMessages.isEmpty()) {
            for (String errorMessage : errorMessages) {
                ObjectError error = new ObjectError("globalError", errorMessage);
                bindingResult.addError(error);
            }

            model.addAttribute("newQuestionGroup", new QuestionGroup());
            model.addAttribute("newQuestion", new Question());
            model.addAttribute("newCheckboxGroup", new CheckboxGroup());
            model.addAttribute("newCheckbox", new Checkbox());

            return "addQuestions";
        }

        Long surveyID = surveyService.addSurvey(newSurvey);

        // Remove survey as session attribute
        status.setComplete();

        return "redirect:/createSurvey/" + surveyID + "/final";
    }


    @GetMapping("/{surveyID}/final")
    public String finalizeCreation(@Valid @PathVariable("surveyID") Long surveyID, Model model) throws MalformedURLException {
        Optional<Survey> surveyOptional = surveyService.findSurveyById(surveyID);
        if (surveyOptional.isPresent()) {
            Survey survey = surveyOptional.get();
            model.addAttribute("survey", survey);
            
            URL url = new URL("http", "localhost", 8080, "/answer?surveyUUID=" + survey.getUuid());
            model.addAttribute("url", url);
        } else {
            model.addAttribute("notAvailable",
                    "Eine Umfrage mit der ID '" + surveyID + "' ist leider nicht vorhanden.");
        }

        return "finalizeSurvey";
    }
}
