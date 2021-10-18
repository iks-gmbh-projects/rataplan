import {NgModule} from '@angular/core';
import {BrowserModule} from '@angular/platform-browser';

import {AppComponent} from './app.component';
import {RouterModule, Routes} from "@angular/router";
import {HomeComponent} from "./components/home/home.component";
import {SurveyCreationComponent} from "./components/survey-creation/survey-creation.component";
import {QuestionAddingComponent} from "./components/question-adding/question-adding.component";

const appRoutes: Routes = [
  {path: '', component: HomeComponent},
  {path: 'createSurvey', component: SurveyCreationComponent},
  {path: 'createSurvey/addQuestions', component: QuestionAddingComponent}
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
    RouterModule.forRoot(appRoutes)
  ],
  providers: [],
  bootstrap: [AppComponent]
})
export class AppModule {
}
