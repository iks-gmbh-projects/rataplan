import {NgModule} from '@angular/core';
import {BrowserModule} from '@angular/platform-browser';

import {AppComponent} from './app.component';
import {RouterModule, Routes} from "@angular/router";
import {HomeComponent} from "./components/home/home.component";
import {SurveyCreationComponent} from "./components/survey-creation/survey-creation.component";
import {SurveyCompletionComponent} from "./components/survey-completion/survey-completion.component";
import {HttpClientModule} from "@angular/common/http";
import {FormsModule} from "@angular/forms";
import {QuestionGroupFormComponent} from "./components/question-group-form/question-group-form.component";
import {QuestionGroupListComponent} from "./components/question-group-list/question-group-list.component";
import {QuestionFormComponent} from "./components/question-form/question-form.component";
import {QuestionListComponent} from "./components/question-list/question-list.component";
import {CheckboxListComponent} from "./components/checkbox-list/checkbox-list.component";
import {SurveySubmissionComponent} from "./components/survey-submission/survey-submission.component";
import {CheckboxFormComponent} from './components/checkbox-form/checkbox-form.component';

const appRoutes: Routes = [
  {path: '', component: HomeComponent},
  {path: 'createSurvey', component: SurveyCreationComponent},
  {path: 'createSurvey/questions', component: SurveyCompletionComponent}
]

@NgModule({
  declarations: [
    AppComponent,
    HomeComponent,
    SurveyCreationComponent,
    SurveyCompletionComponent,
    QuestionGroupFormComponent,
    QuestionGroupListComponent,
    QuestionFormComponent,
    QuestionListComponent,
    CheckboxFormComponent,
    CheckboxListComponent,
    SurveySubmissionComponent
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
