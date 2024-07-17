import { HttpClientTestingModule } from '@angular/common/http/testing';
import { ComponentFixture, TestBed } from '@angular/core/testing';
import { MatLegacyButtonModule as MatButtonModule } from '@angular/material/legacy-button';
import { MatExpansionModule } from '@angular/material/expansion';
import { MatIconModule } from '@angular/material/icon';
import { MatProgressSpinnerModule } from '@angular/material/progress-spinner';
import { MatLegacyTableModule as MatTableModule } from '@angular/material/legacy-table';
import { RouterTestingModule } from '@angular/router/testing';
import { SurveyService } from '../survey.service';

import { SurveyResultsComponent } from './survey-results.component';

describe('SurveyResultsComponent', () => {
  let component: SurveyResultsComponent;
  let fixture: ComponentFixture<SurveyResultsComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      declarations: [ SurveyResultsComponent ],
      imports: [ RouterTestingModule, HttpClientTestingModule, MatTableModule, MatExpansionModule, MatProgressSpinnerModule, MatButtonModule, MatIconModule ],
      providers: [ SurveyService ],
    })
    .compileComponents();
  });

  beforeEach(() => {
    fixture = TestBed.createComponent(SurveyResultsComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
