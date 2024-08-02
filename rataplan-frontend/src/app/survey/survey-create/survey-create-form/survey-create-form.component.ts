import { Component, ViewChild } from '@angular/core';
import { Store } from '@ngrx/store';
import { combineLatestAll, Observable, of } from 'rxjs';
import { defined } from '../../../operators/non-empty';
import { FormErrorMessageService } from '../../../services/form-error-message-service/form-error-message.service';
import { surveyCreateActions } from '../state/survey-create.action';
import { surveyCreateFeature } from '../state/survey-create.feature';
import { SurveyCreateFormHeadComponent } from './survey-create-form-head/survey-create-form-head.component';
import { SurveyCreateFormPageComponent } from './survey-create-form-page/survey-create-form-page.component';

@Component({
  selector: 'app-survey-create-form',
  templateUrl: './survey-create-form.component.html',
  styleUrls: ['./survey-create-form.component.css'],
})
export class SurveyCreateFormComponent {
  public readonly headPage$: Observable<boolean>;
  public readonly valid$: Observable<boolean>;
  
  @ViewChild('headForm') headForm?: SurveyCreateFormHeadComponent;
  @ViewChild('pageForm') pageForm?: SurveyCreateFormPageComponent;
  
  constructor(
    private readonly store: Store,
    public readonly errorMessageService: FormErrorMessageService,
  )
  {
    this.headPage$ = store.select(surveyCreateFeature.selectHeadPage);
    this.valid$ = store.select(surveyCreateFeature.selectAllValid);
  }
  
  public submit(preview: boolean = false): void {
    of(
      this.headForm?.submit(),
      this.pageForm?.submit(),
    ).pipe(
      defined,
      combineLatestAll(),
    ).subscribe(() => {
      this.store.dispatch(preview ? surveyCreateActions.preview() : surveyCreateActions.postSurvey());
    });
  }
}