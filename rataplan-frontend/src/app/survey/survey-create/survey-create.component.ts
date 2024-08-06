import { Component } from '@angular/core';
import { Store } from '@ngrx/store';
import { Observable } from 'rxjs';
import { surveyCreateFeature } from './state/survey-create.feature';

@Component({
  selector: 'app-survey-create',
  templateUrl: './survey-create.component.html',
  styleUrls: ['./survey-create.component.css']
})
export class SurveyCreateComponent {
  public preview$: Observable<boolean>;

  constructor(
    private readonly store: Store,
  ) {
    this.preview$ = this.store.select(surveyCreateFeature.selectShowPreview);
  }
}