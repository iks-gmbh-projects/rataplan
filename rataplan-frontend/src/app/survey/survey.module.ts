import { NgModule } from '@angular/core';
import { CommonModule } from '@angular/common';
import { SurveyListComponent } from './survey-list/survey-list.component';
import { SurveyFormComponent } from './survey-form/survey-form.component';
import { SurveyCreateFormComponent } from './survey-create-form/survey-create-form.component';
import { SurveyRoutingModule } from './survey.routing.module';
import { SurveyOwnerViewComponent } from './survey-owner-view/survey-owner-view.component';
import { SurveyResultsComponent } from './survey-results/survey-results.component';
import { ReactiveFormsModule } from '@angular/forms';
import { HttpClientModule } from '@angular/common/http';
import { SurveyService } from './survey.service';
import { AccessIDSurveyResolver, ParticipationIDSurveyResolver } from './resolver/survey.resolver';

@NgModule({
  declarations: [
    SurveyListComponent,
    SurveyFormComponent,
    SurveyOwnerViewComponent,
    SurveyCreateFormComponent,
    SurveyResultsComponent
  ],
  imports: [
    CommonModule,
    ReactiveFormsModule,
    HttpClientModule,
    SurveyRoutingModule,
  ],
  providers: [
    SurveyService,
    AccessIDSurveyResolver,
    ParticipationIDSurveyResolver,
  ]
})
export class SurveyModule { }
