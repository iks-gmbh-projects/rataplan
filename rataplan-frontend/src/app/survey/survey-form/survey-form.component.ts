import { Component, OnDestroy, OnInit } from '@angular/core';
import { FormArray, FormControl, FormGroup, NgForm, RequiredValidator, Validators } from '@angular/forms';
import { MatSnackBar } from '@angular/material/snack-bar';
import { ActivatedRoute, Router } from '@angular/router';
import { Subscription } from 'rxjs';
import { Answer, Question, QuestionGroup, Survey } from '../survey.model';
import { SurveyService } from '../survey.service';

@Component({
  selector: 'app-survey-form',
  templateUrl: './survey-form.component.html',
  styleUrls: ['./survey-form.component.css']
})

export class SurveyFormComponent implements OnInit, OnDestroy {
  public survey?: Survey;
  public page: number = 0;
  private answers: {[key: string|number]:Answer} = {};
  private sub?: Subscription;
  constructor(private route: ActivatedRoute, private router:Router, private surveys:SurveyService, private snackBars:MatSnackBar) { }

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

  public pageSubmit(answers: {[key: string|number]: Answer}|null) {
    if(!this.survey) return;
    if(answers == null) {
      if(this.page>0) this.page--;
    } else {
      this.answers = {...this.answers, ...answers};
      this.page++;
      if(this.page >= this.survey.questionGroups.length) {
        this.surveys.answerSurvey(Object.values(this.answers)).subscribe({
          next: d => this.router.navigate(["/survey","list"]),
          error: err => {
            this.page--;
            this.snackBars.open("Fehler beim Hochladen der Antwort.");
          },
        });
      }
    }
  }
}
