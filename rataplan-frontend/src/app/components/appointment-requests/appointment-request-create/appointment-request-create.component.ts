import { Component, OnInit, ViewChild } from '@angular/core';
import { Modal } from 'ngx-modal';
import { Router } from '@angular/router';
import { AppointmentRequestService } from '../../../services/appointment-request-service/appointment-request.service';
import { AppointmentRequest } from '../../../models/appointment-requests/appointment-request';
import { LinkDataStorageService } from '../../../services/appointment-request-service/link-data-storage.service';
import { UserService } from '../../../services/user-service/user.service';
import { isNullOrUndefined } from 'util';

@Component({
  selector: 'rp-appointment-request-create',
  templateUrl: './appointment-request-create.component.html',
  styleUrls: ['./appointment-request-create.component.css']
})
export class AppointmentRequestCreateComponent {
  public modalContent: string;
  public modalTitle: string;
  public isCreated = false;
  public createdId: number;
  public createdMail: string;

  private navigateTo: NavigateTo;
  private alreadySubmitted = false;

  @ViewChild(Modal) modal: Modal;

  constructor(private router: Router,
              private appointmentRequestService: AppointmentRequestService,
              private userService: UserService) {
  }

  public createAppointmentRequest(apRequest: AppointmentRequest) {
    if (this.alreadySubmitted) {
      return;
    }
    this.alreadySubmitted = true;
    this.appointmentRequestService.createAppointmentRequest(apRequest)
      .subscribe(res => {
          this.createdId = res.id;
          this.createdMail = res.organizerMail;
          this.navigateTo = NavigateTo.APPOINTMENTREQUEST_LINK;
          this.openModal('Speicherergebnis', 'Anfrage erfolgreich angelegt', 3000);
        },
        err => {
          if (this.userService.hasUser()) {
            this.navigateTo = NavigateTo.LOGIN;
            this.openModal('Automatischer Logout', 'Sie werden aus Sicherheitsgründen automatisch ausgeloggt #createrequest', 5000);
          } else {
            this.navigateTo = NavigateTo.HOME;
            this.openModal('FEHLER BEIM SPEICHERN!', 'Fehler beim Speichern der Anfrage. Bitte Informieren Sie IKS: ' + err, 5000);
          }
        },
        () => {
          this.alreadySubmitted = false;
        });
  }

  public cancel(cancelled: boolean) {
    if (cancelled) {
      if (this.userService.hasUser()) {
        this.navigateTo = NavigateTo.APPOINTMENTREQUESTS;
      } else {
        this.navigateTo = NavigateTo.HOME;
      }
      this.openModal('Bearbeitung abgebrochen', 'Die neue Anfrage wird nicht gespeichert.', 3000);
    }
  }

  public openModal(title, message, time) {
    this.modalTitle = title;
    this.modalContent = message;
    this.modal.open();
    setTimeout(() => {
      if (!isNullOrUndefined(this.modal)) {
        this.modal.close();
      }
    }, time);
  }

  public onCloseModal() {
    switch (this.navigateTo) {
      case NavigateTo.LOGIN:
        this.userService.logout();
        this.router.navigateByUrl('/login');
        break;
      case NavigateTo.APPOINTMENTREQUEST_LINK:
        this.isCreated = true;
        break;
      case NavigateTo.APPOINTMENTREQUESTS:
        this.router.navigateByUrl('/appointmentrequests');
        break;
      case NavigateTo.APPOINTMENTREQUEST:
        this.router.navigateByUrl('/appointmentrequest');
        break;
      case NavigateTo.HOME:
        this.router.navigateByUrl('/');
        break;
    }
  }

}

enum NavigateTo  {
  LOGIN,
  APPOINTMENTREQUEST_LINK,
  APPOINTMENTREQUESTS,
  APPOINTMENTREQUEST,
  HOME
}
