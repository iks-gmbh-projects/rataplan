import { Component, OnInit } from '@angular/core';
import { Subscription } from 'rxjs';
import { Survey } from '../survey.model';
import { SurveyService } from '../survey.service';

@Component({
  selector: 'app-survey-list',
  templateUrl: './survey-list.component.html',
  styleUrls: ['./survey-list.component.css']
})
export class SurveyListComponent implements OnInit {
  public surveys: Survey[] = [];
  public busy: boolean = false;
  public error: any = null;

  constructor(private surveyService: SurveyService) { }

  public ngOnInit(): void {
    this.updateList();
  }

  public updateList(): void {
    if (this.busy) return;
    this.busy = true;
    this.surveyService.getOpenSurveys().subscribe({
      next: s => this.surveys = s,
      error: err => this.error = err,
      complete: () => this.busy = false,
    });
  }
}
