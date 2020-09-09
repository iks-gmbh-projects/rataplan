import {Component, Input, OnInit, ViewChild} from '@angular/core';
import {Router, ActivatedRoute, Params} from '@angular/router';
import {AppointmentRequest} from '../../../models/appointment-requests/appointment-request';
import {AppointmentRequestService} from '../../../services/appointment-request-service/appointment-request.service';
import {Modal} from 'ngx-modal';
import { isNullOrUndefined } from 'util';
import { ErrorCode } from '../../../models/error-codes/error-codes.component';

@Component({
  selector: 'rp-appointment-request-edit',
  templateUrl: './appointment-request-edit.component.html',
  styleUrls: ['./appointment-request-edit.component.css']
})
export class AppointmentRequestEditComponent implements OnInit {
  public appointmentRequest: AppointmentRequest;
  public modalTitle: string;
  public modalContent: string;

  private requestId;

  public isEdit = true;
  public isLoadingData = true;
  public isAuthorized = true;
  public wrongPassword = false;
  public onlyCreator = false;
  public isLoadingComponent = true;
  public loadingSpinner = {
    'position': 'absolute',
    'top': '50%',
    'left': '50%'
  };

  public password: string;

  private alreadySubmitted = false;

  @ViewChild(Modal) modal: Modal;

  constructor(private router: Router,
              private _route: ActivatedRoute,
              private appointmentRequestService: AppointmentRequestService) {
    _route.params
      .subscribe(
        params => this.requestId = params['id']
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
    this.appointmentRequestService.getAppointmentRequestForEdit(this.requestId, this.password)
      .subscribe((apRequest: AppointmentRequest) => {
          this.appointmentRequest = new AppointmentRequest(null);
          this.appointmentRequest.mapToAppointmentRequestObject(apRequest);

          this.isAuthorized = true;
          this.alreadySubmitted = false;
          this.wrongPassword = false;
          this.isLoadingComponent = false;
        },
        err => {
          if (!isNullOrUndefined(this.password)) {
            this.wrongPassword = true;
          }
          if (err.errorCode === ErrorCode[ErrorCode.ONLY_CREATOR]) {
            this.onlyCreator = true;
          }

          this.isAuthorized = false;
          this.alreadySubmitted = false;
          this.isLoadingData = false;
          this.isLoadingComponent = false;
    }, () => {
          this.isLoadingComponent = false;
        });
  }

  onSubmitPassword(password: string) {
    this.password = password;
    this.getAppointmentRequest();
  }

  saveAppointmentRequest(apRequest: AppointmentRequest) {
    if (this.appointmentRequest.equals(apRequest)) {
      this.openModal('Keine Änderungen gefunden', 'Sie kehren nun zum bearbeiten zurück.', 3000)
    } else {

      if (this.alreadySubmitted) {
        return;
      }
      this.alreadySubmitted = true;
      this.appointmentRequestService.updateAppointmentRequest(apRequest, this.password)
        .subscribe(res => {
            this.openModal('Speicherergebnis', 'Anfrage erfolgreich gespeichert.', 3000)
          },
          err => {
            this.openModal('FEHLER BEIM SPEICHERN', 'Fehler beim Speichern der Anfrage. Bitte Informieren Sie IKS: ' + err, 5000);
          },
          () => {
            this.alreadySubmitted = false;
          }
        );
    }
  }

  cancel(cancelled: boolean) {
    if (cancelled) {
      this.openModal('Bearbeitung abgebrochen', 'Änderungen werden nicht gespeichert.', 3000);
    }
  }

  public openModal(title, message, time) {
    this.modalTitle = title;
    this.modalContent = message;
    this.modal.open();
    setTimeout(() => {
      this.modal.close();
    }, time);
  }

  public onCloseModal() {
    this.router.navigate(['/appointmentrequest/' + this.appointmentRequest.id]);
  }


}
