import { ComponentFixture, TestBed } from '@angular/core/testing';

import { AppointmentRequestFormComponent } from './appointment-request-form.component';

describe('AppointmentRequestFormComponent', () => {
  let component: AppointmentRequestFormComponent;
  let fixture: ComponentFixture<AppointmentRequestFormComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      declarations: [AppointmentRequestFormComponent],
    })
      .compileComponents();
  });

  beforeEach(() => {
    fixture = TestBed.createComponent(AppointmentRequestFormComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
