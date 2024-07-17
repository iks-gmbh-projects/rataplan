import { Clipboard } from '@angular/cdk/clipboard';
import { Component, OnDestroy, OnInit } from '@angular/core';
import { MatLegacySnackBar as MatSnackBar } from '@angular/material/legacy-snack-bar';
import { ActivatedRoute } from '@angular/router';
import { Subscription } from 'rxjs';
import { Survey } from '../survey.model';

@Component({
  selector: 'app-survey-view',
  templateUrl: './survey-owner-view.component.html',
  styleUrls: ['./survey-owner-view.component.css']
})
export class SurveyOwnerViewComponent implements OnInit, OnDestroy {
  public survey?: Survey;
  public expired: boolean = false;

  private sub?: Subscription;
  private timeout?: any;

  constructor(private route: ActivatedRoute, public clipboard: Clipboard, public snackBars: MatSnackBar) { }

  public ngOnInit(): void {
    this.survey = this.route.snapshot.data['survey'];
    this.expired = this.survey!.endDate < new Date();
    this.sub = this.route.data.subscribe(d => {
      this.survey = d['survey'];
      this.expired = this.survey!.endDate < new Date();
    });
    this.timeout = setInterval(() => this.expired = this.survey!.endDate < new Date(), 1000);
  }

  public ngOnDestroy(): void {
    this.sub?.unsubscribe();
    clearInterval(this.timeout);
  }
}