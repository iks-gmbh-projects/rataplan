import { ComponentFixture, TestBed } from '@angular/core/testing';
import { MatButtonModule } from '@angular/material/button';
import { MatDialogModule, MatDialogRef, MAT_DIALOG_DATA } from '@angular/material/dialog';
import { RouterTestingModule } from '@angular/router/testing';

import { SurveyAnswerComponent } from './survey-answer.component';

describe('SurveyAnswerComponent', () => {
  let component: SurveyAnswerComponent;
  let fixture: ComponentFixture<SurveyAnswerComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      declarations: [SurveyAnswerComponent],
      imports: [MatDialogModule, RouterTestingModule, MatButtonModule],
      providers: [
        { provide: MatDialogRef, useValue: {} },
        { provide: MAT_DIALOG_DATA, useValue: {} },
      ],
    })
      .compileComponents();
  });

  beforeEach(() => {
    fixture = TestBed.createComponent(SurveyAnswerComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
