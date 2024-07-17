import { ComponentFixture, TestBed } from '@angular/core/testing';
import { HttpClientTestingModule } from '@angular/common/http/testing';

import { SurveyService } from '../survey.service';

import { SurveyCreateComponent } from './survey-create.component';
import { RouterTestingModule } from '@angular/router/testing';
import { SurveyCreateFormComponent } from './survey-create-form/survey-create-form.component';
import { MatStepperModule } from '@angular/material/stepper';
import { SurveyPreviewComponent } from './survey-preview/survey-preview.component';
import { NoopAnimationsModule } from '@angular/platform-browser/animations';
import { MatLegacyButtonModule as MatButtonModule } from '@angular/material/legacy-button';
import { MatLegacyCardModule as MatCardModule } from '@angular/material/legacy-card';

describe('SurveyCreateComponent', () => {
  let component: SurveyCreateComponent;
  let fixture: ComponentFixture<SurveyCreateComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      declarations: [ SurveyCreateComponent, SurveyCreateFormComponent, SurveyPreviewComponent ],
      imports: [ HttpClientTestingModule, RouterTestingModule, MatStepperModule, NoopAnimationsModule, MatButtonModule, MatCardModule ],
      providers: [ SurveyService ],
    })
    .compileComponents();
  });

  beforeEach(() => {
    fixture = TestBed.createComponent(SurveyCreateComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
