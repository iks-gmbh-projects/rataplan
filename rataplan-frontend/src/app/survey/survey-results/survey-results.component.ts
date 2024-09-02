import { Component } from '@angular/core';
import { Store } from '@ngrx/store';
import { Observable, of, timer } from 'rxjs';
import { distinctUntilChanged, map, switchMap } from 'rxjs/operators';
import { defined } from '../../operators/non-empty';
import { Question, Survey, SurveyResponse } from '../survey.model';
import { surveyResultsAction } from './state/survey-results.action';
import { surveyResultsFeature } from './state/survey-results.feature';
import { AnswerCharts } from './state/survey-results.reducer';

@Component({
  selector: 'app-survey-results',
  templateUrl: './survey-results.component.html',
  styleUrls: ['./survey-results.component.css'],
})
export class SurveyResultsComponent {
  public readonly survey$: Observable<Survey>;
  public readonly tableColumns$: Observable<Partial<Record<string | number, Partial<Record<string | number, string[]>>>>>;
  public readonly charts$: Observable<Partial<Record<string | number, Partial<Record<string | number, AnswerCharts>>>>>;
  public readonly answers$: Observable<SurveyResponse[]>;
  public readonly busy$: Observable<boolean>;
  public readonly delayedBusy$: Observable<boolean>;
  constructor(
    private readonly store: Store,
  ) {
    this.survey$ = store.select(surveyResultsFeature.selectSurvey).pipe(defined);
    this.tableColumns$ = store.select(surveyResultsFeature.selectTableColumns).pipe(defined);
    this.charts$ = store.select(surveyResultsFeature.selectCharts).pipe(
      defined,
    );
    this.answers$ = store.select(surveyResultsFeature.selectResults).pipe(defined);
    this.busy$ = store.select(surveyResultsFeature.selectBusy);
    this.delayedBusy$ = this.busy$.pipe(
      distinctUntilChanged(),
      switchMap(b => b ? timer(1000).pipe(map(() => b)) : of(b)),
    );
  }
  
  protected hasTextfield(question: Question): boolean {
    switch(question.type) {
    case 'OPEN':
      return true;
    case 'CHOICE':
      for(let checkbox of question.choices!) {
        if(checkbox.hasTextField) return true;
      }
      return false;
    case 'ORDER':
      return false;
    }
  }
  
  protected toCheckbox(checked: boolean): string {
    return checked ? 'check_box' : 'check_box_outline_blank';
  }
  
  protected checkboxPercentage(groupId: string | number, questionRank: string | number, checkboxId: string | number, answers: SurveyResponse[]): number | string {
    let count = 0;
    let total = 0;
    for(let response of answers) {
      let answer = response.answers[groupId]?.[questionRank];
      if(answer) {
        total++;
        if(answer.checkboxes![checkboxId]) count++;
      }
    }
    if(total > 0) return count*100/total;
    else return NaN;
  }
  
  /**
   * Determines an overall ranking of the choices of an OrderQuestion using STV (Single Transferable Vote)
   */
  protected orderRanking(groupId: string | number, questionRank: string | number, answers: SurveyResponse[]): Partial<Record<string|number, number>> {
    const ret: Partial<Record<string | number, number>> = {};
    const votes: Partial<Record<string | number, Iterator<string | number>[]>> = {};
    for(const result of answers) {
      const order = result.answers[groupId]?.[questionRank]?.order;
      if(order) {
        const it = order[Symbol.iterator]();
        const r = it.next();
        if(!r.done) {
          votes[r.value]??=[];
          votes[r.value]!.push(it);
        }
      }
    }
    while(true) {
      const [minId, minL, n] = Object.entries(votes).reduce<[keyof typeof votes, Iterator<string|number>[] | undefined, number]>(
        ([ida, la, n], [idv, lv]) => lv !== undefined && (la === undefined || lv.length < la.length) ? [idv, lv, n+1] : [ida, la, n+1],
        [-1, undefined, 0],
      );
      if(minL === undefined) break;
      ret[minId] = n;
      delete votes[minId];
      for(const it of minL) {
        for(let r = it.next(); !r.done; r = it.next()) {
          if(r.value in votes) {
            votes[r.value]?.push(it);
            break;
          }
        }
      }
    }
    return ret;
  }
  
  public downloadResults(): void {
    this.store.dispatch(surveyResultsAction.downloadResults());
  }
  
  protected readonly Object = Object;
}