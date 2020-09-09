import { async, ComponentFixture, TestBed } from '@angular/core/testing';

import { MobileAppointmentMemberFormComponent } from './mobile-appointment-member-form.component';

xdescribe('MobileAppointmentMemberFormComponent', () => {
  let component: MobileAppointmentMemberFormComponent;
  let fixture: ComponentFixture<MobileAppointmentMemberFormComponent>;

  beforeEach(async(() => {
    TestBed.configureTestingModule({
      declarations: [ MobileAppointmentMemberFormComponent ]
    })
    .compileComponents();
  }));

  beforeEach(() => {
    fixture = TestBed.createComponent(MobileAppointmentMemberFormComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should be created', () => {
    expect(component).toBeTruthy();
  });
});
