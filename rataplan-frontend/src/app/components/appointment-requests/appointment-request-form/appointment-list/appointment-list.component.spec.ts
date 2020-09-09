/*import {AppointmentListComponent} from "./appointment-list.component";
/!* tslint:disable:no-unused-variable *!/
import {BrowserAnimationsModule} from "@angular/platform-browser/animations";
import {Appointment} from "../../../../models/appointment-requests/appointment";
import {async, ComponentFixture, TestBed} from '@angular/core/testing';
import {DebugElement} from "@angular/core";

describe('AppointmentListComponent', () => {
  let component: AppointmentListComponent;
  let fixture: ComponentFixture<AppointmentListComponent>;
  let de: DebugElement;
  let el: any;

  let btnDelete;
  let appointmentRows;

  beforeEach(() => {
    TestBed.configureTestingModule({
      declarations: [AppointmentListComponent],
      imports: [BrowserAnimationsModule]
    })
  });

  beforeEach(() => {
    fixture = TestBed.createComponent(AppointmentListComponent);
    component = fixture.componentInstance;
    de = fixture.debugElement;
    el = de.nativeElement;
  });

  it('should create', () => {
    component.appointments = [new Appointment(), new Appointment()];
    fixture.detectChanges();

    expect(component).toBeTruthy();
  });

  it('should call deleteAppointment', () => {
    component.appointments = [new Appointment(), new Appointment()];
    fixture.detectChanges();

    spyOn(component, 'deleteAppointment');

    btnDelete = el.querySelector('.appointment-delete');
    btnDelete.dispatchEvent(new Event('click'));

    expect(component.deleteAppointment).toHaveBeenCalledWith(1);
  });

  it('should show appointment-data with date-order', () => {
    let appointment = new Appointment();
    appointment.description = "IKS Hilden";
    appointment.date = "2017-08-10";
    appointment.startTime = "00:00";
    appointment.endTime = "23:59";
    appointment.url = "Schwimmsachen nicht vergessen!";

    component.appointments = [appointment];
    fixture.detectChanges();

    appointmentRows = el.querySelectorAll('.appointment-borders');
    expect(appointmentRows.length).toBe(1);

    let innerDivs = appointmentRows[0].querySelectorAll('.appointment-float');
    expect(innerDivs.length).toBe(5);
    expect(innerDivs[0].innerText).toContain("10.08.2017");
    expect(innerDivs[1].innerText).toContain("00:00");
    expect(innerDivs[2].innerText).toContain("23:59");
    expect(innerDivs[3].innerText).toContain("IKS Hilden");
    expect(innerDivs[4].innerText).toContain("Schwimmsachen nicht vergessen!");

    expect(component).toBeTruthy();
  });

  it('should show appointment-data with no-date-order', () => {
    let appointment = new Appointment();
    appointment.description = "IKS Hilden";
    appointment.startTime = "00:00";
    appointment.endTime = "23:59";
    appointment.url = "Schwimmsachen nicht vergessen!";

    component.appointments = [appointment];
    fixture.detectChanges();

    appointmentRows = el.querySelectorAll('.appointment-borders');
    expect(appointmentRows.length).toBe(1);

    let innerDivs = appointmentRows[0].querySelectorAll('.appointment-float');
    expect(innerDivs.length).toBe(4);
    expect(innerDivs[0].innerText).toContain("Schwimmsachen nicht vergessen!");
    expect(innerDivs[1].innerText).toContain("00:00");
    expect(innerDivs[2].innerText).toContain("23:59");
    expect(innerDivs[3].innerText).toContain("IKS Hilden");

    expect(component).toBeTruthy();
  });
});
*/
