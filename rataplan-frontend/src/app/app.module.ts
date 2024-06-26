import { registerLocaleData } from '@angular/common';
import { HttpClientModule } from '@angular/common/http';
import localeDE from '@angular/common/locales/de';
import { CUSTOM_ELEMENTS_SCHEMA, NgModule } from '@angular/core';
import { MatBottomSheetModule } from '@angular/material/bottom-sheet';
import { DateAdapter, MAT_DATE_LOCALE, MatNativeDateModule } from '@angular/material/core';
import { MatDialogModule } from '@angular/material/dialog';
import { MatSidenavModule } from '@angular/material/sidenav';
import { MatSnackBarModule } from '@angular/material/snack-bar';
import { MatToolbarModule } from '@angular/material/toolbar';
import { BrowserModule } from '@angular/platform-browser';
import { BrowserAnimationsModule } from '@angular/platform-browser/animations';
import { MtxNativeDatetimeModule } from '@ng-matero/extensions/core';
import { EffectsModule } from '@ngrx/effects';
import { StoreModule } from '@ngrx/store';

import { environment } from '../environments/environment';
import { AppCommonModule } from './app-common.module';
import { AppRoutingModule } from './app-routing.module';
import { AppComponent } from './app.component';
import { appEffects } from './app.effects';
import { authFeature } from './authentication/auth.feature';
import { ChangePasswordComponent } from './change-password/change-password.component';
import { ConfirmAccountInstructionComponent } from './confirm-account/confirm-account-instruction/confirm-account-instruction.component';
import { ConfirmAccountComponent } from './confirm-account/confirm-account/confirm-account.component';
import { ResendAccountConfirmationEmailComponent } from './confirm-account/resend-account-confirmation-email/resend-account-confirmation-email.component';
import { AddContactComponent } from './contact-list/add-contact/add-contact.component';
import { ContactListComponent } from './contact-list/contact-list.component';
import { contactsFeature } from './contact-list/contacts.feature';
import { EditGroupComponent } from './contact-list/edit-group/edit-group.component';
import { GroupDisplayComponent } from './contact-list/group-display/group-display.component';
import { CookieBannerComponent } from './cookie-banner/cookie-banner.component';
import { cookieFeature } from './cookie-banner/cookie.feature';
import { DeleteProfileComponent } from './delete-profile/delete-profile.component';
import { FeedbackDialogComponent } from './dialogs/feedback-dialog/feedback-dialog.component';
import { EditProfileComponent } from './edit-profile/edit-profile.component';
import { EmailNotificationSettingsComponent } from './email-notification-settings/email-notification-settings.component';
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
import { ChangeNoteComponent } from './version/change-note/change-note.component';
import { VersionComponent } from './version/version.component';
import { ViewProfileComponent } from './view-profile/view-profile.component';
import { ConfirmChoiceComponent } from './vote/vote-form/confirm-choice/confirm-choice.component';

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
    ChangeNoteComponent,
  ],
  imports: [
    BrowserModule,
    AppRoutingModule,
    BrowserAnimationsModule,
    MatToolbarModule,
    MatSidenavModule,
    MatNativeDateModule,
    HttpClientModule,
    MatSnackBarModule,
    MatBottomSheetModule,
    MatDialogModule,
    StoreModule.forRoot({}),
    StoreModule.forFeature(authFeature),
    StoreModule.forFeature(contactsFeature),
    StoreModule.forFeature(cookieFeature),
    StoreModule.forFeature(emailNotificationSettingsFeature),
    StoreModule.forFeature(notificationFeature),
    EffectsModule.forRoot(appEffects),
    ...environment.devModules,
    AppCommonModule,
    MtxNativeDatetimeModule,
  ],
  providers: [
    {provide: MAT_DATE_LOCALE, useValue: 'de-DE'},
    {provide: DateAdapter, useClass: EUDateAdapter},
  ],
  bootstrap: [AppComponent],
  schemas: [CUSTOM_ELEMENTS_SCHEMA],
})
export class AppModule {}