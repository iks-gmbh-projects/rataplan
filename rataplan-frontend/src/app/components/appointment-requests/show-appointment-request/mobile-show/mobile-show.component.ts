import { AfterViewChecked, AfterViewInit, Component, Input, OnInit } from '@angular/core';
import { AppointmentRequestService } from '../../../../services/appointment-request-service/appointment-request.service';
import { ActivatedRoute, Router } from '@angular/router';
import { AppointmentRequest } from '../../../../models/appointment-requests/appointment-request';
import { isNullOrUndefined } from 'util';
import { User } from '../../../../models/users/user';
import { Subject } from 'rxjs/Subject';
import { Observable } from 'rxjs/Observable';
import { ISubscription } from 'rxjs/Subscription';
import { Appointment } from '../../../../models/appointment-requests/appointment';
import { AppointmentMember } from '../../../../models/appointment-requests/appointment-member';

@Component({
  selector: 'rp-mobile-show',
  templateUrl: './mobile-show.component.html',
  styleUrls: ['./mobile-show.component.css']
})
export class MobileShowComponent implements OnInit, AfterViewInit, AfterViewChecked {

  public appointmentRequest: AppointmentRequest = new AppointmentRequest(undefined);

  public appointmentMember: AppointmentMember;

  private user = new User();
  private sub: ISubscription;

  public appointment: Appointment;
  public isShowAppointment = false;
  public appointmentIndex: number;
  public appointmentMembers: AppointmentMember[];

  public showAppointmentMemberForm = false;

  public isEdit = false;

  public isFirstLoad = true;
  public requestId;
  public dateFormat = [];

  public isLoadingData = true;
  public isAuthorized = true;
  public isWrongPassword = false;
  private alreadySubmitted = false;
  public isLoadingComponent = true;
  public loadingSpinner = {
    'position': 'absolute',
    'top': '50%',
    'left': '50%'
  };

  public iconSpinnerStyles = {
    'padding-top': '10px',
    'padding-left': '18px'
  };

  @Input() password: string;

  constructor(private appointmentRequestService: AppointmentRequestService,
              private _router: Router,
              private _route: ActivatedRoute) {

    _route.params
      .subscribe(
        params => {
          this.requestId = params['id'];
        });

  }

  ngOnInit() {
    this.getAppointmentRequest();
  }

  getAppointmentRequest() {
    if (this.alreadySubmitted) {
      return;
    }

    this.isLoadingData = true;
    this.alreadySubmitted = true;
    this.appointmentRequestService.getAppointmentRequest(this.requestId, this.password)
      .subscribe(
        appointmentRequest => {
          this.appointmentRequest = new AppointmentRequest(appointmentRequest);

          if (!isNullOrUndefined(JSON.parse(localStorage.getItem('rp_username')))) {
            this.user.username = JSON.parse(localStorage.getItem('rp_username'));
            this.user.id = JSON.parse(localStorage.getItem('rp_id')); //kommt weg, wird von backend gesetzt. FÜr testzwecke da
          }

          this.appointmentRequest.appointmentMembers.sort((a, b) => 0 - (a.id > b.id ? -1 : 1));

          this.isAuthorized = true;
          this.alreadySubmitted = false;
          this.isWrongPassword = false;
          this.isLoadingData = false;
          this.isLoadingComponent = false;
        },
        err => {
          if (!isNullOrUndefined(this.password)) {
            this.isWrongPassword = true;
          }

          this.isAuthorized = false;
          this.alreadySubmitted = false;
          this.isLoadingData = false;
          this.isLoadingComponent = false;
        });
  }

  ngAfterViewInit() {
    let resizeSubject: Subject<any> = new Subject();
    this.sub = Observable.fromEvent(window, 'resize')
      .subscribe(resizeSubject);

    resizeSubject.subscribe(res => {
      this.checkWidth();
    });
  }

  ngAfterViewChecked() {
    if (this.isFirstLoad) {
      this.checkWidth();
      this.isFirstLoad = false;
      return;
    }
  }

  checkWidth() {
    if (window.innerWidth > 768) {
      this._router.navigateByUrl('/appointmentrequest/' + this.requestId)
    }
  }

  onSubmitPassword(password: string) {
    this.password = password;
    this.getAppointmentRequest();
  }

  ngOnDestroy() {
    this.sub.unsubscribe();
  }

  show(index: number) {
    this.isShowAppointment = true;
    this.appointmentIndex = index;
  }

  back() {
    this.appointmentIndex = undefined;
    this.isShowAppointment = false;
  }

  onEdit($event) {
    this.appointmentMember = $event;
    this.isEdit = true;
    this.showAppointmentMemberForm = true;
  }

  onCancelAddAppointmentMember() {
    this.showAppointmentMemberForm = false;
  }

  addAppointmentMember() {
    this.isEdit = false;
    this.appointmentMember = new AppointmentMember();
    this.showAppointmentMemberForm = true;
  }

}
