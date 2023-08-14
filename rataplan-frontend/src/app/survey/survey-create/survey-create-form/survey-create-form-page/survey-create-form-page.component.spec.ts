import { ComponentFixture, TestBed } from '@angular/core/testing';

import { SurveyCreateFormPageComponent } from './survey-create-form-page.component';

describe('SurveyCreateFormPageComponent', () => {
  let component: SurveyCreateFormPageComponent;
  let fixture: ComponentFixture<SurveyCreateFormPageComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      declarations: [ SurveyCreateFormPageComponent ]
    })
    .compileComponents();
  });

  beforeEach(() => {
    fixture = TestBed.createComponent(SurveyCreateFormPageComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
