import { Clipboard } from '@angular/cdk/clipboard';
import { Component, OnDestroy, OnInit } from '@angular/core';
import { MatSnackBar } from '@angular/material/snack-bar';
import { ActivatedRoute, Router } from '@angular/router';
import { Subscription } from 'rxjs';
import { Survey } from '../survey.model';

@Component({
  selector: 'app-survey-view',
  templateUrl: './survey-owner-view.component.html',
  styleUrls: ['./survey-owner-view.component.css']
})
export class SurveyOwnerViewComponent implements OnInit, OnDestroy {
  public survey: Survey|null = null;
  public expired: boolean = false;

  private sub: Subscription|null = null;

  constructor(private router: Router, private route: ActivatedRoute, public clipboard: Clipboard, public snackBars: MatSnackBar) { }

  public ngOnInit(): void {
    this.survey = this.route.snapshot.data['survey'];
    this.expired = this.survey!.endDate < new Date();
    this.sub = this.route.data.subscribe(d => {
      this.survey = d['survey'];
      this.expired = this.survey!.endDate < new Date();
    });
    setInterval(() => this.expired = this.survey!.endDate < new Date(), 1000);
  }

  public ngOnDestroy(): void {
    this.sub?.unsubscribe();
  }
}
