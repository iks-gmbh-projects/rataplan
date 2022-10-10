import { NgModule } from '@angular/core';
import { RouterModule, Routes } from '@angular/router';
import { AccessIDSurveyResolver, ParticipationIDSurveyResolver } from './resolver/survey.resolver';
import { SurveyClosedComponent } from './survey-closed/survey-closed.component';
import { SurveyCreateComponent } from './survey-create/survey-create.component';
import { SurveyForbiddenComponent } from './survey-forbidden/survey-forbidden.component';
import { SurveyFormComponent } from './survey-form/survey-form.component';
import { SurveyListComponent } from './survey-list/survey-list.component';
import { SurveyMissingComponent } from './survey-missing/survey-missing.component';
import { SurveyOwnerViewComponent } from './survey-owner-view/survey-owner-view.component';
import { SurveyResultsComponent } from './survey-results/survey-results.component';

const surveyRoutes: Routes = [{
  path: "", children: [
    {
      path: "access", children: [
        {
          path: ":accessID", resolve: { survey: AccessIDSurveyResolver }, runGuardsAndResolvers: "always", children: [
            { path: "", pathMatch: "exact", component: SurveyOwnerViewComponent },
            { path: "results", component: SurveyResultsComponent },
            { path: "edit", component: SurveyCreateComponent },
          ]
        },
      ]
    },
    { path: "create", component: SurveyCreateComponent },
    { path: "list", data: { own: false }, component: SurveyListComponent },
    { path: "own", data: { own: true }, component: SurveyListComponent },
    { path: "participate/:participationID", resolve: { survey: ParticipationIDSurveyResolver }, component: SurveyFormComponent },
    { path: "missing", component: SurveyMissingComponent },
    { path: "forbidden", component: SurveyForbiddenComponent },
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
