import { Component, OnDestroy, OnInit } from '@angular/core';
import { ActivatedRoute } from '@angular/router';
import { BehaviorSubject, delay, Observable, of, Subscription } from 'rxjs';
import { switchMap } from 'rxjs/operators';

import { SurveyHead } from '../survey.model';
import { SurveyService } from '../survey.service';
import { MatSnackBar } from '@angular/material/snack-bar';
import { Clipboard } from '@angular/cdk/clipboard';

@Component({
  selector: 'app-survey-list',
  templateUrl: './survey-list.component.html',
  styleUrls: ['./survey-list.component.css'],
})
export class SurveyListComponent implements OnInit, OnDestroy {
  public surveys: SurveyHead[] = [];
  readonly busy$ = new BehaviorSubject<boolean>(false);
  readonly delayedBusy$: Observable<boolean> = this.busy$.pipe(
    switchMap(v => v ? of(v).pipe(delay(1000)) : of(v)),
  );
  public error: any = null;
  public isOwn = false;
  private sub?: Subscription;

  constructor(
    private surveyService: SurveyService,
    private activeRoute: ActivatedRoute,
    readonly snackBars: MatSnackBar,
    readonly clipboard: Clipboard) {
  }

  public expired(survey: SurveyHead): boolean {
    return survey.endDate < new Date();
  }

  public ngOnInit(): void {
    this.isOwn = this.activeRoute.snapshot.data['own'];
    this.updateList();
    this.sub = this.activeRoute.data.subscribe(data => {
      this.isOwn = data['own'];
      this.updateList();
    });
  }

  public ngOnDestroy(): void {
    this.sub?.unsubscribe();
  }

  public updateList(): void {
    if(this.busy$.value) return;
    this.busy$.next(true);
    this.error = null;
    const request = this.isOwn ? this.surveyService.getOwnSurveys() : this.surveyService.getOpenSurveys();
    request.subscribe({
      next: s => this.surveys = s,
      error: err => {
        this.error = err;
        this.busy$.next(false);
      },
      complete: () => this.busy$.next(false),
    });
  }
}