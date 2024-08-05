import { NgModule } from '@angular/core';
import { RouterModule, Routes } from '@angular/router';
import { ensureLoggedIn } from '../authentication/auth.guard';

import { SurveyClosedComponent } from './survey-closed/survey-closed.component';
import { SurveyCreateComponent } from './survey-create/survey-create.component';
import { SurveyForbiddenComponent } from './survey-forbidden/survey-forbidden.component';
import { SurveyFormComponent } from './survey-form/survey-form.component';
import { SurveyListComponent } from './survey-list/survey-list.component';
import { SurveyMissingComponent } from './survey-missing/survey-missing.component';
import { SurveyOwnerViewComponent } from './survey-owner-view/survey-owner-view.component';
import { SurveyResultsComponent } from './survey-results/survey-results.component';
import { SurveyUnknownErrorComponent } from './survey-unknown-error/survey-unknown-error.component';

const surveyRoutes: Routes = [{
  path: '', children: [
    {
      path: 'access', children: [
        {
          path: ':accessID', children: [
            { path: '', pathMatch: 'full', component: SurveyOwnerViewComponent },
            { path: 'results', component: SurveyResultsComponent },
            { path: 'edit', component: SurveyCreateComponent },
          ],
        },
      ],
    },
    { path: 'create', component: SurveyCreateComponent },
    { path: 'list', data: { own: false }, component: SurveyListComponent },
    { path: 'own', data: { own: true }, component: SurveyListComponent, canActivate: [ensureLoggedIn] },
    {
      path: 'participate/:participationID',
      component: SurveyFormComponent,
    },
    { path: 'missing', component: SurveyMissingComponent },
    { path: 'forbidden', component: SurveyForbiddenComponent },
    { path: 'unknown', component: SurveyUnknownErrorComponent },
    { path: 'closed', component: SurveyClosedComponent },
    { path: '', pathMatch: 'full', redirectTo: 'list' },
  ],
}];

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