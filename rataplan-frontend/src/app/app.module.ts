import { registerLocaleData } from '@angular/common';
import { HttpClientModule } from '@angular/common/http';
import localeDE from '@angular/common/locales/de';
import { CUSTOM_ELEMENTS_SCHEMA, NgModule } from '@angular/core';
import { DateAdapter, MAT_DATE_LOCALE, MatNativeDateModule } from '@angular/material/core';
import { MatDialogModule } from '@angular/material/dialog';
import { MatExpansionModule } from '@angular/material/expansion';
import { MatMenuModule } from '@angular/material/menu';
import { MatRadioModule } from '@angular/material/radio';
import { MatSidenavModule } from '@angular/material/sidenav';
import { MatSnackBarModule } from '@angular/material/snack-bar';
import { MatToolbarModule } from '@angular/material/toolbar';
import { BrowserModule } from '@angular/platform-browser';
import { BrowserAnimationsModule } from '@angular/platform-browser/animations';
import { EffectsModule } from '@ngrx/effects';
import { StoreModule } from '@ngrx/store';
import { NgxMaterialTimepickerModule } from 'ngx-material-timepicker';

import { environment } from '../environments/environment';
import { AppComponent } from './app.component';
import { appEffects } from './app.effects';
import { AppRoutingModule } from './app-routing.module';
import { ChangePasswordComponent } from './change-password/change-password.component';
import { ConfirmAccountComponent } from './confirm-account/confirm-account/confirm-account.component';
import { ConfirmAccountInstructionComponent } from './confirm-account/confirm-account-instruction/confirm-account-instruction.component';
import { ResendAccountConfirmationEmailComponent } from './confirm-account/resend-account-confirmation-email/resend-account-confirmation-email.component';
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
import { EUDateAdapter } from './eu-date-adapter';
import { CookieBannerComponent } from './cookie-banner/cookie-banner.component';
import { MatBottomSheetModule } from '@angular/material/bottom-sheet';
import { authFeature } from './authentication/auth.feature';
import { cookieFeature } from './cookie-banner/cookie.feature';
import { FormsModule, ReactiveFormsModule } from '@angular/forms';
import { MatIconModule } from '@angular/material/icon';
import { MatBadgeModule } from '@angular/material/badge';
import { MatButtonModule } from '@angular/material/button';
import { MatInputModule } from '@angular/material/input';
import { MatProgressSpinnerModule } from '@angular/material/progress-spinner';
import { MatListModule } from '@angular/material/list';
import { notificationFeature } from './notification/notification.feature';
import { MatSlideToggleModule } from '@angular/material/slide-toggle';
import { ConfirmChoiceComponent } from './vote/vote-form/confirm-choice/confirm-choice.component';
import { VersionComponent } from './version/version.component';

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
    VersionComponent,
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
    StoreModule.forFeature(cookieFeature),
    StoreModule.forFeature(notificationFeature),
    EffectsModule.forRoot(appEffects),
    ...environment.devModules,
    MatSlideToggleModule,
  ],
  providers: [
    { provide: MAT_DATE_LOCALE, useValue: 'de-DE' },
    { provide: DateAdapter, useClass: EUDateAdapter },
  ],
  bootstrap: [AppComponent],
  schemas: [CUSTOM_ELEMENTS_SCHEMA],
})
export class AppModule { }
