import { Component, OnChanges, OnDestroy, OnInit, SimpleChanges } from '@angular/core';
import { ActivatedRoute } from '@angular/router';
import { Observable, Subscription } from 'rxjs';
import { Answer, Survey } from '../survey.model';
import { SurveyService } from '../survey.service';

@Component({
  selector: 'app-survey-results',
  templateUrl: './survey-results.component.html',
  styleUrls: ['./survey-results.component.css']
})
export class SurveyResultsComponent implements OnInit, OnDestroy, OnChanges {
  public survey?: Survey;
  private sub?: Subscription;
  public answers: { [questionId: string]: {columns: string[], observable: Observable<Answer[]>} } = {};
  public busy: boolean = false;

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

  private fetchAnswers(survey: Survey): void {
    if (this.survey === survey) return;
    this.busy = true;
    this.survey = survey;
    this.answers = {};
    for (let group of survey.questionGroups) {
      for (let question of group.questions) {
        if (!question.id) continue;
        const qid = question.id;
        let columns: string[] = ["user"];
        if(question.checkboxGroup) {
          let hasFreeText: boolean = false;
          for(let checkbox of question.checkboxGroup.checkboxes) {
            if(checkbox.id) {
            if(checkbox.hasTextField) hasFreeText = true;
            columns.push("checkbox"+checkbox.id);
            }
          }
          if(hasFreeText) columns.push("answer");
        } else {
          columns.push("answer");
        }
        this.answers[qid] = {
          columns: columns,
          observable: this.surveys.fetchAnswers(qid),
        };
      }
    }
    this.busy = false;
  }

  public hasAnswer(questionId: string | number): boolean {
    return questionId in this.answers;
  }
}
