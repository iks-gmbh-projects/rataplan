import { Component, OnDestroy, OnInit } from '@angular/core';
import { AbstractControl, FormArray, FormControl, FormGroup, Validators } from '@angular/forms';
import { ActivatedRoute, Router } from '@angular/router';
import { Subscription } from 'rxjs';
import { Checkbox, Question, QuestionGroup, Survey } from '../survey.model';
import { SurveyService } from '../survey.service';

@Component({
  selector: 'app-survey-create-form',
  templateUrl: './survey-create-form.component.html',
  styleUrls: ['./survey-create-form.component.css']
})
export class SurveyCreateFormComponent implements OnInit, OnDestroy {
  public formGroup?: FormGroup;
  public isEdit: boolean = false;

  private sub?: Subscription;

  constructor(private router: Router, private route: ActivatedRoute, private surveys: SurveyService) { }

  public ngOnInit(): void {
    this.isEdit = !!this.route.snapshot.data['survey'];
    this.formGroup = this.createSurvey(this.route.snapshot.data['survey']);
    this.sub = this.route.data.subscribe(data => {
      this.isEdit = !!data['survey'];
      this.formGroup = this.createSurvey(data['survey']);
    });
  }

  public ngOnDestroy(): void {
    this.sub?.unsubscribe();
  }

  private createSurvey(survey?: Survey): FormGroup {
    return new FormGroup({
      id: new FormControl(survey?.id),
      accessId: new FormControl(survey?.accessId),
      participationId: new FormControl(survey?.participationId),
      name: new FormControl(survey?.name || null, Validators.required),
      description: new FormControl(survey?.description || null, Validators.required),
      startDate: new FormControl(survey?.startDate || null, Validators.required),
      endDate: new FormControl(survey?.endDate || null, Validators.required),
      openAccess: new FormControl(survey?.openAccess || false),
      anonymousParticipation: new FormControl(survey?.anonymousParticipation || false),
      userId: new FormControl(survey?.userId || 120),
      questionGroups: new FormArray(survey?.questionGroups.map(this.createQuestionGroup, this) || [this.createQuestionGroup()], Validators.required)
    })
  }

  public createQuestionGroup(questionGroup?: QuestionGroup): FormGroup {
    return new FormGroup({
      id: new FormControl(questionGroup?.id),
      title: new FormControl(questionGroup?.title || null, Validators.required),
      questions: new FormArray(questionGroup?.questions?.map(this.createQuestion, this) || [this.createQuestion()], Validators.required)
    });
  }

  public createQuestion(question?: Question): FormGroup {
    return new FormGroup({
      id: new FormControl(question?.id),
      text: new FormControl(question?.text || null, Validators.required),
      required: new FormControl(question?.required || false),
      checkboxGroup: new FormGroup({
        multipleSelect: new FormControl(question?.checkboxGroup?.multipleSelect || false),
        minSelect: new FormControl(question?.checkboxGroup?.minSelect || 0, Validators.min(0)),
        maxSelect: new FormControl(question?.checkboxGroup?.maxSelect || 2, Validators.min(2)),
        checkboxes: new FormArray(question?.checkboxGroup?.checkboxes?.map(this.createCheckbox, this) || [])
      })
    });
  }

  public createCheckbox(checkbox?:Checkbox): FormGroup {
    return new FormGroup({
      id: new FormControl(checkbox?.id),
      text: new FormControl(checkbox?.text || null, Validators.required),
      hasTextField: new FormControl(checkbox?.hasTextField || false),
      answers: new FormArray([]),
    });
  }

  public getQuestionGroups(): FormArray {
    return this.formGroup?.get("questionGroups") as FormArray;
  }

  public getQuestions(questionGroup: AbstractControl): FormArray {
    return questionGroup?.get("questions") as FormArray;
  }

  public getCheckboxes(question: AbstractControl): FormArray {
    return question?.get(["checkboxGroup", "checkboxes"]) as FormArray;
  }

  public submit(): void {
    if(!this.formGroup || this.formGroup.invalid) return;
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
    (
      this.isEdit ?
      this.surveys.editSurvey(survey) :
      this.surveys.createSurvey(survey)
    ).subscribe(surv => this.router.navigate(["..", "access", surv.accessId], {relativeTo: this.route}));
  }
}
