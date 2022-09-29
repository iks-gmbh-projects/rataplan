import { ComponentFixture, TestBed } from '@angular/core/testing';

import { SurveyOwnerViewComponent } from './survey-owner-view.component';

describe('SurveyOwnerViewComponent', () => {
  let component: SurveyOwnerViewComponent;
  let fixture: ComponentFixture<SurveyOwnerViewComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      declarations: [ SurveyOwnerViewComponent ]
    })
    .compileComponents();
  });

  beforeEach(() => {
    fixture = TestBed.createComponent(SurveyOwnerViewComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
