import { Component, OnDestroy, OnInit } from '@angular/core';
import { ActivatedRoute } from '@angular/router';
import { Observable, Subscription } from 'rxjs';
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
  public answers: {[questionId: string]: Observable<Answer[]>} = {};
  public busy: boolean = false;
  
  constructor(private route:ActivatedRoute, private surveys:SurveyService) { }

  public ngOnInit(): void {
    this.fetchAnswers(this.route.snapshot.data['survey']);
    this.sub = this.route.data.subscribe(d => this.fetchAnswers(d['survey']));
  }

  public ngOnDestroy(): void {
    this.sub?.unsubscribe();
  }

  private fetchAnswers(survey: Survey): void {
    if(this.survey === survey) return;
    this.busy = true;
    this.survey = survey;
    this.answers = {};
    for(let question of survey.questionGroups) {
      if(!question.id) continue;
      this.answers[question.id] = this.surveys.fetchAnswers(question.id);
    }
    this.busy = false;
  }

  public hasAnswer(questionId: string|number): boolean {
    return questionId in this.answers;
  }
}
