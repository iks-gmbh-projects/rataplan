import { NgModule } from '@angular/core';
import { CommonModule } from '@angular/common';
import { SurveyListComponent } from './survey-list/survey-list.component';
import { SurveyFormComponent } from './survey-form/survey-form.component';
import { SurveyCreateFormComponent } from './survey-create-form/survey-create-form.component';
import { SurveyRoutingModule } from './survey.routing.module';
import { SurveyOwnerViewComponent } from './survey-owner-view/survey-owner-view.component';
import { SurveyResultsComponent } from './survey-results/survey-results.component';
import { FormsModule, ReactiveFormsModule } from '@angular/forms';
import { HttpClientModule } from '@angular/common/http';
import { SurveyService } from './survey.service';
import { AccessIDSurveyResolver, ParticipationIDSurveyResolver } from './resolver/survey.resolver';
import { SurveyClosedComponent } from './survey-closed/survey-closed.component';
import { MatButtonModule } from '@angular/material/button';
import { MatInputModule } from '@angular/material/input';
import { MatDatepickerModule } from '@angular/material/datepicker';
import { MatCardModule } from '@angular/material/card';
import { MatIconModule } from '@angular/material/icon';
import { MatProgressSpinnerModule } from '@angular/material/progress-spinner';
import { MatTableModule } from '@angular/material/table';
import { MatCheckboxModule } from '@angular/material/checkbox';
import { MatDividerModule } from '@angular/material/divider';
import { PageComponent } from './survey-form/page/page.component';
import { MatRadioModule } from '@angular/material/radio';
import { DateRangeComponent } from './date-range/date-range.component';
import { MatSortModule } from '@angular/material/sort';

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
  ],
  providers: [
    SurveyService,
    AccessIDSurveyResolver,
    ParticipationIDSurveyResolver,
  ]
})
export class SurveyModule { }
