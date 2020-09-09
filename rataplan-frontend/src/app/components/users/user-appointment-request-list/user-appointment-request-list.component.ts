import { Component, OnInit, ViewChild } from '@angular/core';
import { Router } from '@angular/router';
import { Modal } from 'ngx-modal';
import { AppointmentRequest } from '../../../models/appointment-requests/appointment-request';
import { AppointmentRequestService } from '../../../services/appointment-request-service/appointment-request.service';
import { UserService } from '../../../services/user-service/user.service';

@Component({
  selector: 'rp-user-appointment-request-list',
  templateUrl: './user-appointment-request-list.component.html',
  styleUrls: ['./user-appointment-request-list.component.css']
})
export class UserAppointmentRequestListComponent implements OnInit {
  participateAppointmentRequests: AppointmentRequest[];
  ownAppointmentRequests: AppointmentRequest[];

  modalContent: string;
  modalTitle: string;

  @ViewChild(Modal) modal: Modal;

  constructor(private router: Router, private appointmentRequestService: AppointmentRequestService,
              private userService: UserService) {
  }

  ngOnInit() {
    this.appointmentRequestService.getParticipateAppointmentRequests()
      .subscribe(res => {
          this.participateAppointmentRequests = [];
          for (let request of res) {
            this.participateAppointmentRequests.push(Object.assign(new AppointmentRequest(null), request));
          }
        },
        err => {
          this.openModal('Logout', err, 5000);
        });

    this.appointmentRequestService.getAppointmentRequestsCreatedbyUser()
      .subscribe(res => {
          this.ownAppointmentRequests = [];
          for (let request of res) {
            this.ownAppointmentRequests.push(Object.assign(new AppointmentRequest(null), request));
          }
        },
        err => {
        });
  }

  public openModal(title, message, time) {
    if (this.userService.logout()) {
      this.modalTitle = title;
      this.modalContent = message;
      this.modal.open();
      setTimeout(() => {
        this.modal.close();
      }, time);
    }
  }

  public onCloseModal() {
    this.userService.logout();
    this.router.navigateByUrl('/login');
  }

}
