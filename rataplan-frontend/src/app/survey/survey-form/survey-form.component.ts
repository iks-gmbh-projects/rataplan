import { HttpErrorResponse } from '@angular/common/http';
import { Component, OnDestroy, OnInit, QueryList, ViewChildren } from '@angular/core';
import { MatLegacyDialog as MatDialog } from '@angular/material/legacy-dialog';
import { MatLegacySnackBar as MatSnackBar } from '@angular/material/legacy-snack-bar';
import { ActivatedRoute } from '@angular/router';
import { BehaviorSubject, combineLatest, delay, map, Observable, of, ReplaySubject, startWith, Subscription, switchMap } from 'rxjs';
import { Answer, Survey } from '../survey.model';
import { SurveyService } from '../survey.service';
import { PageComponent } from './page/page.component';
import { SurveyAnswerComponent } from './survey-answer/survey-answer.component';

@Component({
  selector: 'app-survey-form',
  templateUrl: './survey-form.component.html',
  styleUrls: ['./survey-form.component.css'],
})

export class SurveyFormComponent implements OnInit, OnDestroy {
  public survey?: Survey;
  public page = 0;
  readonly busy$ = new BehaviorSubject<boolean>(false);
  readonly delayedBusy$: Observable<boolean> = this.busy$.pipe(
    switchMap(v => v ? of(v).pipe(delay(1000)) : of(v)),
  );
  private answers: {[groupId: string | number]: {[rank: string | number]: Answer}} = {};
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
            startWith(page.form!.invalid),
          ),
        ),
      )),
      map(values => values.some(v => v)),
    )),
    startWith(false),
  );
  
  @ViewChildren('surveyPage') set pages(pages: QueryList<PageComponent>) {
    this.pagesSubject.next(pages);
  }
  
  constructor(
    private route: ActivatedRoute,
    private surveys: SurveyService,
    private snackBars: MatSnackBar,
    private dialogs: MatDialog,
  )
  {
  }
  
  public ngOnInit(): void {
    this.survey = this.route.snapshot.data['survey'];
    this.sub = this.route.data.subscribe(data => {
      if(this.survey !== data['survey']) {
        this.survey = data['survey'];
        this.answers = {};
      }
    });
  }
  
  public ngOnDestroy(): void {
    this.sub?.unsubscribe();
  }
  
  public pageSubmit(answers?: {[rank: string | number]: Answer}) {
    if(!this.survey) return;
    if(answers) {
      this.answers = {
        ...this.answers,
        [this.survey.questionGroups[this.page].id!]: answers,
      };
      this.page++;
    } else {
      this.page--;
    }
  }
  
  public submit(): void {
    if(!this.survey) return;
    if(this.page >= this.survey.questionGroups.length) {
      this.busy$.next(true)
      this.surveys.answerSurvey({
        surveyId: this.survey.id!,
        answers: this.answers,
      }).subscribe({
        next: () => {
          this.busy$.next(false);
          this.dialogs.open(SurveyAnswerComponent);
        },
        error: (err: HttpErrorResponse) => {
          this.busy$.next(false);
          this.page--;
          switch(err.status) {
          case 409:
            this.snackBars.open('Sie haben bereits teilgenommen.', 'OK');
            break;
          case 422:
            this.snackBars.open('Hochladen nicht erfolgreich, Antwort oder Umfrage war ungültig.', 'OK');
            break;
          default:
            this.snackBars.open('Fehler beim Hochladen der Antwort: ' + err.status, 'OK');
            break;
          }
        },
      });
    }
  }
}