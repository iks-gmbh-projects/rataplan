import {Component, Input, OnInit} from '@angular/core';
import {QuestionGroup} from "../../model/question-group";
import {Survey} from "../../model/survey";
import {SurveyService} from "../../services/survey.service";

@Component({
  selector: 'question-group-form',
  templateUrl: 'question-group-form.component.html'
})

export class QuestionGroupFormComponent implements OnInit {

  questionGroup: QuestionGroup;
  @Input() survey!: Survey;

  constructor(private surveyService: SurveyService) {
    this.questionGroup = new QuestionGroup();
  }

  ngOnInit() {

  }

  onSubmit() {

    // this.surveyService.addQuestionGroup(this.survey, this.questionGroup).subscribe();
  }
}
