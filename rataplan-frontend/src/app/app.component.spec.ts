/*
/!* tslint:disable:no-unused-variable *!/
import {TestBed, async} from '@angular/core/testing';
import {AppComponent} from './app.component';
import {NavbarComponent} from "./components/shared-components/navbar/navbar.component";
import {RataplanFrontendRoutingModule} from "./app-routing.module";
import {HomeComponent} from "./components/home/home.component";
import {ShowAppointmentRequestComponent} from "./components/appointment-requests/show-appointment-request/show-appointment-request.component";
import {CreateAppointmentRequestComponent} from "./components/appointment-requests/create-appointment-request/create-appointment-request.component";
import {EditAppointmentRequestComponent} from "./components/appointment-requests/edit-appointment-request/edit-appointment-request.component";
import {ShowAppointmentRequestLinkComponent} from "./components/appointment-requests/create-appointment-request/show-appointment-request-link/show-appointment-request-link.component";
import {UserRegistrationComponent} from "./components/users/user-registration/user-registration.component";
import {UserLoginComponent} from "./components/users/user-login/user-login.component";
import {UserLogoutComponent} from "./components/users/user-logout/user-logout.component";
import {UserProfileComponent} from "./components/users/user-profile/user-profile.component";
import {NotFoundComponent} from "./components/not-found/not-found.component";
import {FormsModule, ReactiveFormsModule} from "@angular/forms";
import {AppointmentRequestFormDataComponent} from "./components/appointment-requests/create-appointment-request/appointment-request-form-data/appointment-request-form-data.component";
import {AppointmentRequestFormComponent} from "./components/appointment-request-form/appointment-request-form.component";
import {ConfigureAppointmentRequestComponent} from "./components/appointment-requests/create-appointment-request/configure-appointment-request/configure-appointment-request.component";
import {AppointmentsComponent} from "./components/appointment-requests/create-appointment-request/appointments/appointments.component";
import {ModalModule} from "ngx-modal";
import {PopoverModule} from "ngx-popover";
import {DecisionOptionsComponent} from "./components/appointment-requests/show-appointment-request/decision-options/decision-options.component";
import {DecisionParticipantsComponent} from "./components/appointment-requests/show-appointment-request/decision-participants/decision-participants.component";
import {NumberInputComponent} from "./components/shared-components/number-input/number-input.component";
import {DeleteAppointmentMemberComponent} from "./components/appointment-requests/show-appointment-request/delete-appointment-member/delete-appointment-member.component";
import {APP_BASE_HREF} from "@angular/common";
import {UserService} from "./services/user-service/user.service";
import {HttpModule} from "@angular/http";
import {Configuration} from "../environments/environment";

describe('App: RataplanFrontend', () => {
  beforeEach(() => {
    TestBed.configureTestingModule({
      declarations: [
        AppComponent,
        AppointmentsComponent,
        AppointmentRequestFormDataComponent,
        ConfigureAppointmentRequestComponent,
        CreateAppointmentRequestComponent,
        DecisionOptionsComponent,
        DecisionParticipantsComponent,
        DeleteAppointmentMemberComponent,
        EditAppointmentRequestComponent,
        HomeComponent,
        NavbarComponent,
        NotFoundComponent,
        NumberInputComponent,
        ShowAppointmentRequestComponent,
        ShowAppointmentRequestLinkComponent,
        UserRegistrationComponent,
        UserLoginComponent,
        UserLogoutComponent,
        UserProfileComponent
      ],
      imports: [
        FormsModule,
        HttpModule,
        ModalModule,
        PopoverModule,
        RataplanFrontendRoutingModule,
        ReactiveFormsModule
      ],
      providers: [
        {provide: APP_BASE_HREF, useValue: '/'},
        Configuration,
        UserService
      ]
    });
  });

  it('should create the app', async(() => {
    let fixture = TestBed.createComponent(AppComponent);
    let app = fixture.debugElement.componentInstance;
    expect(app).toBeTruthy();
  }));

  it(`should have a navbar element`, async(() => {
    let fixture = TestBed.createComponent(AppComponent);
    let element = fixture.nativeElement;
    fixture.detectChanges();
    expect(element.querySelector('rp-navbar')).toBeTruthy();
  }));

  it(`should have a router outlet element`, async(() => {
    let fixture = TestBed.createComponent(AppComponent);
    let element = fixture.nativeElement;
    fixture.detectChanges();
    expect(element.querySelector('router-outlet')).toBeTruthy();
  }));

});
*/
