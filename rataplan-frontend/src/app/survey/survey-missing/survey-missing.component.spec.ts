import { ComponentFixture, TestBed } from '@angular/core/testing';

import { SurveyMissingComponent } from './survey-missing.component';

describe('SurveyMissingComponent', () => {
  let component: SurveyMissingComponent;
  let fixture: ComponentFixture<SurveyMissingComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      declarations: [ SurveyMissingComponent ]
    })
    .compileComponents();
  });

  beforeEach(() => {
    fixture = TestBed.createComponent(SurveyMissingComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
