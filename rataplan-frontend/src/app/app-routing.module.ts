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
import { ContactListComponent } from './contact-list/contact-list.component';
import { DeleteProfileComponent } from './delete-profile/delete-profile.component';
import { EditProfileComponent } from './edit-profile/edit-profile.component';
import { EmailNotificationSettingsComponent } from './email-notification-settings/email-notification-settings.component';
import { FeedbackComponent } from './feedback/feedback.component';
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
import { VersionComponent } from './version/version.component';
import { ViewProfileComponent } from './view-profile/view-profile.component';

const routes: Routes = [
  { path: '', component: HomepageComponent },
  { path: 'vote', loadChildren: () => import('./vote/vote.module').then(m => m.VoteModule) },
  { path: 'appointmentrequest/:id', redirectTo: '/vote/:id' },
  { path: 'terms-and-conditions', component: ConditionsComponent },
  { path: 'imprint', component: ImprintComponent },
  { path: 'privacy', component: PrivacyComponent },
  { path: 'contact', component: ContactComponent },
  { path: 'changes', component: VersionComponent },
  { path: 'feedback', component: FeedbackComponent },
  { path: 'register', component: RegisterComponent },
  { path: 'login', component: LoginComponent, canActivate: [AuthGuardService] },
  { path: 'forgot-password', component: ForgotPasswordComponent },
  { path: 'view-profile', component: ViewProfileComponent, canActivate: [ProfilePasswordAuthService] },
  { path: 'edit-profile', component: EditProfileComponent, canActivate: [ProfilePasswordAuthService] },
  { path: 'email-settings',  component: EmailNotificationSettingsComponent, canActivate: [ProfilePasswordAuthService] },
  { path: 'reset-password', component: ResetPasswordComponent },
  { path: 'change-password', component: ChangePasswordComponent, canActivate: [ProfilePasswordAuthService] },
  { path: 'delete-profile', component: DeleteProfileComponent, canActivate: [ProfilePasswordAuthService] },
  { path: 'contacts', component: ContactListComponent, canActivate: [ProfilePasswordAuthService] },
  { path: 'survey', loadChildren: () => import('./survey/survey.module').then(m => m.SurveyModule) },
  { path: 'confirm-account', component: ConfirmAccountInstructionComponent },
  { path: 'confirm-account/:token', component: ConfirmAccountComponent, canActivate: [ AuthGuardService ] },
  { path: 'resend-confirmation-email', component: ResendAccountConfirmationEmailComponent, canActivate: [ AuthGuardService ] },
  { path: '**', redirectTo: '' }
];

@NgModule({
  imports: [RouterModule.forRoot(routes)],
  exports: [RouterModule],
})
export class AppRoutingModule {
}
