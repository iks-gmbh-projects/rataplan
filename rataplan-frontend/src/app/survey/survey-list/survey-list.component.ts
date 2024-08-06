import { Clipboard } from '@angular/cdk/clipboard';
import { Component } from '@angular/core';
import { MatSnackBar } from '@angular/material/snack-bar';
import { Store } from '@ngrx/store';
import { delay, Observable, of } from 'rxjs';
import { distinctUntilChanged, switchMap } from 'rxjs/operators';

import { SurveyHead } from '../survey.model';
import { surveyListAction, SurveyListType } from './state/survey-list.action';
import { surveyListFeature } from './state/survey-list.feature';

@Component({
  selector: 'app-survey-list',
  templateUrl: './survey-list.component.html',
  styleUrls: ['./survey-list.component.css'],
})
export class SurveyListComponent {
  protected readonly listType$: Observable<SurveyListType>;
  protected readonly data$: Observable<{survey: SurveyHead, started: boolean, expired: boolean}[]>;
  protected readonly busy$: Observable<boolean>;
  protected readonly delayedBusy$: Observable<boolean>;
  protected readonly error$: Observable<any>;
  
  constructor(
    private readonly store: Store,
    readonly snackBars: MatSnackBar,
    readonly clipboard: Clipboard,
  ) {
    this.listType$ = this.store.select(surveyListFeature.selectList)
    this.data$ = this.store.select(surveyListFeature.selectData);
    this.busy$ = this.store.select(surveyListFeature.selectBusy);
    this.delayedBusy$ = this.busy$.pipe(
      distinctUntilChanged(),
      switchMap(v => v ? of(v).pipe(delay(1000)) : of(v)),
    );
    this.error$ = this.store.select(surveyListFeature.selectError);
  }

  public expired(survey: SurveyHead): boolean {
    return survey.endDate < new Date();
  }
  
  public updateList(): void {
    this.store.dispatch(surveyListAction.refresh());
  }
  
  protected readonly SurveyListType = SurveyListType;
}