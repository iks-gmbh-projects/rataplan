import { registerLocaleData } from '@angular/common';
import { HttpClientModule } from '@angular/common/http';
import localeDE from '@angular/common/locales/de';
import { NgModule } from '@angular/core';
import { FormsModule, ReactiveFormsModule } from '@angular/forms';
import { MatButtonModule } from '@angular/material/button';
import { MatCardModule } from '@angular/material/card';
import { MatCheckboxModule } from '@angular/material/checkbox';
import { MatChipsModule } from '@angular/material/chips';
import { MAT_DATE_LOCALE, MatNativeDateModule } from '@angular/material/core';
import { MatDatepickerModule } from '@angular/material/datepicker';
import { MatFormFieldModule } from '@angular/material/form-field';
import { MatIconModule } from '@angular/material/icon';
import { MatInputModule } from '@angular/material/input';
import { MatListModule } from '@angular/material/list';
import { MatRadioModule } from '@angular/material/radio';
import { MatSidenavModule } from '@angular/material/sidenav';
import { MatSnackBarModule } from '@angular/material/snack-bar';
import { MatToolbarModule } from '@angular/material/toolbar';
import { MatMomentDateModule } from '@angular/material-moment-adapter';
import { BrowserModule } from '@angular/platform-browser';
import { BrowserAnimationsModule } from '@angular/platform-browser/animations';

import { AppComponent } from './app.component';
import { AppRoutingModule } from './app-routing.module';
import { AppointmentRequestFormComponent, } from './components/appointment/appointment-request-form/appointment-request-form.component';
import { DateOverviewSubformComponent } from './components/appointment/appointment-request-form/date-overview-subform/date-overview-subform.component';
import { DatepickerSubformComponent, } from './components/appointment/appointment-request-form/datepicker-subform/datepicker-subform.component';
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
import { MainNavComponent } from './main-nav/main-nav.component';

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
    RegisterComponent,
    ForgotPasswordComponent,
    ResetPasswordComponent,
    RegisterComponent,
    MainNavComponent,
    AppointmentRequestFormComponent,
    GeneralSubformComponent,
    DatepickerSubformComponent,
    DateOverviewSubformComponent,
    EmailSubformComponent,
    LinkSubformComponent,
  ],
  imports: [
    BrowserModule,
    AppRoutingModule,
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
    MatMomentDateModule,
    MatChipsModule,
    HttpClientModule,
    MatSnackBarModule,
    MatCheckboxModule,
    MatFormFieldModule,
    FormsModule,
  ],
  providers: [
    { provide: MAT_DATE_LOCALE, useValue: 'de-DE' },
  ],
  bootstrap: [AppComponent],
})
export class AppModule {
}
