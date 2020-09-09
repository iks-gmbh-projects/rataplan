import { async, ComponentFixture, TestBed } from '@angular/core/testing';

import { ShowAppointmentMemberRowComponent } from './show-appointment-member-row.component';

xdescribe('ShowAppointmentMemberRowComponent', () => {
  let component: ShowAppointmentMemberRowComponent;
  let fixture: ComponentFixture<ShowAppointmentMemberRowComponent>;

  beforeEach(async(() => {
    TestBed.configureTestingModule({
      declarations: [ ShowAppointmentMemberRowComponent ]
    })
    .compileComponents();
  }));

  beforeEach(() => {
    fixture = TestBed.createComponent(ShowAppointmentMemberRowComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should be created', () => {
    expect(component).toBeTruthy();
  });
});
