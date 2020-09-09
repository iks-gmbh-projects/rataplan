import { Component, OnInit, ViewChild } from '@angular/core';
import { Router } from '@angular/router';
import { Modal } from 'ngx-modal';
import { UserService } from '../../../services/user-service/user.service';
import { User } from './../../../models/users/user';

@Component({
  selector: 'rp-user-profile',
  templateUrl: './user-profile.component.html',
  styleUrls: ['./user-profile.component.css']
})
export class UserProfileComponent implements OnInit {

  public user: User = new User();

  modalContent: string;
  modalTitle: string;

  @ViewChild(Modal) modal: Modal;

  constructor(private router: Router, private userService: UserService) {
  }

  ngOnInit() {
    this.userService.getUserData()
      .subscribe(res => {
          this.user = Object.assign(new User(), res);
        },
        err => {
          this.openModal('Logout', err, 4000);
        });
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
    this.userService.logout()
      .subscribe(res => {
        this.router.navigateByUrl('/login');
      });
  }

}
