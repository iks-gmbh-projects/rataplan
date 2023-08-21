import { ComponentFixture, TestBed } from '@angular/core/testing';

import { SurveyUnknownErrorComponent } from './survey-unknown-error.component';

describe('SurveyUnknownErrorComponent', () => {
  let component: SurveyUnknownErrorComponent;
  let fixture: ComponentFixture<SurveyUnknownErrorComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      declarations: [ SurveyUnknownErrorComponent ]
    })
    .compileComponents();
  });

  beforeEach(() => {
    fixture = TestBed.createComponent(SurveyUnknownErrorComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
