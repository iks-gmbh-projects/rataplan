import { NgModule } from '@angular/core';
import { LetDirective, PushPipe } from '@ngrx/component';
import { EffectsModule } from '@ngrx/effects';
import { StoreModule } from '@ngrx/store';
import { NgChartsModule } from 'ng2-charts';

import { AppCommonModule } from '../app-common.module';
import { DateRangeComponent } from './date-range/date-range.component';
import { SurveyClosedComponent } from './survey-closed/survey-closed.component';
import { SurveyCreateEffects } from './survey-create/state/survey-create.effects';
import { surveyCreateFeature } from './survey-create/state/survey-create.feature';
import { SurveyCreateFormHeadComponent } from './survey-create/survey-create-form/survey-create-form-head/survey-create-form-head.component';
import { SurveyCreateFormPageComponent } from './survey-create/survey-create-form/survey-create-form-page/survey-create-form-page.component';
import { SurveyCreateFormComponent } from './survey-create/survey-create-form/survey-create-form.component';
import { SurveyCreateComponent } from './survey-create/survey-create.component';
import { SurveyPreviewComponent } from './survey-create/survey-preview/survey-preview.component';
import { SurveyForbiddenComponent } from './survey-forbidden/survey-forbidden.component';
import { PageComponent } from './survey-form/page/page.component';
import { SurveyFormEffects } from './survey-form/state/survey-form.effects';
import { surveyFormFeature } from './survey-form/state/survey-form.feature';
import { SurveyAnswerComponent } from './survey-form/survey-answer/survey-answer.component';
import { SurveyFormComponent } from './survey-form/survey-form.component';
import { SurveyListEffects } from './survey-list/state/survey-list.effects';
import { surveyListFeature } from './survey-list/state/survey-list.feature';
import { SurveyListComponent } from './survey-list/survey-list.component';
import { SurveyMissingComponent } from './survey-missing/survey-missing.component';
import { SurveyOwnerViewComponent } from './survey-owner-view/survey-owner-view.component';
import { SurveyResultsEffects } from './survey-results/state/survey-results.effects';
import { surveyResultsFeature } from './survey-results/state/survey-results.feature';
import { SurveyResultsComponent } from './survey-results/survey-results.component';
import { SurveyUnknownErrorComponent } from './survey-unknown-error/survey-unknown-error.component';
import { SurveyRoutingModule } from './survey.routing.module';

@NgModule({
  declarations: [
    SurveyListComponent,
    SurveyFormComponent,
    SurveyOwnerViewComponent,
    SurveyCreateFormComponent,
    SurveyResultsComponent,
    SurveyClosedComponent,
    PageComponent,
    DateRangeComponent,
    SurveyPreviewComponent,
    SurveyCreateComponent,
    SurveyMissingComponent,
    SurveyForbiddenComponent,
    SurveyAnswerComponent,
    SurveyUnknownErrorComponent,
    SurveyCreateFormHeadComponent,
    SurveyCreateFormPageComponent,
  ],
  imports: [
    AppCommonModule,
    SurveyRoutingModule,
    NgChartsModule,
    LetDirective,
    PushPipe,
    StoreModule.forFeature(surveyListFeature),
    StoreModule.forFeature(surveyFormFeature),
    StoreModule.forFeature(surveyCreateFeature),
    StoreModule.forFeature(surveyResultsFeature),
    EffectsModule.forFeature([SurveyListEffects, SurveyFormEffects, SurveyCreateEffects, SurveyResultsEffects]),
  ],
})
export class SurveyModule {}