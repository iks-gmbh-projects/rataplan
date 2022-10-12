import { Component, OnDestroy, OnInit } from '@angular/core';
import { FormArray, FormControl, FormGroup, NgForm, RequiredValidator, Validators } from '@angular/forms';
import { MatDialog } from '@angular/material/dialog';
import { MatSnackBar } from '@angular/material/snack-bar';
import { MatStepper } from '@angular/material/stepper';
import { ActivatedRoute, Router } from '@angular/router';
import { Subscription } from 'rxjs';
import { Answer, Question, QuestionGroup, Survey } from '../survey.model';
import { SurveyService } from '../survey.service';
import { SurveyAnswerComponent } from './survey-answer/survey-answer.component';

@Component({
  selector: 'app-survey-form',
  templateUrl: './survey-form.component.html',
  styleUrls: ['./survey-form.component.css']
})

export class SurveyFormComponent implements OnInit, OnDestroy {
  public survey?: Survey;
  private answers: {[key: string|number]:Answer} = {};
  private sub?: Subscription;
  constructor(private route: ActivatedRoute, private router:Router, private surveys:SurveyService, private snackBars:MatSnackBar, private dialogs:MatDialog) { }

  public ngOnInit(): void {
    this.survey = this.route.snapshot.data['survey'];
    this.sub = this.route.data.subscribe(data => {
      if(this.survey !== data['survey']) {
        this.survey = data['survey'];
        this.answers = {};
      }
    });
  }

  public ngOnDestroy(): void {
    this.sub?.unsubscribe();
  }

  public pageSubmit(stepper: MatStepper, answers?: {[key: string|number]: Answer}) {
    if(!this.survey) return;
    if(answers) {
      this.answers = {...this.answers, ...answers};
      stepper.next();
    } else {
      stepper.previous();
    }
  }

  public submit(stepper: MatStepper): void {
    if(!this.survey) return;
    if(stepper.selectedIndex >= this.survey.questionGroups.length) {
      this.surveys.answerSurvey({
        surveyId: this.survey.id!,
        answers: this.answers,
      }).subscribe({
        next: d => {
          this.dialogs.open(SurveyAnswerComponent);
        },
        error: err => {
          stepper.previous();
          this.snackBars.open("Fehler beim Hochladen der Antwort.", "OK");
        },
      });
    }
  }
}
