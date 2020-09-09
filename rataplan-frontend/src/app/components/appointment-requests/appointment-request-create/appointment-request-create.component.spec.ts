/*
/!* tslint:disable:no-unused-variable *!/
import {NO_ERRORS_SCHEMA} from '@angular/core';
import {FormsModule, ReactiveFormsModule, FormBuilder, Validators} from '@angular/forms';
import { async, ComponentFixture, TestBed, fakeAsync, tick } from '@angular/core/testing';
import { By } from '@angular/platform-browser';
import { DebugElement } from '@angular/core';


import { AppointmentRequestCreateComponent } from './appointment-request-create.component';

describe('AppointmentRequestCreateComponent', () => {
  let component: AppointmentRequestCreateComponent;
  let fixture: ComponentFixture<AppointmentRequestCreateComponent>;

  beforeEach(() => {
    TestBed.configureTestingModule({

      declarations: [ AppointmentRequestCreateComponent ]
    })
  });

  beforeEach(() => {
    fixture = TestBed.createComponent(AppointmentRequestCreateComponent);
    component = fixture.componentInstance;
    nativeElement = fixture.nativeElement;

    fb = new FormBuilder();
    component.appointmentRequestForm = fb.group({
      title: ['', Validators.required],
      description: [''],
      organizerMail: ['', CustomValidators.email],
      deadline: ['', Validators.required]
    });

    titleInput = nativeElement.querySelector('#form-title');
    descriptionInput = nativeElement.querySelector('#form-description');
    organizerMailInput = nativeElement.querySelector('#form-email');
    deadlineInput = nativeElement.querySelector('#form-deadline');

    component.appointmentRequest = new AppointmentRequest(null);
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });

  it('should fill form-data', () => {
    titleInput.value = 'test-title';
    titleInput.dispatchEvent(new Event('input'));

    descriptionInput.value = 'test-description';
    descriptionInput.dispatchEvent(new Event('input'));

    organizerMailInput.value = 'test@email.mail';
    organizerMailInput.dispatchEvent(new Event('input'));

    deadlineInput.value = new Date('01-01-2018');
    deadlineInput.dispatchEvent(new Event('input'));

    component.setDeadLine();

    expect(component['appointmentRequest'].title).toEqual('test-title');
    expect(component['appointmentRequest'].description).toEqual('test-description');
    expect(component['appointmentRequest'].organizerMail).toEqual('test@email.mail');
    expect(component['appointmentRequest'].deadline).toEqual('2018-01-01');
  });

  it('should fill only neccessary form-data', () => {
    titleInput.value = 'test-title';
    titleInput.dispatchEvent(new Event('input'));

    deadlineInput.value = new Date('01-01-2018');
    deadlineInput.dispatchEvent(new Event('input'));

    component.setDeadLine();

    expect(component['appointmentRequest'].title).toEqual('test-title');
    expect(component['appointmentRequest'].description).toBeUndefined();
    expect(component['appointmentRequest'].organizerMail).toEqual('');
    expect(component['appointmentRequest'].deadline).toEqual('2018-01-01');
  });

  it('should fill and clear title', () => {
    titleInput.value = 'test-title';
    titleInput.dispatchEvent(new Event('input'));
    expect(component['appointmentRequest'].title).toEqual('test-title');

    titleInput.value = '';
    titleInput.dispatchEvent(new Event('input'));

    expect(component['appointmentRequest'].title).toEqual('');
    expect(component['appointmentRequest'].description).toBeUndefined();
    expect(component['appointmentRequest'].organizerMail).toEqual('');
  });

});
*/
