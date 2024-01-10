import { ComponentFixture, TestBed } from '@angular/core/testing';

import { SurveyForbiddenComponent } from './survey-forbidden.component';

describe('SurveyForbiddenComponent', () => {
  let component: SurveyForbiddenComponent;
  let fixture: ComponentFixture<SurveyForbiddenComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      declarations: [ SurveyForbiddenComponent ]
    })
    .compileComponents();
  });

  beforeEach(() => {
    fixture = TestBed.createComponent(SurveyForbiddenComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
