import { Component, OnDestroy, OnInit } from '@angular/core';
import { ActivatedRoute } from '@angular/router';
import { Store } from '@ngrx/store';
import { Observable, Subscription } from 'rxjs';
import { map } from 'rxjs/operators';
import { Survey } from '../survey.model';
import { surveyCreateActions } from './state/survey-create.action';
import { surveyCreateFeature } from './state/survey-create.feature';

@Component({
  selector: 'app-survey-create',
  templateUrl: './survey-create.component.html',
  styleUrls: ['./survey-create.component.css']
})
export class SurveyCreateComponent implements OnInit, OnDestroy {
  public preview$: Observable<boolean>;
  private sub?: Subscription;

  constructor(
    private readonly store: Store,
    private readonly route: ActivatedRoute,
  ) {
    this.preview$ = this.store.select(surveyCreateFeature.selectShowPreview);
  }
  
  public ngOnInit(): void {
    this.sub?.unsubscribe();
    this.sub = this.route.data.pipe(
      map(data => data['survey'] as Survey | undefined),
    ).subscribe(survey => this.store.dispatch(survey ? surveyCreateActions.editSurveyLoaded({survey}) : surveyCreateActions.newSurvey()));
  }
  
  public ngOnDestroy(): void {
    this.sub?.unsubscribe();
    this.sub = undefined;
  }
}