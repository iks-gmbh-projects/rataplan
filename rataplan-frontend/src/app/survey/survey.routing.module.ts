import { NgModule } from '@angular/core';
import { SurveyListComponent } from './survey-list/survey-list.component';
import { SurveyFormComponent } from './survey-form/survey-form.component';
import { SurveyCreateFormComponent } from './survey-create-form/survey-create-form.component';
import { RouterModule, Routes } from '@angular/router';
import { SurveyOwnerViewComponent } from './survey-owner-view/survey-owner-view.component';
import { SurveyResultsComponent } from './survey-results/survey-results.component';
import { AccessIDSurveyResolver, ParticipationIDSurveyResolver } from './resolver/survey.resolver';

const surveyRoutes: Routes = [
  { path: "", pathMatch: "exact", component: SurveyListComponent },
  {
    path: "access", children: [
      {
        path: ":accessID", resolve: {survey: AccessIDSurveyResolver}, children: [
          { path: "", pathMatch: "exact", component: SurveyOwnerViewComponent },
          { path: "results", component: SurveyResultsComponent },
          { path: "edit", component: SurveyCreateFormComponent },
        ]
      },
    ]
  },
  { path: "create", component: SurveyCreateFormComponent },
  { path: "participate/:participationID", resolve: {survey: ParticipationIDSurveyResolver}, component: SurveyFormComponent },
];

@NgModule({
  imports: [
    RouterModule.forChild(surveyRoutes),
  ],
  exports: [
    RouterModule
  ]
})
export class SurveyRoutingModule { }
