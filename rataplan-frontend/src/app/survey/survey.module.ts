import { ClipboardModule } from '@angular/cdk/clipboard';
import { NgModule } from '@angular/core';
import { FormsModule, ReactiveFormsModule } from '@angular/forms';
import { MatButtonModule } from '@angular/material/button';
import { MatCardModule } from '@angular/material/card';
import { MatCheckboxModule } from '@angular/material/checkbox';
import { MatDatepickerModule } from '@angular/material/datepicker';
import { MatDividerModule } from '@angular/material/divider';
import { MatExpansionModule } from '@angular/material/expansion';
import { MatIconModule } from '@angular/material/icon';
import { MatInputModule } from '@angular/material/input';
import { MatProgressSpinnerModule } from '@angular/material/progress-spinner';
import { MatRadioModule } from '@angular/material/radio';
import { MatSortModule } from '@angular/material/sort';
import { MatStepperModule } from '@angular/material/stepper';
import { MatTableModule } from '@angular/material/table';
import { NgxMatDatetimePickerModule, NgxMatNativeDateModule, NgxMatTimepickerModule } from '@angular-material-components/datetime-picker';

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
  ],
  imports: [
    AppCommonModule,
    ReactiveFormsModule,
    FormsModule,
    SurveyRoutingModule,
    MatButtonModule,
    MatInputModule,
    MatDatepickerModule,
    MatCardModule,
    MatIconModule,
    MatProgressSpinnerModule,
    MatTableModule,
    MatExpansionModule,
    MatSortModule,
    MatCheckboxModule,
    MatRadioModule,
    MatDividerModule,
    MatStepperModule,
    ClipboardModule,
    NgxMatDatetimePickerModule,
    NgxMatTimepickerModule,
    NgxMatNativeDateModule,
  ],
  providers: [
    SurveyService,
    AccessIDSurveyResolver,
    ParticipationIDSurveyResolver,
  ]
})
export class SurveyModule { }
