import { Component, OnDestroy, OnInit } from '@angular/core';
import { FormArray, FormControl, FormGroup, NgForm, RequiredValidator, Validators } from '@angular/forms';
import { ActivatedRoute } from '@angular/router';
import { Subscription } from 'rxjs';
import { Question, QuestionGroup, Survey } from '../survey.model';

@Component({
  selector: 'app-survey-form',
  templateUrl: './survey-form.component.html',
  styleUrls: ['./survey-form.component.css']
})

export class SurveyFormComponent implements OnInit, OnDestroy {
  public survey: Survey|null = null;
  public answers: FormGroup = new FormGroup({});
  private sub: Subscription|null = null;
  constructor(private route: ActivatedRoute) {}

  public ngOnInit(): void {
    this.generateFormGroups(this.route.snapshot.data['survey']);
    this.sub = this.route.data.subscribe(data => this.generateFormGroups(data['survey']));
  }

  private generateFormGroups(survey: Survey) {
    this.survey = survey;
    let answers: {[questionId: string]: FormGroup} = {};
    for(let group of survey.questionGroups!) {
      for(let question of group.questions) {
        answers[question.id] = new FormGroup({
          questionId: new FormControl(question.id),
          ...(question.checkboxGroup ? {checkboxId: new FormControl(null, Validators.required)} : null),
          text: new FormControl(null, question.checkboxGroup ? undefined : Validators.required)
        }, v => {
          if(question.checkboxGroup?.checkboxes.find(cb => cb.id == v.value.checkboxId)?.hasTextField) {
            return Validators.required(v.get("text") as FormControl);
          }
          return null;
        });
      }
    }
    this.answers = new FormGroup(answers);
  }

  public ngOnDestroy(): void {
    this.sub?.unsubscribe();
  }

  public submit(): void {
    if(this.answers.valid) console.log(this.answers.value);
  }
}
