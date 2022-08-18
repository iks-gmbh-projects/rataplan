import {NgModule} from '@angular/core';
import {RouterModule, Routes} from '@angular/router';
import {ConditionsComponent} from "./legals/conditions/conditions.component";
import {HomepageComponent} from "./homepage/homepage.component";
import {ImprintComponent} from "./legals/imprint/imprint.component";
import {PrivacyComponent} from "./legals/privacy/privacy.component";
import {ContactComponent} from "./legals/contact/contact.component";
import {RegisterComponent} from "./register/register.component";
import {ForgotPasswordComponent} from "./forgot-password/forgot-password.component";
import {ResetPasswordComponent} from "./reset-password/reset-password.component";

const routes: Routes = [
  {path: '', component: HomepageComponent},
  {path: 'terms-and-conditions', component: ConditionsComponent},
  {path: 'imprint', component: ImprintComponent},
  {path: 'privacy', component: PrivacyComponent},
  {path: 'contact', component: ContactComponent},
  {path: 'register', component: RegisterComponent},
  {path: 'forgot-password', component: ForgotPasswordComponent},
  {path: 'reset-password', component: ResetPasswordComponent}

];

@NgModule({
  imports: [RouterModule.forRoot(routes)],
  exports: [RouterModule]
})
export class AppRoutingModule {
}
