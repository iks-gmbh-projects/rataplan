import { NgModule } from '@angular/core';
import { SurveyListComponent } from './survey-list/survey-list.component';
import { SurveyFormComponent } from './survey-form/survey-form.component';
import { SurveyCreateFormComponent } from './survey-create-form/survey-create-form.component';
import { RouterModule, Routes } from '@angular/router';
import { SurveyOwnerViewComponent } from './survey-owner-view/survey-owner-view.component';
import { SurveyResultsComponent } from './survey-results/survey-results.component';
import { AccessIDSurveyResolver, ParticipationIDSurveyResolver } from './resolver/survey.resolver';
import { SurveyOverviewComponent } from './survey-list/survey-overview/survey-overview.component';
import { SurveyClosedComponent } from './survey-closed/survey-closed.component';

const surveyRoutes: Routes = [{
  path: "", children: [
    {
      path: "access", children: [
        {
          path: ":accessID", resolve: { survey: AccessIDSurveyResolver }, children: [
            { path: "", pathMatch: "exact", component: SurveyOwnerViewComponent },
            { path: "results", component: SurveyResultsComponent },
            { path: "edit", component: SurveyCreateFormComponent },
          ]
        },
      ]
    },
    { path: "create", component: SurveyCreateFormComponent },
    {
      path: "list", component: SurveyListComponent, children: [
        { path: ":participationID", resolve: { survey: ParticipationIDSurveyResolver }, component: SurveyOverviewComponent }
      ]
    },
    { path: "participate/:participationID", resolve: { survey: ParticipationIDSurveyResolver }, component: SurveyFormComponent },
    { path: "closed", component: SurveyClosedComponent },
    { path: "", pathMatch: "exact", redirectTo: "list" },
  ]
}];

@NgModule({
  imports: [
    RouterModule.forChild(surveyRoutes),
  ],
  exports: [
    RouterModule
  ]
})
export class SurveyRoutingModule { }
