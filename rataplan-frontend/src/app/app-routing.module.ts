import { NgModule } from '@angular/core';
import { RouterModule, Routes } from '@angular/router';

import { AppointmentComponent } from './appointment/appointment/appointment.component';
import { AppointmentRequestFormComponent } from './appointment/appointment-request-form/appointment-request-form.component';
import { ConfigSubformComponent } from './appointment/appointment-request-form/config-subform/config-subform.component';
import { DatepickerSubformComponent } from './appointment/appointment-request-form/datepicker-subform/datepicker-subform.component';
import { EmailSubformComponent } from './appointment/appointment-request-form/email-subform/email-subform.component';
import { GeneralSubformComponent } from './appointment/appointment-request-form/general-subform/general-subform.component';
import { LinkSubformComponent } from './appointment/appointment-request-form/link-subform/link-subform.component';
import { OverviewSubformComponent } from './appointment/appointment-request-form/overview-subform/overview-subform.component';
import { AppointmentRequestAuthGuard } from './appointment/auth-guard/appointment-request-auth-guard.service';
import { ChangePasswordComponent } from './change-password/change-password.component';
import { DeleteProfileComponent } from './delete-profile/delete-profile.component';
import { ForgotPasswordComponent } from './forgot-password/forgot-password.component';
import { HomepageComponent } from './homepage/homepage.component';
import { ConditionsComponent } from './legals/conditions/conditions.component';
import { ContactComponent } from './legals/contact/contact.component';
import { ImprintComponent } from './legals/imprint/imprint.component';
import { PrivacyComponent } from './legals/privacy/privacy.component';
import { LoginComponent } from './login/login.component';
import { EditProfileComponent } from './profile/edit-profile.component';
import { RegisterComponent } from './register/register.component';
import { ResetPasswordComponent } from './reset-password/reset-password.component';
import { AuthGuardService } from './services/auth-guard-service/auth-guard-service';
import { ProfilePasswordAuthService } from './services/auth-guard-service/profile-password-auth-service';
import { ViewProfileComponent } from './view-profile/view-profile.component';
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
    // matcher: matcherFunction,  component: AppointmentRequestFormComponent,
    path: 'create-vote', component: AppointmentRequestFormComponent,
    children: [
      { path: '', redirectTo: 'general', pathMatch: 'full' },
      { path: 'general', component: GeneralSubformComponent },
      { path: 'datepicker', component: DatepickerSubformComponent, canActivate: [AppointmentRequestAuthGuard] },
      { path: 'configurationOptions', component: ConfigSubformComponent, canActivate: [AppointmentRequestAuthGuard] },
      { path: 'configuration', component: OverviewSubformComponent, canActivate: [AppointmentRequestAuthGuard] },
      { path: 'email', component: EmailSubformComponent, canActivate: [AppointmentRequestAuthGuard] },
      { path: '**', redirectTo: 'general', pathMatch: 'full' },
    ],
  },
  { path: 'vote/links', component: LinkSubformComponent },
  { path: 'vote/own', component: VoteListComponent },
  { path: 'vote/:id', component: AppointmentComponent },
  {
    path: 'vote/edit/:id', component: AppointmentRequestFormComponent,
    children: [
      { path: '', redirectTo: 'general', pathMatch: 'full' },
      { path: 'general', component: GeneralSubformComponent },
      { path: 'datepicker', component: DatepickerSubformComponent },
      { path: 'configurationOptions', component: ConfigSubformComponent },
      { path: 'configuration', component: OverviewSubformComponent },
      { path: 'email', component: EmailSubformComponent },
      { path: '**', redirectTo: 'general', pathMatch: 'full' },
    ],
  },

  { path: 'terms-and-conditions', component: ConditionsComponent },
  { path: 'imprint', component: ImprintComponent },
  { path: 'privacy', component: PrivacyComponent },
  { path: 'contact', component: ContactComponent },
  { path: 'register', component: RegisterComponent },
  { path: 'login', component: LoginComponent, canActivate: [AuthGuardService], },
  { path: 'forgot-password', component: ForgotPasswordComponent },
  { path: 'view-profile', component: ViewProfileComponent, canActivate: [ProfilePasswordAuthService] },
  { path: 'edit-profile', component: EditProfileComponent, canActivate: [ProfilePasswordAuthService] },
  { path: 'reset-password', component: ResetPasswordComponent },
  { path: 'change-password', component: ChangePasswordComponent, canActivate: [ProfilePasswordAuthService] },
  { path: 'delete-profile', component: DeleteProfileComponent, canActivate: [ProfilePasswordAuthService] },
  { path: 'survey', loadChildren: () => import('./survey/survey.module').then(m => m.SurveyModule) },
];

@NgModule({
  imports: [RouterModule.forRoot(routes)],
  exports: [RouterModule]
})
export class AppRoutingModule {
}
