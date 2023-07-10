import { NgModule } from '@angular/core';
import { NgxMatDatetimePickerModule, NgxMatNativeDateModule } from '@angular-material-components/datetime-picker';

import { AppCommonModule } from '../app-common.module';
import { DateRangeComponent } from './date-range/date-range.component';
import { AccessIDSurveyResolver, ParticipationIDSurveyResolver } from './resolver/survey.resolver';
import { SurveyRoutingModule } from './survey.routing.module';
import { SurveyService } from './survey.service';
import { SurveyClosedComponent } from './survey-closed/survey-closed.component';
import { SurveyCreateComponent } from './survey-create/survey-create.component';
import { SurveyCreateFormComponent } from './survey-create/survey-create-form/survey-create-form.component';
import { SurveyPreviewComponent } from './survey-create/survey-preview/survey-preview.component';
import { SurveyForbiddenComponent } from './survey-forbidden/survey-forbidden.component';
import { PageComponent } from './survey-form/page/page.component';
import { SurveyAnswerComponent } from './survey-form/survey-answer/survey-answer.component';
import { SurveyFormComponent } from './survey-form/survey-form.component';
import { SurveyListComponent } from './survey-list/survey-list.component';
import { SurveyMissingComponent } from './survey-missing/survey-missing.component';
import { SurveyOwnerViewComponent } from './survey-owner-view/survey-owner-view.component';
import { SurveyResultsComponent } from './survey-results/survey-results.component';
import { SurveyUnknownErrorComponent } from './survey-unknown-error/survey-unknown-error.component';

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
  ],
  imports: [
    AppCommonModule,
    SurveyRoutingModule,
    NgxMatDatetimePickerModule,
    NgxMatNativeDateModule,
  ],
  providers: [
    SurveyService,
    AccessIDSurveyResolver,
    ParticipationIDSurveyResolver,
  ]
})
export class SurveyModule { }
