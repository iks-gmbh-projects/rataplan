import { NgModule } from '@angular/core';
import { RouterModule, Routes } from '@angular/router';

import { ChangePasswordComponent } from './change-password/change-password.component';
import { ConfirmAccountComponent } from './confirm-account/confirm-account/confirm-account.component';
import {
  ConfirmAccountInstructionComponent
} from './confirm-account/confirm-account-instruction/confirm-account-instruction.component';
import {
  ResendAccountConfirmationEmailComponent
} from './confirm-account/resend-account-confirmation-email/resend-account-confirmation-email.component';
import { DeleteProfileComponent } from './delete-profile/delete-profile.component';
import { EditProfileComponent } from './edit-profile/edit-profile.component';
import { ForgotPasswordComponent } from './forgot-password/forgot-password.component';
import { HomepageComponent } from './homepage/homepage.component';
import { ConditionsComponent } from './legals/conditions/conditions.component';
import { ContactComponent } from './legals/contact/contact.component';
import { ImprintComponent } from './legals/imprint/imprint.component';
import { PrivacyComponent } from './legals/privacy/privacy.component';
import { LoginComponent } from './login/login.component';
import { RegisterComponent } from './register/register.component';
import { ResetPasswordComponent } from './reset-password/reset-password.component';
import { AuthGuardService } from './services/auth-guard-service/auth-guard-service';
import { ProfilePasswordAuthService } from './services/auth-guard-service/profile-password-auth-service';
import { ViewProfileComponent } from './view-profile/view-profile.component';
import { VoteAuthGuard } from './vote/auth-guard/vote-auth-guard.service';
import { VoteResolver } from './vote/vote/resolver/vote.resolver';
import { VotePreviewResolver } from './vote/vote/resolver/vote-preview.resolver';
import { VoteComponent } from './vote/vote/vote.component';
import { ConfigSubformComponent } from './vote/vote-form/config-subform/config-subform.component';
import { DatepickerSubformComponent } from './vote/vote-form/datepicker-subform/datepicker-subform.component';
import { EmailSubformComponent } from './vote/vote-form/email-subform/email-subform.component';
import { GeneralSubformComponent } from './vote/vote-form/general-subform/general-subform.component';
import { LinkSubformComponent } from './vote/vote-form/link-subform/link-subform.component';
import { OverviewSubformComponent } from './vote/vote-form/overview-subform/overview-subform.component';
import { VoteFormComponent } from './vote/vote-form/vote-form.component';
import { VoteListComponent } from './vote-list/vote-list.component';

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
  { path: '', component: HomepageComponent },
  {
    // matcher: matcherFunction,  component: VoteFormComponent,
    path: 'create-vote', component: VoteFormComponent,
    children: [
      { path: '', redirectTo: 'general', pathMatch: 'full' },
      { path: 'general', component: GeneralSubformComponent },
      { path: 'datepicker', component: DatepickerSubformComponent, canActivate: [VoteAuthGuard] },
      { path: 'configurationOptions', component: ConfigSubformComponent, canActivate: [VoteAuthGuard] },
      { path: 'configuration', component: OverviewSubformComponent, canActivate: [VoteAuthGuard] },
      { path: 'email', component: EmailSubformComponent, canActivate: [VoteAuthGuard] },
      {
        path: 'preview',
        data: { isPreview: true },
        resolve: { vote: VotePreviewResolver },
        component: VoteComponent,
        canActivate: [VoteAuthGuard],
      },
      { path: '**', redirectTo: 'general', pathMatch: 'full' },
    ],
  },
  { path: 'vote/links', component: LinkSubformComponent },
  { path: 'vote/own', component: VoteListComponent },
  {
    path: 'vote/:id',
    data: { isPreview: false },
    resolve: { vote: VoteResolver },
    component: VoteComponent,
  },
  {
    path: 'vote/edit/:id', component: VoteFormComponent,
    children: [
      { path: '', redirectTo: 'general', pathMatch: 'full' },
      { path: 'general', component: GeneralSubformComponent },
      { path: 'datepicker', component: DatepickerSubformComponent },
      { path: 'configurationOptions', component: ConfigSubformComponent },
      { path: 'configuration', component: OverviewSubformComponent },
      { path: 'email', component: EmailSubformComponent },
      {
        path: 'preview',
        data: { isPreview: true },
        resolve: { vote: VotePreviewResolver },
        component: VoteComponent,
      },
      { path: '**', redirectTo: 'general', pathMatch: 'full' },
    ],
  },
  { path: 'appointmentrequest/:id', redirectTo: '/vote/:id' },
  { path: 'terms-and-conditions', component: ConditionsComponent },
  { path: 'imprint', component: ImprintComponent },
  { path: 'privacy', component: PrivacyComponent },
  { path: 'contact', component: ContactComponent },
  { path: 'register', component: RegisterComponent },
  { path: 'login', component: LoginComponent, canActivate: [AuthGuardService] },
  { path: 'forgot-password', component: ForgotPasswordComponent },
  { path: 'view-profile', component: ViewProfileComponent, canActivate: [ProfilePasswordAuthService] },
  { path: 'edit-profile', component: EditProfileComponent, canActivate: [ProfilePasswordAuthService] },
  { path: 'reset-password', component: ResetPasswordComponent },
  { path: 'change-password', component: ChangePasswordComponent, canActivate: [ProfilePasswordAuthService] },
  { path: 'delete-profile', component: DeleteProfileComponent, canActivate: [ProfilePasswordAuthService] },
  { path: 'survey', loadChildren: () => import('./survey/survey.module').then(m => m.SurveyModule) },
  { path: 'confirm-account', component: ConfirmAccountInstructionComponent },
  { path: 'confirm-account/:token', component: ConfirmAccountComponent, canActivate: [ AuthGuardService ] },
  { path: 'resend-confirmation-email', component: ResendAccountConfirmationEmailComponent, canActivate: [ AuthGuardService ] }
];

@NgModule({
  imports: [RouterModule.forRoot(routes)],
  exports: [RouterModule],
})
export class AppRoutingModule {
}
