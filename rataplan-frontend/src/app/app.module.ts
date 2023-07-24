import { registerLocaleData } from '@angular/common';
import { HttpClientModule } from '@angular/common/http';
import localeDE from '@angular/common/locales/de';
import { CUSTOM_ELEMENTS_SCHEMA, NgModule } from '@angular/core';
import { FormsModule, ReactiveFormsModule } from '@angular/forms';
import { MatBadgeModule } from '@angular/material/badge';
import { MatButtonModule } from '@angular/material/button';
import { MatButtonToggleModule } from '@angular/material/button-toggle';
import { MatCardModule } from '@angular/material/card';
import { MatCheckboxModule } from '@angular/material/checkbox';
import { MatChipsModule } from '@angular/material/chips';
import { DateAdapter, MAT_DATE_LOCALE, MatNativeDateModule } from '@angular/material/core';
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
import { EffectsModule } from '@ngrx/effects';
import { StoreModule } from '@ngrx/store';
import { NgxMaterialTimepickerModule } from 'ngx-material-timepicker';

import { environment } from '../environments/environment';
import { AppComponent } from './app.component';
import { appEffects } from './app.effects';
import { appReducers } from './app.reducers';
import { AppCommonModule } from './app-common.module';
import { AppRoutingModule } from './app-routing.module';
import { ChangePasswordComponent } from './change-password/change-password.component';
import { DeleteProfileComponent } from './delete-profile/delete-profile.component';
import { EditProfileComponent } from './edit-profile/edit-profile.component';
import { ForgotPasswordComponent } from './forgot-password/forgot-password.component';
import { HomepageComponent } from './homepage/homepage.component';
import { ConditionsComponent } from './legals/conditions/conditions.component';
import { ContactComponent } from './legals/contact/contact.component';
import { ImprintComponent } from './legals/imprint/imprint.component';
import { PrivacyComponent } from './legals/privacy/privacy.component';
import { LoginComponent } from './login/login.component';
import { MainNavComponent } from './main-nav/main-nav.component';
import { RegisterComponent } from './register/register.component';
import { ResetPasswordComponent } from './reset-password/reset-password.component';
import { ViewProfileComponent } from './view-profile/view-profile.component';
import { VoteDecisionSubformComponent } from './vote/vote/member-decision-subform/vote-decision-subform.component';
import { VoteComponent } from './vote/vote/vote.component';
import { ConfigSubformComponent } from './vote/vote-form/config-subform/config-subform.component';
import { DatepickerSubformComponent } from './vote/vote-form/datepicker-subform/datepicker-subform.component';
import { EmailSubformComponent } from './vote/vote-form/email-subform/email-subform.component';
import { GeneralSubformComponent } from './vote/vote-form/general-subform/general-subform.component';
import { LinkSubformComponent } from './vote/vote-form/link-subform/link-subform.component';
import { OverviewSubformComponent } from './vote/vote-form/overview-subform/overview-subform.component';
import { VoteFormComponent } from './vote/vote-form/vote-form.component';
import { VoteListComponent } from './vote-list/vote-list.component';
import { ClipboardModule } from '@angular/cdk/clipboard';
import { EUDateAdapter } from './eu-date-adapter';

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
    VoteFormComponent,
    GeneralSubformComponent,
    DatepickerSubformComponent,
    EmailSubformComponent,
    LinkSubformComponent,
    EditProfileComponent,
    VoteComponent,
    VoteDecisionSubformComponent,
    ChangePasswordComponent,
    ConfigSubformComponent,
    OverviewSubformComponent,
    DeleteProfileComponent,
    VoteListComponent,
    ViewProfileComponent,
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
    ClipboardModule,
  ],
  providers: [
    { provide: MAT_DATE_LOCALE, useValue: 'de-DE' },
    { provide: DateAdapter, useClass: EUDateAdapter }
  ],
  bootstrap: [AppComponent],
  schemas: [CUSTOM_ELEMENTS_SCHEMA],
})
export class AppModule { }
