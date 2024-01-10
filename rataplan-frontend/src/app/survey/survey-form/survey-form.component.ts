import { HttpErrorResponse } from '@angular/common/http';
import { Component, OnDestroy, OnInit, QueryList, ViewChildren } from '@angular/core';
import { MatDialog } from '@angular/material/dialog';
import { MatSnackBar } from '@angular/material/snack-bar';
import { ActivatedRoute, Router } from '@angular/router';
import { combineLatest, map, Observable, ReplaySubject, startWith, Subscription, switchMap } from 'rxjs';
import { Answer, Survey } from '../survey.model';
import { SurveyService } from '../survey.service';
import { SurveyAnswerComponent } from './survey-answer/survey-answer.component';
import { PageComponent } from './page/page.component';

@Component({
  selector: 'app-survey-form',
  templateUrl: './survey-form.component.html',
  styleUrls: ['./survey-form.component.css'],
})

export class SurveyFormComponent implements OnInit, OnDestroy {
  public survey?: Survey;
  public page = 0;
  public busy = false;
  private answers: { [key: string | number]: Answer } = {};
  private sub?: Subscription;
  private readonly pagesSubject = new ReplaySubject<QueryList<PageComponent>>(1);
  readonly isResponseValid: Observable<boolean> = this.pagesSubject.pipe(
    switchMap(pages => pages.changes.pipe(
      map(() => pages),
      startWith(pages),
      switchMap(pages => combineLatest(
        pages.map(
          page => page.form!.statusChanges!.pipe(
            map(() => page.form!.invalid),
            startWith(page.form!.invalid)
          )
        ),
      )),
      map(values => values.some(v => v))
    )),
    startWith(false)
  );

  @ViewChildren("surveyPage") set pages(pages: QueryList<PageComponent>) {
    this.pagesSubject.next(pages);
  }

  constructor(private route: ActivatedRoute, private router: Router, private surveys: SurveyService, private snackBars: MatSnackBar, private dialogs: MatDialog) {
  }

  public ngOnInit(): void {
    this.survey = this.route.snapshot.data['survey'];
    this.sub = this.route.data.subscribe(data => {
      if (this.survey !== data['survey']) {
        this.survey = data['survey'];
        this.answers = {};
      }
    });
  }

  public ngOnDestroy(): void {
    this.sub?.unsubscribe();
  }

  public pageSubmit(answers?: { [key: string | number]: Answer }) {
    if (!this.survey) return;
    if (answers) {
      this.answers = {...this.answers, ...answers};
      this.page++;
    } else {
      this.page--;
    }
  }

  public submit(): void {
    if (!this.survey) return;
    if (this.page >= this.survey.questionGroups.length) {
      this.busy = true;
      this.surveys.answerSurvey({
        surveyId: this.survey.id!,
        answers: this.answers,
      }).subscribe({
        next: () => {
          this.busy = false;
          this.dialogs.open(SurveyAnswerComponent);
        },
        error: (err:HttpErrorResponse) => {
          this.busy = false;
          this.page--;
          switch(err.status) {
          case 409:
            this.snackBars.open('Sie haben bereits teilgenommen.', 'OK');
            break;
          case 422:
            this.snackBars.open('Hochladen nicht erfolgreich, Antwort oder Umfrage war ungültig.', 'OK');
            break;
          default:
            this.snackBars.open('Fehler beim Hochladen der Antwort: '+err.status, 'OK');
            break;
          }
        },
      });
    }
  }
}
