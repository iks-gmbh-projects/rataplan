import { Component, OnDestroy, OnInit } from '@angular/core';
import { FormArray, FormControl, FormGroup, NgForm, RequiredValidator, Validators } from '@angular/forms';
import { ActivatedRoute, Router } from '@angular/router';
import { Subscription } from 'rxjs';
import { Question, QuestionGroup, Survey } from '../survey.model';
import { SurveyService } from '../survey.service';

@Component({
  selector: 'app-survey-form',
  templateUrl: './survey-form.component.html',
  styleUrls: ['./survey-form.component.css']
})

export class SurveyFormComponent implements OnInit, OnDestroy {
  public survey?: Survey;
  public answers?: FormGroup;
  public page: number = 0;
  public busy: boolean = false;
  private sub?: Subscription;
  constructor(private route: ActivatedRoute, private router:Router, private surveys:SurveyService) { }

  public ngOnInit(): void {
    this.generateFormGroups(this.route.snapshot.data['survey']);
    this.sub = this.route.data.subscribe(data => this.generateFormGroups(data['survey']));
  }

  private generateFormGroups(survey: Survey) {
    this.survey = survey;
    this.page = 0;
    let answers: { [questionId: string]: FormGroup } = {};
    for (let group of survey.questionGroups!) {
      for (let question of group.questions) {
        answers[question.id!] = new FormGroup({
          questionId: new FormControl(question.id),
          ...(question.checkboxGroup ? { checkboxId: new FormControl(null, Validators.required) } : null),
          text: new FormControl(null, question.checkboxGroup ? undefined : Validators.required),
          userId: new FormControl(120),
          userName: new FormControl("Anonym"),
        }, v => {
          if (question.checkboxGroup?.checkboxes.find(cb => cb.id == v.value.checkboxId)?.hasTextField) {
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

  public nextPage(): void {
    if (!this.survey) return;
    this.page++;
    if (this.page >= this.survey.questionGroups.length) this.page = this.survey.questionGroups.length - 1;
  }

  public prevPage(): void {
    this.page--;
    if (this.page < 0) this.page = 0;
  }

  public submit(): void {
    if (this.answers?.valid) {
      this.busy=true;
      this.surveys.answerSurvey(Object.values(this.answers.value)).subscribe({
        next: a => this.router.navigate(["/survey", "list"]),
        error: err => console.log(err),
        complete: () => this.busy = false,
      });
    }
  }
}
