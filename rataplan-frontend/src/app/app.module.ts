import { LOCATION_INITIALIZED } from '@angular/common';
import { APP_INITIALIZER, CUSTOM_ELEMENTS_SCHEMA, Injector, NgModule } from '@angular/core';
import { FormsModule, ReactiveFormsModule } from '@angular/forms';
import { HttpModule } from '@angular/http';
import { BrowserModule } from '@angular/platform-browser';
import { BrowserAnimationsModule } from '@angular/platform-browser/animations';
import { ErrorHandlerService } from 'app/services/error-handler-service/error-handler.service';
import { MyDatePickerModule } from 'mydatepicker';
import { ModalModule } from 'ngx-modal';
import { PopoverModule } from 'ngx-popover';
import { RataplanFrontendRoutingModule } from './app-routing.module';

import { AppComponent } from './app.component';

import { ImprintComponent } from './components/legals/imprint/imprint.component';

import { AppointmentRequestCreateComponent } from './components/appointment-requests/appointment-request-create/appointment-request-create.component';
import { AppointmentRequestLinkComponent } from './components/appointment-requests/appointment-request-create/appointment-request-link/appointment-request-link.component';
import { AppointmentRequestEditComponent } from './components/appointment-requests/appointment-request-edit/appointment-request-edit.component';
import { AppointmentConfigSubformComponent } from './components/appointment-requests/appointment-request-form/appointment-config-subform/appointment-config-subform.component';
import { AppointmentListComponent } from './components/appointment-requests/appointment-request-form/appointment-list/appointment-list.component';
import { AppointmentRequestFormComponent } from './components/appointment-requests/appointment-request-form/appointment-request-form.component';
import { AppointmentSubformComponent } from './components/appointment-requests/appointment-request-form/appointment-subform/appointment-subform.component';
import { DecisionConfigSubformComponent } from './components/appointment-requests/appointment-request-form/decision-config-subform/decision-config-subform.component';
import { EmailSubformComponent } from './components/appointment-requests/appointment-request-form/email-subform/email-subform.component';
import { GeneralSubformComponent } from './components/appointment-requests/appointment-request-form/general-subform/general-subform.component';
import { SecuritySubformComponent } from './components/appointment-requests/appointment-request-form/security-subform/security-subform.component';

import { DecisionOptionsComponent } from './components/appointment-requests/show-appointment-request/decision-options/decision-options.component';
import { DecisionParticipantsComponent } from './components/appointment-requests/show-appointment-request/decision-participants/decision-participants.component';
import { ShowAppointmentRequestComponent } from './components/appointment-requests/show-appointment-request/show-appointment-request.component';

import { ConditionsComponent } from './components/legals/conditions/conditions.component';
import { ContactComponent } from './components/legals/contact/contact.component';
import { HomeComponent } from './components/home/home.component';
import { AppointmentRequestNotFoundComponent } from './components/errors/not-found/appointment-request-not-found/appointment-request-not-found.component';

import { NotFoundComponent } from './components/errors/not-found/not-found.component';
import { PrivacyComponent } from './components/legals/privacy/privacy.component';
import { ServiceUnavailableComponent } from './components/errors/service-unavailable/service-unavailable.component';
import { FooterComponent } from './components/shared-components/footer/footer.component';

import { NavbarComponent } from './components/shared-components/navbar/navbar.component';
import { NumberInputComponent } from './components/shared-components/number-input/number-input.component';
import { ChangePasswordComponent } from './components/users/change-password/change-password.component';
import { UserAppointmentRequestListComponent } from './components/users/user-appointment-request-list/user-appointment-request-list.component';
import { UserOwnerListComponent } from './components/users/user-appointment-request-list/user-owner-list/user-owner-list.component';
import { UserParticipantComponent } from './components/users/user-appointment-request-list/user-participant-list/user-participant-list.component';
import { UserLoginComponent } from './components/users/user-login/user-login.component';
import { UserProfileComponent } from './components/users/user-profile/user-profile.component';

import { UserRegistrationComponent } from './components/users/user-registration/user-registration.component';
import { AppointmentMemberService } from './services/appointment-member-service/appointment-member.service';
import { AppointmentRequestService } from './services/appointment-request-service/appointment-request.service';
import { LinkDataStorageService } from './services/appointment-request-service/link-data-storage.service';
import { ContactService } from './services/contact-service/contact.service';
import { DatepickerOptionsService } from './services/datepicker-service/datepicker-options.service';

