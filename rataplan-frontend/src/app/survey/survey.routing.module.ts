import { NgModule } from '@angular/core';
import { SurveyListComponent } from './survey-list/survey-list.component';
import { SurveyFormComponent } from './survey-form/survey-form.component';
import { SurveyCreateFormComponent } from './survey-create-form/survey-create-form.component';
import { RouterModule, Routes } from '@angular/router';
import { SurveyViewComponent } from './survey-view/survey-view.component';
import { SurveyResultsComponent } from './survey-results/survey-results.component';
import { AccessIDSurveyResolver, ParticipationIDSurveyResolver } from './resolver/survey.resolver';

const surveyRoutes: Routes = [
  { path: "", pathMatch: "exact", component: SurveyListComponent },
  {
    path: "access", children: [
      {
        path: ":accessID", resolve: {survey: AccessIDSurveyResolver}, children: [
          { path: "", pathMatch: "exact", component: SurveyViewComponent },
          { path: "results", component: SurveyResultsComponent },
          { path: "edit", component: SurveyCreateFormComponent },
        ]
      },
      { path: "create", component: SurveyCreateFormComponent }
    ]
  },
  { path: ":participationID", resolve: {survey: ParticipationIDSurveyResolver}, component: SurveyFormComponent },
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
