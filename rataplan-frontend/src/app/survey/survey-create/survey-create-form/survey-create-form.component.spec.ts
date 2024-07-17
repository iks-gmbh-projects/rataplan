import { ComponentFixture, TestBed } from '@angular/core/testing';
import { ReactiveFormsModule } from '@angular/forms';
import { MatDatepickerModule } from '@angular/material/datepicker';
import { MatIconModule } from '@angular/material/icon';
import { MatLegacyButtonModule as MatButtonModule } from '@angular/material/legacy-button';
import { MatCheckboxModule } from '@angular/material/checkbox';
import { MatFormFieldModule } from '@angular/material/form-field';
import { MatStepperModule } from '@angular/material/stepper';
import { NoopAnimationsModule } from '@angular/platform-browser/animations';
import { RouterTestingModule } from '@angular/router/testing';
import { MtxDatetimepickerModule } from '@ng-matero/extensions/datetimepicker';

import { SurveyCreateFormComponent } from './survey-create-form.component';

describe('SurveyCreateFormComponent', () => {
  let component: SurveyCreateFormComponent;
  let fixture: ComponentFixture<SurveyCreateFormComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      declarations: [ SurveyCreateFormComponent ],
      imports: [ RouterTestingModule, ReactiveFormsModule, MatStepperModule, NoopAnimationsModule, MatIconModule, MatFormFieldModule, MatCheckboxModule, MatButtonModule, MatDatepickerModule, MtxDatetimepickerModule ],
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