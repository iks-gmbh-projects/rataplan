import {Component, DoCheck, ViewChild} from '@angular/core';
import {User} from '../../../models/users/user';
import {UserService} from '../../../services/user-service/user.service';
import {Router} from '@angular/router';
import {Modal} from 'ngx-modal';
import {isNullOrUndefined} from "util";

@Component({
  selector: 'rp-navbar',
  templateUrl: 'navbar.component.html',
  styleUrls: ['navbar.component.css']
})
export class NavbarComponent implements DoCheck {
  public username: string;
  public isLoggedIn = false;

  public isIn = false;

  modalContent: string;
  modalTitle: string;

  @ViewChild(Modal) modal: Modal;

  constructor(private userService: UserService, private router: Router) {
  }

  ngDoCheck() {
    this.username = JSON.parse(localStorage.getItem('rp_username'));
    this.isLoggedIn = !isNullOrUndefined(this.username);
  }

  public toggleState() {
    this.isIn = !this.isIn
  }

  public onClickTab() {
    this.isIn = false;
  }

  public logout() {
    this.isIn = false;
    this.userService.logout()
        .subscribe(res => {
          this.openModal('Logout', 'Sie wurden erfolgreich ausgeloggt', 3000);
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
    this.router.navigateByUrl('/login');
  }

}
