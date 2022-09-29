import { Component, OnDestroy, OnInit } from '@angular/core';
import { ActivatedRoute } from '@angular/router';
import { Subscription } from 'rxjs';
import { Answer, Survey } from '../survey.model';
import { SurveyService } from '../survey.service';

@Component({
  selector: 'app-survey-results',
  templateUrl: './survey-results.component.html',
  styleUrls: ['./survey-results.component.css']
})
export class SurveyResultsComponent implements OnInit, OnDestroy {
  public survey?: Survey;
  private sub?:Subscription;
  public answers: {[questionId: string]: Answer[]} = {};
  private _busy: {[questionId: string]: boolean} = {};
  private _lbusy: boolean = false;
  public get busy(): boolean {
    return this._lbusy || Object.values(this._busy).some(b => b);
  }

  constructor(private route:ActivatedRoute, private surveys:SurveyService) { }

  public ngOnInit(): void {
    this.fetchAnswers(this.route.snapshot.data['survey']);
    this.sub = this.route.data.subscribe(d => this.fetchAnswers(d['survey']));
  }

  public ngOnDestroy(): void {
    this.sub?.unsubscribe();
  }

  private fetchAnswers(survey: Survey): void {
    this._lbusy = true;
    this.survey = survey;
    this._busy = {};
    this.answers = {};
    for(let question of survey.questionGroups) {
      if(!question.id) continue;
      this._busy[question.id!] = true;
      this.surveys.fetchAnswers(question.id).subscribe({
        next: ans => this.answers[question.id!] = ans,
        complete: () => this._busy[question.id!] = false,
      })
    }
    this._lbusy = false;
  }
}
