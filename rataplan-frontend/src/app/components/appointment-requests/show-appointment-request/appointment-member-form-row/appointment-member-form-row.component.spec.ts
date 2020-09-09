import { async, ComponentFixture, TestBed } from '@angular/core/testing';

import { AppointmentMemberFormRowComponent } from './appointment-member-form-row.component';

xdescribe('AppointmentMemberFormComponent', () => {
  let component: AppointmentMemberFormRowComponent;
  let fixture: ComponentFixture<AppointmentMemberFormRowComponent>;

  beforeEach(async(() => {
    TestBed.configureTestingModule({
      declarations: [ AppointmentMemberFormRowComponent ]
    })
    .compileComponents();
  }));

  beforeEach(() => {
    fixture = TestBed.createComponent(AppointmentMemberFormRowComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should be created', () => {
    expect(component).toBeTruthy();
  });
});
