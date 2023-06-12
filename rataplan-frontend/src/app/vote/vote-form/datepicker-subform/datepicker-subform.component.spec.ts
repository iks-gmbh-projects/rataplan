import { ComponentFixture, TestBed } from '@angular/core/testing';

import { DatepickerSubformComponent } from './datepicker-subform.component';

describe('CalendarSubformComponent', () => {
  let component: DatepickerSubformComponent;
  let fixture: ComponentFixture<DatepickerSubformComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      declarations: [DatepickerSubformComponent],
    })
      .compileComponents();
  });

  beforeEach(() => {
    fixture = TestBed.createComponent(DatepickerSubformComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
