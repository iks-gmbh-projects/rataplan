import { ComponentFixture, TestBed } from '@angular/core/testing';

import { SurveyCreateFormHeadComponent } from './survey-create-form-head.component';

describe('SurveyCreateFormHeadComponent', () => {
  let component: SurveyCreateFormHeadComponent;
  let fixture: ComponentFixture<SurveyCreateFormHeadComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      declarations: [ SurveyCreateFormHeadComponent ]
    })
    .compileComponents();
  });

  beforeEach(() => {
    fixture = TestBed.createComponent(SurveyCreateFormHeadComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
