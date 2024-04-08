import { registerLocaleData } from '@angular/common';
import { HttpClientModule } from '@angular/common/http';
import localeDE from '@angular/common/locales/de';
import { CUSTOM_ELEMENTS_SCHEMA, NgModule } from '@angular/core';
import { FormsModule, ReactiveFormsModule } from '@angular/forms';
import { MatBadgeModule } from '@angular/material/badge';
import { MatBottomSheetModule } from '@angular/material/bottom-sheet';
import { MatButtonModule } from '@angular/material/button';
import { DateAdapter, MAT_DATE_LOCALE, MatNativeDateModule } from '@angular/material/core';
import { MatDialogModule } from '@angular/material/dialog';
import { MatExpansionModule } from '@angular/material/expansion';
import { MatIconModule } from '@angular/material/icon';
import { MatInputModule } from '@angular/material/input';
import { MatListModule } from '@angular/material/list';
import { MatMenuModule } from '@angular/material/menu';
import { MatProgressSpinnerModule } from '@angular/material/progress-spinner';
import { MatRadioModule } from '@angular/material/radio';
import { MatSelectModule } from '@angular/material/select';
import { MatSidenavModule } from '@angular/material/sidenav';
import { MatSlideToggleModule } from '@angular/material/slide-toggle';
import { MatSnackBarModule } from '@angular/material/snack-bar';
import { MatToolbarModule } from '@angular/material/toolbar';
import { BrowserModule } from '@angular/platform-browser';
import { BrowserAnimationsModule } from '@angular/platform-browser/animations';
import { EffectsModule } from '@ngrx/effects';
import { StoreModule } from '@ngrx/store';
import { NgxMaterialTimepickerModule } from 'ngx-material-timepicker';

import { environment } from '../environments/environment';
import { AppCommonModule } from './app-common.module';
import { AppComponent } from './app.component';
import { appEffects } from './app.effects';
import { AppRoutingModule } from './app-routing.module';
import { authFeature } from './authentication/auth.feature';
import { ChangePasswordComponent } from './change-password/change-password.component';
import { ConfirmAccountComponent } from './confirm-account/confirm-account/confirm-account.component';
import { ConfirmAccountInstructionComponent } from './confirm-account/confirm-account-instruction/confirm-account-instruction.component';
import { ResendAccountConfirmationEmailComponent } from './confirm-account/resend-account-confirmation-email/resend-account-confirmation-email.component';
import { contactsFeature } from './contact-list/contacts.feature';
import { cookieFeature } from './cookie-banner/cookie.feature';
import { CookieBannerComponent } from './cookie-banner/cookie-banner.component';
import { DeleteProfileComponent } from './delete-profile/delete-profile.component';
import { EditProfileComponent } from './edit-profile/edit-profile.component';
import { emailNotificationSettingsFeature } from './email-notification-settings/state/email-notification-settings.feature';
import { EUDateAdapter } from './eu-date-adapter';
import { FeedbackComponent } from './feedback/feedback.component';
import { ForgotPasswordComponent } from './forgot-password/forgot-password.component';
import { HomepageComponent } from './homepage/homepage.component';
import { ConditionsComponent } from './legals/conditions/conditions.component';
import { ContactComponent } from './legals/contact/contact.component';
import { ImprintComponent } from './legals/imprint/imprint.component';
import { PrivacyComponent } from './legals/privacy/privacy.component';
import { LoginComponent } from './login/login.component';
import { MainNavComponent } from './main-nav/main-nav.component';
import { notificationFeature } from './notification/notification.feature';
import { RegisterComponent } from './register/register.component';
import { ResetPasswordComponent } from './reset-password/reset-password.component';
import { VersionComponent } from './version/version.component';
import { ViewProfileComponent } from './view-profile/view-profile.component';
import { ConfirmChoiceComponent } from './vote/vote-form/confirm-choice/confirm-choice.component';
import { FeedbackDialogComponent } from './dialogs/feedback-dialog/feedback-dialog.component';
import { EmailNotificationSettingsComponent } from './email-notification-settings/email-notification-settings.component';
import { ContactListComponent } from './contact-list/contact-list.component';
import { AddContactComponent } from './contact-list/add-contact/add-contact.component';
import { GroupDisplayComponent } from './contact-list/group-display/group-display.component';
import { EditGroupComponent } from './contact-list/edit-group/edit-group.component';

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
    EditProfileComponent,
    ChangePasswordComponent,
    DeleteProfileComponent,
    ViewProfileComponent,
    CookieBannerComponent,
    ConfirmAccountInstructionComponent,
    ConfirmAccountComponent,
    ResendAccountConfirmationEmailComponent,
    ConfirmChoiceComponent,
    FeedbackComponent,
    VersionComponent,
    FeedbackDialogComponent,
    EmailNotificationSettingsComponent,
    ContactListComponent,
    AddContactComponent,
    GroupDisplayComponent,
    EditGroupComponent,
  ],
  imports: [
    BrowserModule,
    AppRoutingModule,
    ReactiveFormsModule,
    FormsModule,
    BrowserAnimationsModule,
    MatIconModule,
    MatBadgeModule,
    MatButtonModule,
    MatRadioModule,
    MatInputModule,
    MatSelectModule,
    MatProgressSpinnerModule,
    MatListModule,
    MatToolbarModule,
    MatSidenavModule,
    MatNativeDateModule,
    HttpClientModule,
    MatMenuModule,
    MatSnackBarModule,
    NgxMaterialTimepickerModule,
    MatBottomSheetModule,
    MatDialogModule,
    MatExpansionModule,
    StoreModule.forRoot({}),
    StoreModule.forFeature(authFeature),
    StoreModule.forFeature(contactsFeature),
    StoreModule.forFeature(cookieFeature),
    StoreModule.forFeature(emailNotificationSettingsFeature),
    StoreModule.forFeature(notificationFeature),
    EffectsModule.forRoot(appEffects),
    ...environment.devModules,
    MatSlideToggleModule,
    AppCommonModule,
  ],
  providers: [
    {provide: MAT_DATE_LOCALE, useValue: 'de-DE'},
    {provide: DateAdapter, useClass: EUDateAdapter},
  ],
  bootstrap: [AppComponent],
  schemas: [CUSTOM_ELEMENTS_SCHEMA],
})
export class AppModule {}