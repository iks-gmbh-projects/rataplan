import { ComponentFixture, TestBed } from '@angular/core/testing';

import { SurveyClosedComponent } from './survey-closed.component';

describe('SurveyClosedComponent', () => {
  let component: SurveyClosedComponent;
  let fixture: ComponentFixture<SurveyClosedComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      declarations: [ SurveyClosedComponent ]
    })
    .compileComponents();
  });

  beforeEach(() => {
    fixture = TestBed.createComponent(SurveyClosedComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
