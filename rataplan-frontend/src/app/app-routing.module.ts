import { NgModule } from '@angular/core';
import { RouterModule, Routes } from '@angular/router';
import {ConditionsComponent} from "./legals/conditions/conditions.component";
import {HomepageComponent} from "./homepage/homepage.component";
import {ImprintComponent} from "./legals/imprint/imprint.component";
import {PrivacyComponent} from "./legals/privacy/privacy.component";
import {ContactComponent} from "./legals/contact/contact.component";
import {LoginComponent} from "./login/login.component";
import {RegisterComponent} from "./register/register.component";
import {AuthGuardService} from "./services/auth-guard-service/auth-guard-service";

const routes: Routes = [
  { path: '', component: HomepageComponent},
  { path: 'login', component: LoginComponent, canActivate: [AuthGuardService]},
  { path: 'register', component: RegisterComponent, canActivate: [AuthGuardService]},
  { path: 'terms-and-conditions', component: ConditionsComponent},
  { path: 'imprint', component: ImprintComponent},
  { path: 'privacy', component: PrivacyComponent},
  { path: 'contact', component: ContactComponent}

];

@NgModule({
  imports: [RouterModule.forRoot(routes)],
  exports: [RouterModule]
})
export class AppRoutingModule { }
