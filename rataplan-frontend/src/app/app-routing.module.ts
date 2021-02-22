import { NgModule } from '@angular/core';
import { RouterModule, Routes } from '@angular/router';
import { AboutComponent } from './components/legals/imprint/about.component';
import { AppointmentRequestCreateComponent } from './components/appointment-requests/appointment-request-create/appointment-request-create.component';
import { ConditionsComponent } from './components/legals/conditions/conditions.component';
import { ContactComponent } from './components/legals/contact/contact.component';
import { HomeComponent } from './components/home/home.component';
import { AppointmentRequestNotFoundComponent } from './components/errors/not-found/appointment-request-not-found/appointment-request-not-found.component';
import { NotFoundComponent } from './components/errors/not-found/not-found.component';
import { PrivacyComponent } from './components/legals/privacy/privacy.component';
import { ServiceUnavailableComponent } from './components/errors/service-unavailable/service-unavailable.component';
import { UserAppointmentRequestListComponent } from './components/users/user-appointment-request-list/user-appointment-request-list.component';
import { UserLoginComponent } from './components/users/user-login/user-login.component';
import { UserProfileComponent } from './components/users/user-profile/user-profile.component';
import { UserRegistrationComponent } from './components/users/user-registration/user-registration.component';
import { ChangePasswordComponent } from './components/users/change-password/change-password.component';
import { AppointmentRequestEditComponent } from './components/appointment-requests/appointment-request-edit/appointment-request-edit.component';
import { ShowAppointmentRequestComponent } from './components/appointment-requests/show-appointment-request/show-appointment-request.component';
import { MobileShowComponent } from './components/appointment-requests/show-appointment-request/mobile-show/mobile-show.component';

const routes: Routes = [
  { path: '', component: HomeComponent },

  { path: 'appointmentrequest/:id/edit', component: AppointmentRequestEditComponent },
  { path: 'appointmentrequest/:id', component: ShowAppointmentRequestComponent },
  { path: 'appointmentrequest', component: AppointmentRequestCreateComponent },
  { path: 'm/appointmentrequest/:id', component: MobileShowComponent },
  { path: 'm/appointmentrequest/:id/edit', redirectTo: 'appointmentrequest/:id/edit' },

  { path: 'register', component: UserRegistrationComponent },
  { path: 'profile', component: UserProfileComponent },
  { path: 'profile/changePassword', component: ChangePasswordComponent },
  { path: 'appointmentrequests', component: UserAppointmentRequestListComponent },
  { path: 'login', component: UserLoginComponent },

  { path: 'privacy', component: PrivacyComponent },
  { path: 'about', component: AboutComponent },
  { path: 'terms-and-conditions', component: ConditionsComponent },
  { path: 'contact', component: ContactComponent },
  { path: 'unavailable', component: ServiceUnavailableComponent },

  { path: 'not-found/:id', component: AppointmentRequestNotFoundComponent },
  { path: 'not-found', component: NotFoundComponent },
  { path: '**', redirectTo: 'not-found' }
];

@NgModule({
  imports: [RouterModule.forRoot(routes)],
  exports: [RouterModule],
  providers: []
})
export class RataplanFrontendRoutingModule {
}
