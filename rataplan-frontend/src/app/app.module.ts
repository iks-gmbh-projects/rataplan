import { registerLocaleData } from '@angular/common';
import { HttpClientModule } from '@angular/common/http';
import localeDE from '@angular/common/locales/de';
import { NgModule } from '@angular/core';
import { FormsModule, ReactiveFormsModule } from '@angular/forms';
import { MatBadgeModule } from '@angular/material/badge';
import { MatButtonModule } from '@angular/material/button';
import { MatButtonToggleModule } from '@angular/material/button-toggle';
import { MatCardModule } from '@angular/material/card';
import { MatCheckboxModule } from '@angular/material/checkbox';
import { MatChipsModule } from '@angular/material/chips';
import { MAT_DATE_LOCALE, MatNativeDateModule } from '@angular/material/core';
import { MatDatepickerModule } from '@angular/material/datepicker';
import { MatDialogModule } from '@angular/material/dialog';
import { MatExpansionModule } from '@angular/material/expansion';
import { MatFormFieldModule } from '@angular/material/form-field';
import { MatIconModule } from '@angular/material/icon';
import { MatInputModule } from '@angular/material/input';
import { MatListModule } from '@angular/material/list';
import { MatMenuModule } from '@angular/material/menu';
import { MatProgressSpinnerModule } from '@angular/material/progress-spinner';
import { MatRadioModule } from '@angular/material/radio';
import { MatSidenavModule } from '@angular/material/sidenav';
import { MatSnackBarModule } from '@angular/material/snack-bar';
import { MatToolbarModule } from '@angular/material/toolbar';
import { MatTooltipModule } from '@angular/material/tooltip';
import { BrowserModule } from '@angular/platform-browser';
import { BrowserAnimationsModule } from '@angular/platform-browser/animations';
import { NgxMaterialTimepickerModule } from 'ngx-material-timepicker';

import { AppComponent } from './app.component';
import { AppCommonModule } from './app-common.module';
import { AppRoutingModule } from './app-routing.module';
import { AppointmentComponent } from './appointment/appointment/appointment.component';
import { MemberDecisionSubformComponent } from './appointment/appointment/member-decision-subform/member-decision-subform.component';
import { AppointmentRequestFormComponent, } from './appointment/appointment-request-form/appointment-request-form.component';
import { ConfigSubformComponent } from './appointment/appointment-request-form/config-subform/config-subform.component';
import { DatepickerSubformComponent } from './appointment/appointment-request-form/datepicker-subform/datepicker-subform.component';
import { EmailSubformComponent } from './appointment/appointment-request-form/email-subform/email-subform.component';
import { GeneralSubformComponent } from './appointment/appointment-request-form/general-subform/general-subform.component';
import { LinkSubformComponent } from './appointment/appointment-request-form/link-subform/link-subform.component';
import { OverviewSubformComponent } from './appointment/appointment-request-form/overview-subform/overview-subform.component';
import { ChangePasswordComponent } from './change-password/change-password.component';
import { DeleteProfileComponent } from './delete-profile/delete-profile.component';
import { ForgotPasswordComponent } from './forgot-password/forgot-password.component';
import { HomepageComponent } from './homepage/homepage.component';
import { ConditionsComponent } from './legals/conditions/conditions.component';
import { ContactComponent } from './legals/contact/contact.component';
import { ImprintComponent } from './legals/imprint/imprint.component';
import { PrivacyComponent } from './legals/privacy/privacy.component';
import { LoginComponent } from './login/login.component';
import { MainNavComponent } from './main-nav/main-nav.component';
import { ProfileComponent } from './profile/profile.component';
import { RegisterComponent } from './register/register.component';
import { ResetPasswordComponent } from './reset-password/reset-password.component';
import { StoreModule } from '@ngrx/store';
import { EffectsModule } from '@ngrx/effects';
import { environment } from "../environments/environment";
import { appReducers } from "./app.reducers";
import { appEffects } from "./app.effects";

registerLocaleData(localeDE);

@NgModule({
  declarations: [
    AppComponent,
    ConditionsComponent,
    HomepageComponent,
    ImprintComponent,
    PrivacyComponent,
    MainNavComponent,
    ContactComponent,
    LoginComponent,
    RegisterComponent,
    ForgotPasswordComponent,
    ResetPasswordComponent,
    RegisterComponent,
    MainNavComponent,
    AppointmentRequestFormComponent,
    GeneralSubformComponent,
    DatepickerSubformComponent,
    EmailSubformComponent,
    LinkSubformComponent,
    ProfileComponent,
    AppointmentComponent,
    MemberDecisionSubformComponent,
    ChangePasswordComponent,
    ConfigSubformComponent,
    OverviewSubformComponent,
    DeleteProfileComponent,
  ],
  imports: [
    BrowserModule,
    AppRoutingModule,
    AppCommonModule,
    BrowserAnimationsModule,
    MatButtonModule,
    MatToolbarModule,
    MatSidenavModule,
    MatListModule,
    MatIconModule,
    MatInputModule,
    MatDatepickerModule,
    MatNativeDateModule,
    MatRadioModule,
    ReactiveFormsModule,
    MatCardModule,
    MatChipsModule,
    HttpClientModule,
    MatSnackBarModule,
    MatCheckboxModule,
    MatFormFieldModule,
    FormsModule,
    MatButtonToggleModule,
    MatDialogModule,
    MatMenuModule,
    MatBadgeModule,
    MatExpansionModule,
    MatProgressSpinnerModule,
    MatTooltipModule,
    NgxMaterialTimepickerModule,
    StoreModule.forRoot(appReducers),
    EffectsModule.forRoot(appEffects),
    ...environment.devModules,
  ],
  providers: [
    { provide: MAT_DATE_LOCALE, useValue: 'de-DE' },
  ],
  bootstrap: [AppComponent],
})
export class AppModule { }
