import { Component } from '@angular/core';
import { Store } from '@ngrx/store';
import { delay, Observable, of, switchMap } from 'rxjs';
import { defined } from '../../../operators/non-empty';
import { surveyFormFeature } from '../../survey-form/state/survey-form.feature';
import { Survey } from '../../survey.model';
import { surveyCreateActions } from '../state/survey-create.action';

@Component({
  selector: 'app-survey-preview',
  templateUrl: './survey-preview.component.html',
  styleUrls: ['./survey-preview.component.css']
})
export class SurveyPreviewComponent {
  public readonly survey$: Observable<Survey>;
  public readonly page$: Observable<number>;
  public readonly busy$: Observable<boolean>;
  public readonly delayedBusy$: Observable<boolean>;

  constructor(
    private readonly store: Store,
  ) {
    this.survey$ = this.store.select(surveyFormFeature.selectSurvey).pipe(defined);
    this.page$ = this.store.select(surveyFormFeature.selectPage);
    this.busy$ = this.store.select(surveyFormFeature.selectBusy);
    this.delayedBusy$ = this.busy$.pipe(
      switchMap(v => v ? of(v).pipe(delay(1000)) : of(v)),
    )
  }
  
  back(): void {
    this.store.dispatch(surveyCreateActions.endPreview());
  }
  
  submit(): void {
    this.store.dispatch(surveyCreateActions.postSurvey());
  }
}