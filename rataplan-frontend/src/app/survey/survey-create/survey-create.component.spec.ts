import { ComponentFixture, TestBed } from '@angular/core/testing';
import { provideHttpClientTesting } from '@angular/common/http/testing';

import { SurveyService } from '../survey.service';

import { SurveyCreateComponent } from './survey-create.component';
import { RouterTestingModule } from '@angular/router/testing';
import { SurveyCreateFormComponent } from './survey-create-form/survey-create-form.component';
import { MatStepperModule } from '@angular/material/stepper';
import { SurveyPreviewComponent } from './survey-preview/survey-preview.component';
import { NoopAnimationsModule } from '@angular/platform-browser/animations';
import { MatButtonModule } from '@angular/material/button';
import { MatCardModule } from '@angular/material/card';
import { provideHttpClient, withInterceptorsFromDi } from '@angular/common/http';

describe('SurveyCreateComponent', () => {
  let component: SurveyCreateComponent;
  let fixture: ComponentFixture<SurveyCreateComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
    declarations: [SurveyCreateComponent, SurveyCreateFormComponent, SurveyPreviewComponent],
    imports: [RouterTestingModule, MatStepperModule, NoopAnimationsModule, MatButtonModule, MatCardModule],
    providers: [SurveyService, provideHttpClient(withInterceptorsFromDi()), provideHttpClientTesting()]
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
