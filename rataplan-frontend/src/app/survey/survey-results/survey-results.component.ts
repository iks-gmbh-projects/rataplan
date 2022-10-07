import { Component, OnChanges, OnDestroy, OnInit, SimpleChanges } from '@angular/core';
import { ActivatedRoute } from '@angular/router';
import { Observable, Subscription } from 'rxjs';
import { Answer, Survey, SurveyResponse } from '../survey.model';
import { SurveyService } from '../survey.service';

@Component({
  selector: 'app-survey-results',
  templateUrl: './survey-results.component.html',
  styleUrls: ['./survey-results.component.css']
})
export class SurveyResultsComponent implements OnInit, OnDestroy, OnChanges {
  public survey?: Survey;
  private sub?: Subscription;
  public columns: { [questionId: string | number]: string[] } = {};
  public answers: SurveyResponse[] = [];
  public busy: boolean = false;
  public error: any = null;

  constructor(private route: ActivatedRoute, private surveys: SurveyService) { }

  public ngOnInit(): void {
    this.fetchAnswers(this.route.snapshot.data['survey']);
    this.sub = this.route.data.subscribe(d => this.fetchAnswers(d['survey']));
  }

  public ngOnDestroy(): void {
    this.sub?.unsubscribe();
  }

  public ngOnChanges(changes: SimpleChanges): void {
    console.log(changes);
  }

  public checkboxChecked(response: SurveyResponse, questionId: string|number, checkboxId: string|number): boolean {
    return (response.answers[questionId].checkboxes || {})[checkboxId];
  }

  private fetchAnswers(survey: Survey): void {
    if (this.survey === survey) return;
    this.busy = true;
    this.survey = survey;
    this.columns = {};
    for (let questionGroup of survey.questionGroups) {
      for (let question of questionGroup.questions) {
        if (question.id) {
          this.columns[question.id] = ["user"];
          if (question.checkboxGroup) {
            for (let checkbox of question.checkboxGroup.checkboxes) {
              if (checkbox.id) this.columns[question.id].push("checkbox" + checkbox.id);
            }
          }
          this.columns[question.id].push("answer");
        }
      }
    }
    this.answers = [];
    this.error = null;
    this.surveys.fetchAnswers(survey).subscribe({
      next: answers => this.answers = answers,
      error: err => {
        this.error = err;
        this.busy = false;
      },
      complete: () => this.busy = false,
    })
  }

  public toCheckbox(checked: boolean): string {
    return checked ? "check_box" : "check_box_outline_blank";
  }
}
