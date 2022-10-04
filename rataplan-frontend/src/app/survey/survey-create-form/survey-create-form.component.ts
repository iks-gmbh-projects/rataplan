import { Component } from '@angular/core';
import { AbstractControl, FormArray, FormControl, FormGroup, Validators } from '@angular/forms';
import { ActivatedRoute, Router } from '@angular/router';
import { Survey } from '../survey.model';
import { SurveyService } from '../survey.service';

@Component({
  selector: 'app-survey-create-form',
  templateUrl: './survey-create-form.component.html',
  styleUrls: ['./survey-create-form.component.css']
})
export class SurveyCreateFormComponent {
  public formGroup: FormGroup = new FormGroup({
    name: new FormControl(null, Validators.required),
    description: new FormControl(null, Validators.required),
    startDate: new FormControl(null, Validators.required),
    endDate: new FormControl(null, Validators.required),
    openAccess: new FormControl(true),
    anonymousParticipation: new FormControl(true),
    userId: new FormControl(120),
    userName: new FormControl("Anonym"),
    questionGroups: new FormArray([this.createQuestionGroup()], Validators.required)
  });

  constructor(private router: Router, private route: ActivatedRoute, private surveys: SurveyService) { }

  public createQuestionGroup(): FormGroup {
    return new FormGroup({
      title: new FormControl(null, Validators.required),
      questions: new FormArray([this.createQuestion()], Validators.required)
    });
  }

  public createQuestion(): FormGroup {
    return new FormGroup({
      text: new FormControl(null, Validators.required),
      required: new FormControl(true),
      checkboxGroup: new FormGroup({
        multipleSelect: new FormControl(false),
        minSelect: new FormControl(0, Validators.min(0)),
        maxSelect: new FormControl(2, Validators.min(2)),
        checkboxes: new FormArray([])
      })
    });
  }

  public createCheckbox(): FormGroup {
    return new FormGroup({
      text: new FormControl(null, Validators.required),
      hasTextField: new FormControl(false),
      answers: new FormArray([]),
    });
  }

  public getQuestionGroups(): FormArray {
    return this.formGroup.get("questionGroups") as FormArray;
  }

  public getQuestions(questionGroup: AbstractControl): FormArray {
    return questionGroup.get("questions") as FormArray;
  }

  public getCheckboxes(question: AbstractControl): FormArray {
    return question.get(["checkboxGroup", "checkboxes"]) as FormArray;
  }

  public submit(): void {
    if(this.formGroup.invalid) return;
    let survey: Survey = this.formGroup.value;
    survey.startDate = new Date(survey.startDate);
    survey.endDate = new Date(survey.endDate);
    for(let qg of survey.questionGroups) {
      for(let q of qg.questions) {
        if(q.checkboxGroup && q.checkboxGroup.checkboxes.length == 0) {
          delete q.checkboxGroup;
        }
        q.hasCheckbox= "checkboxGroup" in q;
      }
    }
    this.surveys.createSurvey(survey).subscribe(surv => this.router.navigate(["..", "access", surv.accessId], {relativeTo: this.route}));
  }
}
