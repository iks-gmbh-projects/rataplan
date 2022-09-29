import { Component, OnDestroy, OnInit } from '@angular/core';
import { ActivatedRoute, Router } from '@angular/router';
import { Subscription } from 'rxjs';
import { Survey } from '../../survey.model';

@Component({
  selector: 'app-survey-overview',
  templateUrl: './survey-overview.component.html',
  styleUrls: ['./survey-overview.component.css']
})
export class SurveyOverviewComponent implements OnInit, OnDestroy {
  public survey?:Survey;

  private sub?:Subscription;

  constructor(private router:Router, private route:ActivatedRoute) { }

  public ngOnInit(): void {
    this.survey = this.route.snapshot.data['survey'];
    this.sub = this.route.data.subscribe(d => this.survey = d['survey']);
  }

  public ngOnDestroy(): void {
    this.sub?.unsubscribe();
  }

  public willOpen(): boolean {
    return this.survey!.startDate > new Date();
  }

  public isOpen(): boolean {
    return this.survey!.startDate <= new Date() && this.survey!.endDate > new Date();
  }

  public hasClosed(): boolean {
    return this.survey!.endDate <= new Date();
  }
}
