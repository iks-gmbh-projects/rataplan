import { Component, OnDestroy, OnInit } from '@angular/core';
import { ActivatedRoute } from '@angular/router';
import { Subscription } from 'rxjs';
import { SurveyHead } from '../survey.model';
import { SurveyService } from '../survey.service';
import {AppointmentRequestModel} from "../../models/appointment-request.model";
import {DashboardService} from "../../services/dashboard-service/dashboard.service";
import {formatDate} from "@angular/common";

@Component({
  selector: 'app-survey-list',
  templateUrl: './survey-list.component.html',
  styleUrls: ['./survey-list.component.css']
})
export class SurveyListComponent implements OnInit, OnDestroy {
  public surveys: SurveyHead[] = [];
  public createdVotes: AppointmentRequestModel[] = [];
  public participatedVotes: AppointmentRequestModel[] = [];
  public currentDate: string = formatDate(new Date(),'dd-MM-yyyy','de_DE');
  public busy: boolean = false;
  public error: any = null;
  public isOwn: boolean = false;
  private sub?: Subscription;

  constructor(private surveyService: SurveyService, private dashboardService: DashboardService, private activeRoute: ActivatedRoute) { }

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
    if (this.busy) return;
    this.busy = true;
    this.error = null;
    let request = this.isOwn ? this.surveyService.getOwnSurveys() : this.surveyService.getOpenSurveys()
    request.subscribe({
      next: s => this.surveys = s,
      error: err => {
        this.error = err;
        this.busy = false;
      },
      complete: () => this.busy = false,
    });

    this.dashboardService.getCreatedVotes().subscribe(res => {
      this.createdVotes = res;
    });

    this.dashboardService.getParticipatedVotes().subscribe(res => {
      this.participatedVotes = res;
    });
  }
}
