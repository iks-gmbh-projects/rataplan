import { Clipboard } from '@angular/cdk/clipboard';
import { Component } from '@angular/core';
import { MatSnackBar } from '@angular/material/snack-bar';
import { Store } from '@ngrx/store';
import { Observable, of, startWith, timer } from 'rxjs';
import { map, switchMap } from 'rxjs/operators';
import { defined } from '../../operators/non-empty';
import { surveyFormFeature } from '../survey-form/state/survey-form.feature';
import { Survey } from '../survey.model';

@Component({
  selector: 'app-survey-view',
  templateUrl: './survey-owner-view.component.html',
  styleUrls: ['./survey-owner-view.component.css']
})
export class SurveyOwnerViewComponent {
  public readonly survey$: Observable<Survey>;
  public readonly expired$: Observable<boolean>;

  constructor(
    private readonly store: Store,
    protected readonly clipboard: Clipboard,
    protected readonly snackBars: MatSnackBar
  ) {
    this.survey$ = store.select(surveyFormFeature.selectSurvey).pipe(defined);
    this.expired$ = this.survey$.pipe(
      switchMap(survey => {
        if(survey.endDate >= new Date()) {
          return timer(survey.endDate).pipe(
            map(() => true),
            startWith(false),
          )
        } else {
          return of(true);
        }
      }),
    );
  }
}