import { NgModule } from '@angular/core';
import { RouterModule, Routes } from '@angular/router';
import {ConditionsComponent} from "./legals/conditions/conditions.component";
import {HomepageComponent} from "./homepage/homepage.component";
import {ImprintComponent} from "./legals/imprint/imprint.component";

const routes: Routes = [
  { path: '', component: HomepageComponent},
  { path: 'terms-and-conditions', component: ConditionsComponent},
  { path: 'imprint', component: ImprintComponent}
];

@NgModule({
  imports: [RouterModule.forRoot(routes)],
  exports: [RouterModule]
})
export class AppRoutingModule { }
