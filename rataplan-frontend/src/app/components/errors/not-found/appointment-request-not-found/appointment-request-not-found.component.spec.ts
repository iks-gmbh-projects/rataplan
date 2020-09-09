import { async, ComponentFixture, TestBed } from '@angular/core/testing';

import { AppointmentRequestNotFoundComponent } from './appointment-request-not-found.component';

xdescribe('AppointmentRequestNotFoundComponent', () => {
  let component: AppointmentRequestNotFoundComponent;
  let fixture: ComponentFixture<AppointmentRequestNotFoundComponent>;

  beforeEach(async(() => {
    TestBed.configureTestingModule({
      declarations: [ AppointmentRequestNotFoundComponent ]
    })
    .compileComponents();
  }));

  beforeEach(() => {
    fixture = TestBed.createComponent(AppointmentRequestNotFoundComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should be created', () => {
    expect(component).toBeTruthy();
  });
});
