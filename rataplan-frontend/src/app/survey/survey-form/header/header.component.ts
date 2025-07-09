import { Component } from '@angular/core';
import { Store } from '@ngrx/store';
import { Observable } from 'rxjs';
import { defined } from '../../../operators/non-empty';
import { Survey } from '../../survey.model';
import { surveyFormFeature } from '../state/survey-form.feature';


@Component({
  selector: 'app-survey-form-header',
  templateUrl: './header.component.html',
  styleUrl: './header.component.css'
})
export class HeaderComponent {
  public survey$: Observable<Survey>;
  
  constructor(
    private readonly store: Store,
  ) {
    this.survey$ = this.store.select(surveyFormFeature.selectSurvey).pipe(defined);
  }
}

