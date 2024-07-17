import { HttpClientTestingModule } from '@angular/common/http/testing';
import { ComponentFixture, TestBed } from '@angular/core/testing';
import { MatLegacyButtonModule as MatButtonModule } from '@angular/material/legacy-button';
import { MatLegacyCardModule as MatCardModule } from '@angular/material/legacy-card';
import { MatIconModule } from '@angular/material/icon';
import { MatProgressSpinnerModule } from '@angular/material/progress-spinner';
import { RouterTestingModule } from '@angular/router/testing';

import { SurveyService } from '../survey.service';
import { SurveyListComponent } from './survey-list.component';

describe('SurveyListComponent', () => {
  let component: SurveyListComponent;
  let fixture: ComponentFixture<SurveyListComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      declarations: [ SurveyListComponent ],
      imports: [ RouterTestingModule, HttpClientTestingModule, MatCardModule, MatButtonModule, MatProgressSpinnerModule, MatIconModule ],
      providers: [ SurveyService ]
    })
      .compileComponents();
  });

  beforeEach(() => {
    fixture = TestBed.createComponent(SurveyListComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
