import { NgModule } from '@angular/core';
import { RouterModule, Routes } from '@angular/router';
import { ensureLoggedIn } from '../authentication/auth.guard';
import { VoteListComponent } from '../vote-list/vote-list.component';
import { ConfigSubformComponent } from './vote-form/config-subform/config-subform.component';
import { DatepickerSubformComponent } from './vote-form/datepicker-subform/datepicker-subform.component';
import { EmailSubformComponent } from './vote-form/email-subform/email-subform.component';
import { GeneralSubformComponent } from './vote-form/general-subform/general-subform.component';
import { LinkSubformComponent } from './vote-form/link-subform/link-subform.component';
import { OverviewSubformComponent } from './vote-form/overview-subform/overview-subform.component';
import { VoteFormComponent } from './vote-form/vote-form.component';
import { VoteResultsComponent } from './vote-results/vote-results.component';
import { redirectIncompleteToGeneral } from './vote.guards';
import { VoteComponent } from './vote/vote.component';

const routes: Routes = [
  {
    // matcher: matcherFunction,  component: VoteFormComponent,
    path: 'create', component: VoteFormComponent,
    children: [
      {path: 'general', component: GeneralSubformComponent},
      {path: 'datepicker', component: DatepickerSubformComponent, canActivate: [redirectIncompleteToGeneral]},
      {path: 'configurationOptions', component: ConfigSubformComponent, canActivate: [redirectIncompleteToGeneral]},
      {path: 'configuration', component: OverviewSubformComponent, canActivate: [redirectIncompleteToGeneral]},
      {path: 'email', component: EmailSubformComponent, canActivate: [redirectIncompleteToGeneral]},
      {
        path: 'preview',
        component: VoteComponent,
        canActivate: [redirectIncompleteToGeneral],
      },
      {path: '**', redirectTo: 'general'},
    ],
  },
  {path: 'links', component: LinkSubformComponent},
  {path: 'own', component: VoteListComponent, canActivate: [ensureLoggedIn]},
    {
        path: 'edit/:id', component: VoteFormComponent,
        children: [
            {path: 'general', component: GeneralSubformComponent},
            {path: 'datepicker', component: DatepickerSubformComponent},
      {path: 'configuration', component: OverviewSubformComponent},
      {path: 'email', component: EmailSubformComponent},
      {
        path: 'preview',
        component: VoteComponent,
      },
      {path: '**', redirectTo: 'general'},
    ],
  },
  {
    path: ':id',
    data: {isPreview: false, loadVote: true},
    component: VoteComponent,
  }, {
    path: ':id/results', data: {loadVote: true}, component: VoteResultsComponent,
  },

];

@NgModule({
  imports: [RouterModule.forChild(routes)],
  exports: [RouterModule],
})
export class VoteRoutingModule {
}