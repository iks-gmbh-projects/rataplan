import { async, ComponentFixture, TestBed } from '@angular/core/testing';

import { MobileAppointmentRowsComponent } from './mobile-appointment-rows.component';

xdescribe('MobileAppointmentRowsComponent', () => {
  let component: MobileAppointmentRowsComponent;
  let fixture: ComponentFixture<MobileAppointmentRowsComponent>;

  beforeEach(async(() => {
    TestBed.configureTestingModule({
      declarations: [ MobileAppointmentRowsComponent ]
    })
    .compileComponents();
  }));

  beforeEach(() => {
    fixture = TestBed.createComponent(MobileAppointmentRowsComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should be created', () => {
    expect(component).toBeTruthy();
  });
});
