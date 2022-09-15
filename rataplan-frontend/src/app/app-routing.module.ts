import { NgModule } from '@angular/core';
import { RouterModule, Routes } from '@angular/router';

import { AppointmentRequestAuthGuard } from './auth-guard/appointment-request-auth-guard.service';
import { AppointmentRequestFormComponent } from './components/appointment/appointment-request-form/appointment-request-form.component';
import { DateOverviewSubformComponent } from './components/appointment/appointment-request-form/date-overview-subform/date-overview-subform.component';
import { DatepickerSubformComponent } from './components/appointment/appointment-request-form/datepicker-subform/datepicker-subform.component';
import { EmailSubformComponent } from './components/appointment/appointment-request-form/email-subform/email-subform.component';
import { GeneralSubformComponent } from './components/appointment/appointment-request-form/general-subform/general-subform.component';
import { LinkSubformComponent } from './components/appointment/appointment-request-form/link-subform/link-subform.component';
import { ForgotPasswordComponent } from './components/forgot-password/forgot-password.component';
import { HomepageComponent } from './components/homepage/homepage.component';
import { ConditionsComponent } from './components/legals/conditions/conditions.component';
import { ContactComponent } from './components/legals/contact/contact.component';
import { ImprintComponent } from './components/legals/imprint/imprint.component';
import { PrivacyComponent } from './components/legals/privacy/privacy.component';
import { RegisterComponent } from './components/register/register.component';
import { ResetPasswordComponent } from './components/reset-password/reset-password.component';

const routes: Routes = [
  { path: '', component: HomepageComponent },
  {
    path: 'create-appointment', component: AppointmentRequestFormComponent,
    children: [
      { path: '', redirectTo: 'general', pathMatch: 'full' },
      { path: 'general', component: GeneralSubformComponent },
      { path: 'datepicker', component: DatepickerSubformComponent, canActivate: [AppointmentRequestAuthGuard] },
      { path: 'overview', component: DateOverviewSubformComponent, canActivate: [AppointmentRequestAuthGuard] },
      { path: 'email', component: EmailSubformComponent, canActivate: [AppointmentRequestAuthGuard] },
      { path: 'links', component: LinkSubformComponent, canActivate: [AppointmentRequestAuthGuard] },
      { path: '**', redirectTo: 'general', pathMatch: 'full' },
    ],
  },
  { path: 'terms-and-conditions', component: ConditionsComponent },
  { path: 'imprint', component: ImprintComponent },
  { path: 'privacy', component: PrivacyComponent },
  { path: 'contact', component: ContactComponent },
  { path: 'register', component: RegisterComponent },
  { path: 'forgot-password', component: ForgotPasswordComponent },
  { path: 'reset-password', component: ResetPasswordComponent },
];

@NgModule({
  imports: [RouterModule.forRoot(routes)],
  exports: [RouterModule],
})
export class AppRoutingModule {
}
