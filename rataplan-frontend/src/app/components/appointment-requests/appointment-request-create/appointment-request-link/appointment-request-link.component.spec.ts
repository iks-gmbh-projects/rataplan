/*
/!* tslint:disable:no-unused-variable *!/
import {ComponentFixture, TestBed} from '@angular/core/testing';


import { AppointmentRequestLinkComponent } from './appointment-request-link.component';

describe('AppointmentRequestLinkComponent', () => {
  let component: AppointmentRequestLinkComponent;
  let fixture: ComponentFixture<AppointmentRequestLinkComponent>;

  beforeEach(() => {
    TestBed.configureTestingModule({
      declarations: [ AppointmentRequestLinkComponent ]
    })
    .compileComponents();
  }));

  beforeEach(() => {
    fixture = TestBed.createComponent(AppointmentRequestLinkComponent);
    component = fixture.componentInstance;
    nativeElement = fixture.nativeElement;
  });

  it('should create', () => {
    component.appointmentRequest = new AppointmentRequest(null);
    fixture.detectChanges();

    expect(component).toBeTruthy();
  });

  it('should be able to change appointmentConfig', () => {
    component.appointmentRequest = new AppointmentRequest(null);
    fixture.detectChanges();

    expect(component['appointmentRequest'].config.appointmentConfig.location).toBe(true);
    expect(component['appointmentRequest'].config.appointmentConfig.date).toBe(false);
    expect(component['appointmentRequest'].config.appointmentConfig.startTime).toBe(false);
    expect(component['appointmentRequest'].config.appointmentConfig.endTime).toBe(false);
    expect(component['appointmentRequest'].config.appointmentConfig.freeInput).toBe(false);

    nativeElement.querySelector('#checkbox-date').click();
    nativeElement.querySelector('#checkbox-starttime').click();
    nativeElement.querySelector('#checkbox-endtime').click();
    nativeElement.querySelector('#checkbox-freeinput').click();

    expect(component['appointmentRequest'].config.appointmentConfig.location).toBe(true);
    expect(component['appointmentRequest'].config.appointmentConfig.date).toBe(true);
    expect(component['appointmentRequest'].config.appointmentConfig.startTime).toBe(true);
    expect(component['appointmentRequest'].config.appointmentConfig.endTime).toBe(true);
    expect(component['appointmentRequest'].config.appointmentConfig.freeInput).toBe(true);
  });

  it('should not be possible to disable location', () => {
    component.appointmentRequest = new AppointmentRequest(null);
    fixture.detectChanges();

    expect(component['appointmentRequest'].config.appointmentConfig.location).toBe(true);
    nativeElement.querySelector('#checkbox-description').click();
    expect(component['appointmentRequest'].config.appointmentConfig.location).toBe(true);
  });

  it('should be able to change activeTab', () => {
    component.appointmentRequest = new AppointmentRequest(null);
    fixture.detectChanges();

    expect(component['activeTab']).toBe(0);
    expect(nativeElement.querySelector('#radio-yes-no')).toBeNull();
    nativeElement.querySelector('#decision-tab').click();
    fixture.detectChanges();

    expect(component['activeTab']).toBe(1);
    expect(nativeElement.querySelector('#radio-yes-no')).not.toBeNull();

    nativeElement.querySelector('#appointment-tab').click();
    fixture.detectChanges();

    expect(component['activeTab']).toBe(0);
  });

  it('should have DecisionType.DEFAULT by default', () => {
    component.appointmentRequest = new AppointmentRequest(null);
    fixture.detectChanges();

    expect(component['appointmentRequest'].config.decisionType).toBe(DecisionType[0]);
  });

  it('should be possible to change the DeicisonType', () => {
    component.appointmentRequest = new AppointmentRequest(null);
    fixture.detectChanges();

    expect(component['appointmentRequest'].config.decisionType).toBe(DecisionType[0]);
    nativeElement.querySelector('#decision-tab').click();
    fixture.detectChanges();

    nativeElement.querySelector('#radio-wenag').click();
    expect(component['appointmentRequest'].config.decisionType).toBe(DecisionType[1]);

    nativeElement.querySelector('#radio-participants').click();
    expect(component['appointmentRequest'].config.decisionType).toBe(DecisionType[2]);

    nativeElement.querySelector('#radio-yes-no').click();
    expect(component['appointmentRequest'].config.decisionType).toBe(DecisionType[0]);
  });
});

*/
