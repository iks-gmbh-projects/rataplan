import {Component, OnDestroy, OnInit} from '@angular/core';
import {ActivatedRoute, Router} from '@angular/router';
import {Subject, takeUntil} from 'rxjs';

import {AppointmentRequestModel} from '../../models/appointment-request.model';
import {AppointmentRequestNavigationEnum} from '../appointment-request-navigation.enum';
import {AppointmentRequestFormService} from './appointment-request-form.service';

@Component({
  selector: 'app-appointment-request-form',
  templateUrl: './appointment-request-form.component.html',
  styleUrls: ['./appointment-request-form.component.css'],
})
export class AppointmentRequestFormComponent implements OnInit, OnDestroy {
  destroySubject: Subject<boolean> = new Subject<boolean>();
  appointmentSections: string[] = [
    AppointmentRequestNavigationEnum.General,
    AppointmentRequestNavigationEnum.Config,
    AppointmentRequestNavigationEnum.OverviewSub,
    AppointmentRequestNavigationEnum.Email,
  ];

  isSend = false;
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
    this.appointmentRequestFormService.resetFormObservable
      .pipe(takeUntil(this.destroySubject))
      .subscribe(() => {
        this.indexOfSection = 0;
        this.isPageValid = false;
        this.isSend = false;
      });
  }

  ngOnDestroy(): void {
    this.destroySubject.next(true);
    this.destroySubject.complete();
    this.appointmentRequestFormService.appointmentRequest = new AppointmentRequestModel();
  }

  nextPage() {
    this.appointmentRequestFormService.submitValues();
    this.router.navigate([this.appointmentSections[++this.indexOfSection]],
      { relativeTo: this.route });
  }

  backPage() {
    this.appointmentRequestFormService.submitValues();
    this.router.navigate([this.appointmentSections[--this.indexOfSection]],
      { relativeTo: this.route });
  }

  sendEndOfAppointment() {
    this.router.navigate(['links'], { relativeTo: this.route });
    this.isSend = true;
    // this.appointmentRequestFormService.createAppointmentRequest();
    console.log(this.appointmentRequestFormService.appointmentRequest);
  }
}
