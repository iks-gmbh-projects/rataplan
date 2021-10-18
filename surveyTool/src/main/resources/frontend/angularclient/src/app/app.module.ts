import {NgModule} from '@angular/core';
import {BrowserModule} from '@angular/platform-browser';

import {AppComponent} from './app.component';
import {RouterModule, Routes} from "@angular/router";
import {HomeComponent} from "./components/home/home.component";
import {SurveyCreationComponent} from "./components/survey-creation/survey-creation.component";
import {QuestionAddingComponent} from "./components/question-adding/question-adding.component";
import {HttpClientModule} from "@angular/common/http";
import {FormsModule} from "@angular/forms";

const appRoutes: Routes = [
  {path: '', component: HomeComponent},
  {path: 'createSurvey', component: SurveyCreationComponent},
  {path: 'createSurvey/questions', component: QuestionAddingComponent}
]

@NgModule({
  declarations: [
    AppComponent,
    HomeComponent,
    SurveyCreationComponent,
    QuestionAddingComponent
  ],
  imports: [
    BrowserModule,
    HttpClientModule,
    RouterModule.forRoot(appRoutes),
    FormsModule
  ],
  providers: [],
  bootstrap: [AppComponent]
})
export class AppModule {
}
