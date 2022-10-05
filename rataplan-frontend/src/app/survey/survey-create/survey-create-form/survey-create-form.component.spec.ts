import { ComponentFixture, TestBed } from '@angular/core/testing';

import { SurveyCreateFormComponent } from './survey-create-form.component';

describe('SurveyCreateFormComponent', () => {
  let component: SurveyCreateFormComponent;
  let fixture: ComponentFixture<SurveyCreateFormComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      declarations: [ SurveyCreateFormComponent ]
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
