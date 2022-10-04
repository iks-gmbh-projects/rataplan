import { ClipboardModule } from '@angular/cdk/clipboard';
import { CommonModule } from '@angular/common';
import { HttpClientModule } from '@angular/common/http';
import { NgModule } from '@angular/core';
import { FormsModule, ReactiveFormsModule } from '@angular/forms';
import { MatButtonModule } from '@angular/material/button';
import { MatCardModule } from '@angular/material/card';
import { MatCheckboxModule } from '@angular/material/checkbox';
import { MatDatepickerModule } from '@angular/material/datepicker';
import { MatDividerModule } from '@angular/material/divider';
import { MatIconModule } from '@angular/material/icon';
import { MatInputModule } from '@angular/material/input';
import { MatProgressSpinnerModule } from '@angular/material/progress-spinner';
import { MatRadioModule } from '@angular/material/radio';
import { MatSortModule } from '@angular/material/sort';
import { MatTableModule } from '@angular/material/table';
import { DateRangeComponent } from './date-range/date-range.component';
import { AccessIDSurveyResolver, ParticipationIDSurveyResolver } from './resolver/survey.resolver';
import { SurveyClosedComponent } from './survey-closed/survey-closed.component';
import { SurveyCreateFormComponent } from './survey-create-form/survey-create-form.component';
import { PageComponent } from './survey-form/page/page.component';
import { SurveyFormComponent } from './survey-form/survey-form.component';
import { SurveyListComponent } from './survey-list/survey-list.component';
import { SurveyOwnerViewComponent } from './survey-owner-view/survey-owner-view.component';
import { SurveyResultsComponent } from './survey-results/survey-results.component';
import { SurveyRoutingModule } from './survey.routing.module';
import { SurveyService } from './survey.service';

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
  ],
  imports: [
    CommonModule,
    ReactiveFormsModule,
    FormsModule,
    HttpClientModule,
    SurveyRoutingModule,
    MatButtonModule,
    MatInputModule,
    MatDatepickerModule,
    MatCardModule,
    MatIconModule,
    MatProgressSpinnerModule,
    MatTableModule,
    MatSortModule,
    MatCheckboxModule,
    MatRadioModule,
    MatDividerModule,
    ClipboardModule,
  ],
  providers: [
    SurveyService,
    AccessIDSurveyResolver,
    ParticipationIDSurveyResolver,
  ]
})
export class SurveyModule { }
