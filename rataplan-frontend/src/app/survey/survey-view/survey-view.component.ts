import { Component, OnDestroy, OnInit } from '@angular/core';
import { ActivatedRoute, Router } from '@angular/router';
import { Subscription } from 'rxjs';
import { Survey } from '../survey.model';

@Component({
  selector: 'app-survey-view',
  templateUrl: './survey-view.component.html',
  styleUrls: ['./survey-view.component.css']
})
export class SurveyViewComponent implements OnInit, OnDestroy {
  public survey: Survey|null = null;

  private sub: Subscription|null = null;

  constructor(private router: Router, private route: ActivatedRoute) { }

  public ngOnInit(): void {
    this.survey = this.route.snapshot.data['survey'];
    this.sub = this.route.data.subscribe(d => this.survey = d['survey']);
  }

  public ngOnDestroy(): void {
    this.sub?.unsubscribe();
  }

  public ensureDate(input: Date|number|string): string {
    let ret = new Date(input).toISOString().split('T')[0];
    return ret;
  }

}
