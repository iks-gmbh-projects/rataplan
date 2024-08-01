import { HttpErrorResponse } from '@angular/common/http';
import { Component, OnDestroy, OnInit } from '@angular/core';
import { AbstractControl } from '@angular/forms';
import { MatDialog } from '@angular/material/dialog';
import { MatSnackBar } from '@angular/material/snack-bar';
import { ActivatedRoute } from '@angular/router';
import { Actions, ofType } from '@ngrx/effects';
import { Store } from '@ngrx/store';
import { delay, map, Observable, of, Subscription, switchMap } from 'rxjs';
import { distinctUntilChanged } from 'rxjs/operators';
import { defined, nonUndefined } from '../../operators/non-empty';
import { Survey } from '../survey.model';
import { surveyFormActions } from './state/survey-form.action';
import { surveyFormFeature } from './state/survey-form.feature';
import { SurveyAnswerComponent } from './survey-answer/survey-answer.component';

@Component({
  selector: 'app-survey-form',
  templateUrl: './survey-form.component.html',
  styleUrls: ['./survey-form.component.css'],
})

export class SurveyFormComponent implements OnInit, OnDestroy {
  public survey$: Observable<Survey>;
  public page$: Observable<number>;
  readonly busy$: Observable<boolean>;
  readonly delayedBusy$: Observable<boolean>;
  private subs: Subscription[] = [];
  protected pageForm?: AbstractControl;
  readonly isResponseValid$: Observable<boolean>;
  
  constructor(
    private readonly route: ActivatedRoute,
    private readonly store: Store,
    private readonly actions$: Actions,
    private readonly snackBars: MatSnackBar,
    private readonly dialogs: MatDialog,
  )
  {
    this.survey$ = this.store.select(surveyFormFeature.selectSurvey).pipe(defined);
    this.page$ = this.store.select(surveyFormFeature.selectPage);
    this.busy$ = this.store.select(surveyFormFeature.selectBusy);
    this.delayedBusy$ = this.busy$.pipe(
      switchMap(v => v ? of(v).pipe(delay(1000)) : of(v)),
    );
    this.isResponseValid$ = this.store.select(surveyFormFeature.selectValid).pipe(
      map(valid => Object.values(valid ?? {x: undefined})
        .every(q => Object.values(q ?? {y: false}).every(b => b))
      )
    );
  }
  
  public ngOnInit(): void {
    this.subs.forEach(s => s.unsubscribe());
    this.subs = [
      this.route.data.pipe(
        map(({survey}) => survey as Survey | undefined),
        distinctUntilChanged(),
        nonUndefined,
      ).subscribe(survey => this.store.dispatch(surveyFormActions.init({survey}))),
      this.actions$.pipe(
        ofType(surveyFormActions.postAnswersSuccess),
      ).subscribe(() => this.dialogs.open(SurveyAnswerComponent)),
      this.actions$.pipe(
        ofType(surveyFormActions.postAnswersError),
      ).subscribe(({error}: {error: HttpErrorResponse}) => {
        switch(error.status) {
        case 409:
          this.snackBars.open('Sie haben bereits teilgenommen.', 'OK');
          break;
        case 422:
          this.snackBars.open('Hochladen nicht erfolgreich, Antwort oder Umfrage war ungültig.', 'OK');
          break;
        default:
          this.snackBars.open('Fehler beim Hochladen der Antwort: ' + error.status, 'OK');
          break;
        }
      }),
    ];
  }
  
  public ngOnDestroy(): void {
    this.subs.forEach(s => s.unsubscribe());
    this.subs = [];
  }
  
  public submit(): void {
    this.store.dispatch(surveyFormActions.postAnswers());
  }
  
  public prevPage(): void {
    this.store.dispatch(surveyFormActions.previousPage({answers: {}}));
  }
  
  protected readonly Infinity = Infinity;
}