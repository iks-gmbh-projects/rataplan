import { async, ComponentFixture, TestBed } from '@angular/core/testing';

import { UserAppointmentRequestListComponent } from './user-appointment-request-list.component';

xdescribe('UserAppointmentRequestListComponent', () => {
  let component: UserAppointmentRequestListComponent;
  let fixture: ComponentFixture<UserAppointmentRequestListComponent>;

  beforeEach(async(() => {
    TestBed.configureTestingModule({
      declarations: [ UserAppointmentRequestListComponent ]
    })
    .compileComponents();
  }));

  beforeEach(() => {
    fixture = TestBed.createComponent(UserAppointmentRequestListComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should be created', () => {
    expect(component).toBeTruthy();
  });
});
