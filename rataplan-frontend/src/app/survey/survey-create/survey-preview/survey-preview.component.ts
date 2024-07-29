import { Component, EventEmitter, Input, Output } from '@angular/core';
import { Store } from '@ngrx/store';
import { delay, NEVER, Observable, of, switchMap } from 'rxjs';
import { defined } from '../../../operators/non-empty';
import { surveyFormFeature } from '../../survey-form/state/survey-form.feature';
import { Survey } from '../../survey.model';

@Component({
  selector: 'app-survey-preview',
  templateUrl: './survey-preview.component.html',
  styleUrls: ['./survey-preview.component.css']
})
export class SurveyPreviewComponent {
  public readonly survey$: Observable<Survey>;
  public readonly page$: Observable<number>;
  @Output() public readonly onSubmit = new EventEmitter<Survey>();
  private _busy: Observable<boolean> = NEVER;
  private _delayedBusy: Observable<boolean> = NEVER;
  public get delayedBusy$(): Observable<boolean> {
    return this._delayedBusy;
  }
  public get busy$(): Observable<boolean> {
    return this._busy;
  }
  @Input("busy") public set busy$(value: Observable<boolean>) {
    this._busy = value;
    this._delayedBusy = value.pipe(
      switchMap(v => v ? of(v).pipe(delay(1000)) : of(v)),
    );
  }

  constructor(
    private readonly store: Store,
  ) {
    this.survey$ = this.store.select(surveyFormFeature.selectSurvey).pipe(defined);
    this.page$ = this.store.select(surveyFormFeature.selectPage);
  }
}