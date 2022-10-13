import { NgxMatTimepickerModule } from '@angular-material-components/datetime-picker';
import { HttpClientTestingModule } from '@angular/common/http/testing';
import { ComponentFixture, TestBed } from '@angular/core/testing';
import { ReactiveFormsModule } from '@angular/forms';
import { MatButtonModule } from '@angular/material/button';
import { MatCheckboxModule } from '@angular/material/checkbox';
import { MatDatepickerModule } from '@angular/material/datepicker';
import { MatFormFieldModule } from '@angular/material/form-field';
import { MatIconModule } from '@angular/material/icon';
import { MatStepperModule } from '@angular/material/stepper';
import { NoopAnimationsModule } from '@angular/platform-browser/animations';
import { RouterTestingModule } from '@angular/router/testing';

import { SurveyCreateFormComponent } from './survey-create-form.component';

describe('SurveyCreateFormComponent', () => {
  let component: SurveyCreateFormComponent;
  let fixture: ComponentFixture<SurveyCreateFormComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      declarations: [ SurveyCreateFormComponent ],
      imports: [ RouterTestingModule, ReactiveFormsModule, MatStepperModule, NoopAnimationsModule, MatIconModule, MatFormFieldModule, MatCheckboxModule, MatButtonModule, MatDatepickerModule, NgxMatTimepickerModule ],
    })
    .compileComponents();
  });

  beforeEach(() => {
    fixture = TestBed.createComponent(SurveyCreateFormComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
