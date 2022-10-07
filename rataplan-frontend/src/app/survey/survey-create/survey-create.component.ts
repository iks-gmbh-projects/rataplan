import { Component, OnDestroy, OnInit } from '@angular/core';
import { ActivatedRoute, Router } from '@angular/router';
import { Subscription } from 'rxjs';
import { Survey } from '../survey.model';
import { SurveyService } from '../survey.service';

@Component({
  selector: 'app-survey-create',
  templateUrl: './survey-create.component.html',
  styleUrls: ['./survey-create.component.css']
})
export class SurveyCreateComponent implements OnInit, OnDestroy {
  public survey?: Survey;
  public preview: boolean = false;
  private isEdit: boolean = false;

  private sub?: Subscription;

  constructor(private surveys: SurveyService, private router: Router, private route: ActivatedRoute) { }

  public ngOnInit(): void {
    this.survey = this.route.snapshot.data['survey'];
    this.isEdit = !!this.survey;
    this.sub = this.route.data.subscribe(data => {
      this.survey = data['survey'];
      this.isEdit = !!this.survey;
      this.preview = false;
    });
  }

  public ngOnDestroy(): void {
    this.sub?.unsubscribe();
  }

  public toPreview(survey: Survey): void {
    this.survey = survey;
    this.preview = true;
  }

  public submit(survey?: Survey): void {
    if (!survey) return this.edit();
    this.survey = survey;
    (
      this.isEdit ?
        this.surveys.editSurvey(survey) :
        this.surveys.createSurvey(survey)
    ).subscribe(surv => {
      this.router.navigate(["/survey", "access", surv.accessId], { relativeTo: this.route });
    });
  }

  public edit(): void {
    this.preview = false;
  }
}
