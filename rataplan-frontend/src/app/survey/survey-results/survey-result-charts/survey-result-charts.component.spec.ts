import { ComponentFixture, TestBed } from '@angular/core/testing';

import { SurveyResultChartsComponent } from './survey-result-charts.component';

describe('SurveyResultChartsComponent', () => {
  let component: SurveyResultChartsComponent;
  let fixture: ComponentFixture<SurveyResultChartsComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [SurveyResultChartsComponent]
    })
    .compileComponents();

    fixture = TestBed.createComponent(SurveyResultChartsComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
