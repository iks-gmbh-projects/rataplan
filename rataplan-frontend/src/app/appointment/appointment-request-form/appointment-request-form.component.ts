import { Component, OnDestroy, OnInit } from '@angular/core';
import { ActivatedRoute, Router } from '@angular/router';
import { Subject, takeUntil } from 'rxjs';

import { AppointmentRequestFormService } from '../appointment-request-form.service';
import { AppointmentRequestNavigationEnum } from '../appointment-request-navigation.enum';
import { AppointmentRequestModel } from '../model/appointmentRequestModel';

@Component({
  selector: 'app-appointment-request-form',
  templateUrl: './appointment-request-form.component.html',
  styleUrls: ['./appointment-request-form.component.css'],
})
export class AppointmentRequestFormComponent implements OnInit, OnDestroy {
  destroySubject: Subject<boolean> = new Subject<boolean>();
  appointmentSections: string[] = [
    AppointmentRequestNavigationEnum.General,
    AppointmentRequestNavigationEnum.Datepicker,
    AppointmentRequestNavigationEnum.Overview,
    AppointmentRequestNavigationEnum.Email,
    AppointmentRequestNavigationEnum.Links,
  ];

  isPageValid = false;
  indexOfSection = 0;

  constructor(private router: Router,
    private route: ActivatedRoute,
    private appointmentRequestFormService: AppointmentRequestFormService) {
  }

  ngOnInit(): void {
    this.appointmentRequestFormService.validationObservable
      .pipe(takeUntil(this.destroySubject))
      .subscribe(valid => {
        this.isPageValid = valid;
      });
  }

  ngOnDestroy(): void {
    this.destroySubject.next(true);
    this.destroySubject.complete();
    this.appointmentRequestFormService.appointmentRequest = new AppointmentRequestModel();
  }

  navigateToPage() {
    this.router.navigate([this.appointmentSections[this.indexOfSection]],
      { relativeTo: this.route });
    if (this.appointmentSections[this.indexOfSection] === AppointmentRequestNavigationEnum.Links) {
      this.sendEndOfAppointment();
    }
  }

  nextPage() {
    this.appointmentRequestFormService.submitValues();
    this.indexOfSection++;
    this.navigateToPage();
  }

  backPage() {
    this.appointmentRequestFormService.submitValues();
    this.indexOfSection--;
    this.navigateToPage();
  }

  sendEndOfAppointment() {
    console.log('Sending HTTP Request... just kidding not working yet');
    console.log(this.appointmentRequestFormService.appointmentRequest);
  }
}
