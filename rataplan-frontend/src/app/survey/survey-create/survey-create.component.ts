import { Component, OnDestroy, OnInit } from '@angular/core';
import { ActivatedRoute, Router } from '@angular/router';
import { BehaviorSubject, Subscription } from 'rxjs';
import { Survey } from '../survey.model';
import { SurveyService } from '../survey.service';
import { MatLegacySnackBar as MatSnackBar } from '@angular/material/legacy-snack-bar';

@Component({
  selector: 'app-survey-create',
  templateUrl: './survey-create.component.html',
  styleUrls: ['./survey-create.component.css']
})
export class SurveyCreateComponent implements OnInit, OnDestroy {
  public survey?: Survey;
  public preview: boolean = false;
  private isEdit: boolean = false;
  public busy$: BehaviorSubject<boolean> = new BehaviorSubject<boolean>(false);

  private sub?: Subscription;

  constructor(
    private snackBar: MatSnackBar,
    private surveys: SurveyService,
    private router: Router,
    private route: ActivatedRoute
  ) { }

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

  public toPreview({survey, preview}: {survey: Survey, preview: boolean}): void {
    this.survey = survey;
    if(preview) this.preview = true;
    else this.submit(survey);
  }

  public submit(survey?: Survey): void {
    if (!survey) return this.edit();
    survey = JSON.parse(JSON.stringify(survey)); // create copy of the input that we can modify freely
    for (let qg of survey!.questionGroups) {
      if((qg.id || 0) < 0) delete qg.id;
      for (let question of qg.questions) {
        if((question.id || 0) < 0) delete question.id;
        if (question.choices) {
          for (let checkbox of question.choices) {
            if((checkbox.id || 0) < 0) delete checkbox.id;
          }
        }
      }
    }
    this.busy$.next(true);
    (
      this.isEdit ?
        this.surveys.editSurvey(survey!) :
        this.surveys.createSurvey(survey!)
    ).subscribe({
      next: surv => {
        this.busy$.next(false);
        this.router.navigate(["/survey", "access", surv.accessId], { relativeTo: this.route });
      },
      error: err => {
        this.busy$.next(false);
        this.survey!.questionGroups = survey!.questionGroups;
        this.snackBar.open("Unbekannter Fehler beim Erstellen der Umfrage", "OK");
      }
    });
  }

  public edit(): void {
    this.preview = false;
  }
}