import { NgModule } from '@angular/core';
import { RouterModule, Routes } from '@angular/router';
import { ensureLoggedIn } from '../authentication/auth.guard';

import { SurveyClosedComponent } from './survey-closed/survey-closed.component';
import { SurveyCreateComponent } from './survey-create/survey-create.component';
import { SurveyForbiddenComponent } from './survey-forbidden/survey-forbidden.component';
import { SurveyFormComponent } from './survey-form/survey-form.component';
import { SurveyListType } from './survey-list/state/survey-list.action';
import { SurveyListComponent } from './survey-list/survey-list.component';
import { SurveyMissingComponent } from './survey-missing/survey-missing.component';
import { SurveyOwnerViewComponent } from './survey-owner-view/survey-owner-view.component';
import { SurveyResultsComponent } from './survey-results/survey-results.component';
import { SurveyUnknownErrorComponent } from './survey-unknown-error/survey-unknown-error.component';

const surveyRoutes: Routes = [
  {
    path: '', children: [
      {
        path: 'access', children: [
          {
            path: ':accessID', children: [
              {path: '', pathMatch: 'full', data: {loadSurveyForm: true}, component: SurveyOwnerViewComponent},
              {path: 'results', data: {loadSurveyResults: true}, component: SurveyResultsComponent},
              {path: 'edit', data: {loadSurveyEdit: true}, component: SurveyCreateComponent},
            ],
          },
        ],
      },
      {path: 'create', data: {loadSurveyEdit: true}, component: SurveyCreateComponent},
      {path: 'list', data: {surveyListType: SurveyListType.PUBLIC}, component: SurveyListComponent},
      {path: 'own', data: {surveyListType: SurveyListType.OWN}, component: SurveyListComponent, canActivate: [ensureLoggedIn]},
      {
        path: 'participate/:participationID',
        data: {loadSurveyForm: true},
        component: SurveyFormComponent,
      },
      {path: 'missing', component: SurveyMissingComponent},
      {path: 'forbidden', component: SurveyForbiddenComponent},
      {path: 'unknown', component: SurveyUnknownErrorComponent},
      {path: 'closed', component: SurveyClosedComponent},
      {path: '', pathMatch: 'full', redirectTo: 'list'},
    ],
  },
];

@NgModule({
  imports: [
    RouterModule.forChild(surveyRoutes),
  ],
  exports: [
    RouterModule,
  ],
})
export class SurveyRoutingModule {
}