import { EnvironmentService } from './services/environment-service/environment.service';
import { ExportService } from './services/export-service/export.service';
import { UserService } from './services/user-service/user.service';

import { DatexPipe } from './utilities/datexpipe';
import { SpinnerComponent } from './components/shared-components/spinner/spinner.component';
import { StatisticComponent } from './components/appointment-requests/show-appointment-request/statistic/statistic.component';
import { ShowTheadComponent } from './components/appointment-requests/show-appointment-request/show-thead/show-thead.component';
import { AppointmentMemberFormRowComponent } from './components/appointment-requests/show-appointment-request/appointment-member-form-row/appointment-member-form-row.component';
import { ShowInformationComponent } from './components/appointment-requests/show-appointment-request/show-information/show-information.component';
import { ShowAppointmentMemberRowComponent } from './components/appointment-requests/show-appointment-request/show-appointment-member-row/show-appointment-member-row.component';
import { ProtectorComponent } from './components/appointment-requests/protector/protector.component';

import { MobileShowComponent } from './components/appointment-requests/show-appointment-request/mobile-show/mobile-show.component';
import { MobileAppointmentRowsComponent } from './components/appointment-requests/show-appointment-request/mobile-show/mobile-appointment-rows/mobile-appointment-rows.component';
import { MobileShowInformationComponent } from './components/appointment-requests/show-appointment-request/mobile-show/mobile-show-information/mobile-show-information.component';
import { MobileShowDecisionsComponent } from './components/appointment-requests/show-appointment-request/mobile-show/mobile-show-decisions/mobile-show-decisions.component';
import { MobileAppointmentMemberFormComponent } from './components/appointment-requests/show-appointment-request/mobile-show/mobile-appointment-member-form/mobile-appointment-member-form.component';

export function appInitializer(injector: Injector, service: EnvironmentService) {
  return () => new Promise(resolve => {
    const locationInitialized = injector.get(LOCATION_INITIALIZED, Promise.resolve(null));
    locationInitialized.then(() => {
      service.load().then(() => {
        resolve();
      });
    });
  }).catch(() => {
    console.log('Initialization failed');
  });
}

@NgModule({
  declarations: [
    AppComponent,
    NavbarComponent,
    FooterComponent,
    ImprintComponent,
    ConditionsComponent,
    ContactComponent,
    HomeComponent,
    PrivacyComponent,
    NotFoundComponent,
    AppointmentRequestNotFoundComponent,
    AppointmentRequestFormComponent,
    AppointmentRequestCreateComponent,
    GeneralSubformComponent,
    DecisionConfigSubformComponent,
    SecuritySubformComponent,
    EmailSubformComponent,
    AppointmentConfigSubformComponent,
    AppointmentSubformComponent,
    AppointmentListComponent,
    AppointmentRequestLinkComponent,
    AppointmentRequestEditComponent,
    ShowAppointmentRequestComponent,
    DecisionOptionsComponent,
    DecisionParticipantsComponent,
    NumberInputComponent,
    UserRegistrationComponent,
    UserLoginComponent,
    UserProfileComponent,
    UserAppointmentRequestListComponent,
    UserOwnerListComponent,
    UserParticipantComponent,
    DatexPipe,
    ServiceUnavailableComponent,
    ChangePasswordComponent,
    SpinnerComponent,
    StatisticComponent,
    ShowTheadComponent,
    AppointmentMemberFormRowComponent,
    ShowInformationComponent,
    ShowAppointmentMemberRowComponent,
    ProtectorComponent,
    MobileShowComponent,
    MobileAppointmentRowsComponent,
    MobileShowInformationComponent,
    MobileShowDecisionsComponent,
    MobileAppointmentMemberFormComponent
  ],
  imports: [
    FormsModule,
    ReactiveFormsModule,
    HttpModule,
    BrowserModule,
    BrowserAnimationsModule,
    ModalModule,
    MyDatePickerModule,
    PopoverModule,
    RataplanFrontendRoutingModule
  ],
  schemas: [CUSTOM_ELEMENTS_SCHEMA],
  providers: [
    EnvironmentService,
    AppointmentRequestService,
    DatepickerOptionsService,
    LinkDataStorageService,
    AppointmentMemberService,
    UserService,
    ContactService,
    ExportService,
    ErrorHandlerService,
    [{
      provide: APP_INITIALIZER,
      useFactory: appInitializer,
      deps: [Injector, EnvironmentService],
      multi: true
    }]
  ],
  bootstrap: [
    AppComponent
  ]
})
export class AppModule {

}
