import { Component, OnInit } from '@angular/core';
import { throwToolbarMixedModesError } from '@angular/material/toolbar';
import { SurveyHead } from '../survey.model';
import { SurveyService } from '../survey.service';

@Component({
  selector: 'app-survey-list',
  templateUrl: './survey-list.component.html',
  styleUrls: ['./survey-list.component.css']
})
export class SurveyListComponent implements OnInit {
  public surveys: SurveyHead[] = [];
  public busy: boolean = false;
  public error: any = null;

  constructor(private surveyService: SurveyService) { }

  public ngOnInit(): void {
    this.updateList();
  }

  public updateList(): void {
    if (this.busy) return;
    this.busy = true;
    this.error = null;
    this.surveyService.getOpenSurveys().subscribe({
      next: s => this.surveys = s,
      error: err => {
        this.error = err;
        this.busy = false;
      },
      complete: () => this.busy = false,
    });
  }
}
