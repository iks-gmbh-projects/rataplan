import { Component, ViewChild } from '@angular/core';
import { Store } from '@ngrx/store';
import {Observable } from 'rxjs';
import { ActivatedRoute } from '@angular/router';
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
  editing: boolean = false;
  
  @ViewChild('headForm') headForm?: SurveyCreateFormHeadComponent;
  @ViewChild('pageForm') pageForm?: SurveyCreateFormPageComponent;
  
  constructor(
    private readonly store: Store,
    public readonly errorMessageService: FormErrorMessageService,
    private route: ActivatedRoute,
  )
  {
    this.headPage$ = store.select(surveyCreateFeature.selectHeadPage);
    this.valid$ = store.select(surveyCreateFeature.selectAllValid);
  }
  
  ngOnInit() {
    const url = this.route.snapshot.url.map(segment => segment.path).join('/');
    this.editing = (
      url.includes('edit')
    );
  }
  
  public submit(preview: boolean = false): void {
    this.headForm?.submit();
    this.pageForm?.submit();
    this.store.dispatch(preview ? surveyCreateActions.preview() : surveyCreateActions.postSurvey());
  }
}