import {NgModule} from '@angular/core';
import {RouterModule, Routes} from '@angular/router';

import {VoteAuthGuard} from './auth-guard/vote-auth-guard.service';
import {VoteResolver} from './vote/resolver/vote.resolver';
import {VotePreviewResolver} from './vote/resolver/vote-preview.resolver';
import {VoteComponent} from './vote/vote.component';
import {ConfigSubformComponent} from './vote-form/config-subform/config-subform.component';
import {DatepickerSubformComponent} from './vote-form/datepicker-subform/datepicker-subform.component';
import {EmailSubformComponent} from './vote-form/email-subform/email-subform.component';
import {GeneralSubformComponent} from './vote-form/general-subform/general-subform.component';
import {LinkSubformComponent} from './vote-form/link-subform/link-subform.component';
import {OverviewSubformComponent} from './vote-form/overview-subform/overview-subform.component';
import {VoteFormComponent} from './vote-form/vote-form.component';
import {VoteListComponent} from '../vote-list/vote-list.component';
import {ProfilePasswordAuthService} from '../services/auth-guard-service/profile-password-auth-service';
import {VoteResultsComponent} from "./vote-results/vote-results.component";
import {VoteResultsResolver} from "./vote/resolver/vote-results.resolver";

// function matcherFunction(url: UrlSegment[]) {
//
//   const path = url[0].path;
//   if(path.startsWith('create-vote')) {
//     console.log(url);
//     return { consumed: url.slice(0,1) };
//   }
//   if (path.startsWith('edit')) {
//     return { consumed: url.slice(0,1) };
//   }
//
//   return null;
// }

const routes: Routes = [
    {
        // matcher: matcherFunction,  component: VoteFormComponent,
        path: 'create', component: VoteFormComponent,
        children: [
            {path: 'general', component: GeneralSubformComponent},
            {path: 'datepicker', component: DatepickerSubformComponent, canActivate: [VoteAuthGuard]},
            {path: 'configurationOptions', component: ConfigSubformComponent, canActivate: [VoteAuthGuard]},
            {path: 'configuration', component: OverviewSubformComponent, canActivate: [VoteAuthGuard]},
            {path: 'email', component: EmailSubformComponent, canActivate: [VoteAuthGuard]},
            {
                path: 'preview',
                data: {isPreview: true},
                resolve: {vote: VotePreviewResolver},
                component: VoteComponent,
                canActivate: [VoteAuthGuard],
            },
            {path: '**', redirectTo: 'general'},
        ],
    },
    {path: 'links', component: LinkSubformComponent},
    {path: 'own', component: VoteListComponent, canActivate: [ProfilePasswordAuthService]},
    {
        path: 'edit/:id', component: VoteFormComponent,
        children: [
            {path: 'general', component: GeneralSubformComponent},
            {path: 'datepicker', component: DatepickerSubformComponent},
            {path: 'configurationOptions', component: ConfigSubformComponent},
            {path: 'configuration', component: OverviewSubformComponent},
            {path: 'email', component: EmailSubformComponent},
            {
                path: 'preview',
                data: {isPreview: true},
                resolve: {vote: VotePreviewResolver},
                component: VoteComponent,
            },
            {path: '**', redirectTo: 'general'},
        ],
    },
    {
        path: ':id',
        data: {isPreview: false},
        resolve: {vote: VoteResolver},
        component: VoteComponent,
    }, {path: ':id/results', component: VoteResultsComponent, resolve: {
        voteResultData: VoteResultsResolver
    }}

];

@NgModule({
    imports: [RouterModule.forChild(routes)],
    exports: [RouterModule],
})
export class VoteRoutingModule {
}
