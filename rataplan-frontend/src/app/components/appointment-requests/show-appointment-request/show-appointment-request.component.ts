import { animate, style, transition, trigger } from '@angular/animations';
import { AfterViewChecked, AfterViewInit, Component, Input, OnDestroy, OnInit } from '@angular/core';
import { ActivatedRoute, Router } from '@angular/router';
import { Observable, Subject } from 'rxjs';
import { isNullOrUndefined } from 'util';
import { AppointmentRequest } from '../../../models/appointment-requests/appointment-request';
import { User } from '../../../models/users/user';
import { AppointmentRequestService } from '../../../services/appointment-request-service/appointment-request.service';
import { ISubscription } from 'rxjs/Subscription';

@Component({
  selector: 'rp-show-appointment-request',
  templateUrl: './show-appointment-request.component.html',
  styleUrls: ['./show-appointment-request.component.css'],
  animations: [
    trigger('flyInOut', [
      transition('void => *', [
        style({ transform: 'scaleY(0.1)' }),
        animate('300ms')
      ]),
      transition('* => void', [
        animate('300ms', style({ transform: 'scaleY(0.1)' }))
      ])
    ])
  ]
})
export class ShowAppointmentRequestComponent implements OnInit, AfterViewInit, AfterViewChecked, OnDestroy {
  public appointmentRequest: AppointmentRequest = new AppointmentRequest(undefined);

  private sub: ISubscription;
  private user = new User();

  public delay = 1000;
  public isFirstLoad = true;
  public requestId;
  public dateFormat = [];
  public editMemberId = -1;

  public isEdit = false;
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
          if (window.innerWidth < 768) {
            this._router.navigateByUrl('/m/appointmentrequest/' + this.requestId)
          }
        }
  );

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
      this.setTableSizes();
    });
  }

  checkWidth() {
    if (window.innerWidth < 768) {
      this._router.navigate(['/m/appointmentrequest/' + this.requestId]);
    }
  }

  ngAfterViewChecked() {
    if (this.isFirstLoad && document.getElementsByClassName('long').length !== 0) {
      this.setTableSizes();
      this.isFirstLoad = false;
      return;
    }
  }

  private setTableSizes() {
    this.checkWidth();
    let tableContainer = document.getElementById('table-container');

    // is null sometimes on first load
    if (isNullOrUndefined(tableContainer)) {
      return;
    }
    let containerWidth = tableContainer.clientWidth;
    let itemsToResize: HTMLCollection = document.getElementsByClassName('long');
    let newWidth = 213;

    if (containerWidth > 213 * this.appointmentRequest.appointments.length) {
      newWidth = containerWidth / this.appointmentRequest.appointments.length;
      let tableContainer: HTMLCollection = document.getElementsByClassName('table-container');
      tableContainer.item(0).setAttribute('style', 'overflow-x:visible');
    } else {
      let tableContainer: HTMLCollection = document.getElementsByClassName('table-container');
      tableContainer.item(0).setAttribute('style', 'overflow-x:scroll');
    }

    for (let i = 0; i < itemsToResize.length; i++) {
      itemsToResize.item(i).setAttribute('style', 'min-width: ' + newWidth + 'px !important');
    }

    return;
  }

  onSubmitPassword(password: string) {
    this.password = password;
    this.getAppointmentRequest();
  }

  public setEditMemberId(id) {
    this.editMemberId = id;
  }

  ngOnDestroy() {
    this.sub.unsubscribe();
  }

}
