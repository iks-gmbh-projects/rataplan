import {Component, OnInit} from '@angular/core';
import {SurveyService} from "../../services/survey.service";
import {Survey} from "../../model/survey";
import {ActivatedRoute, Router} from "@angular/router";

@Component({
  selector: 'survey-final',
  templateUrl: 'survey-overview.component.html'
})

export class SurveyOverviewComponent implements OnInit {

  survey!: Survey;
  private id!: string | null;

  constructor(private activatedRoute: ActivatedRoute,
              private router: Router,
              private surveyService: SurveyService) {
  }

  ngOnInit() {
    this.id = this.activatedRoute!.snapshot.paramMap.get('id');

    this.surveyService.getSurveyOverview(this.id).subscribe(x => {
      this.survey = <Survey>x;
      console.log(this.survey);
    })
  }
}
