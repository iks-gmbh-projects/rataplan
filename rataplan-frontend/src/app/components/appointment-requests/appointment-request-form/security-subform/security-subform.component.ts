import { Component, EventEmitter, Input, OnInit, Output, ViewChild } from '@angular/core';
import { FormGroup } from '@angular/forms';

@Component({
  selector: 'rp-security-subform',
  templateUrl: './security-subform.component.html',
  styleUrls: ['./security-subform.component.css']
})
export class SecuritySubformComponent implements OnInit {

  @Input() securityForm: FormGroup;
  @Input() isLoggedIn: boolean;
  @Input() create: boolean;

  public changePassword: boolean;
  public adminPasswordText = 'Wählen Sie ein Passwort aus, mit dem die Terminanfrage bearbeitet werden kann.';
  public passwordText = 'Wählen Sie ein Passwort aus, mit dem die Benutzer zur Terminanfrage gelangen.';

  @ViewChild('adminPassword') adminPassword;
  @ViewChild('password') password;

  constructor() {
  }

  ngOnInit() {
    if (!this.create) {
      this.changePassword = false;
      if (this.isLoggedIn) {
        this.adminPasswordText = 'Geben Sie hier ihr neues Passwort ein, mit dem auch andere die Terminanfrage bearbeiten können.';
      } else {
        this.adminPasswordText = 'Geben Sie hier ihr neues Passwort ein, um die Terminanfrage zu bearbeiten';
      }
      this.passwordText = 'Geben Sie hier Ihr neues Passwort ein, mit dem die Teilnehmer zur Terminanfrage gelangen.';
    } else {
      this.changePassword = true;
      if (this.isLoggedIn) {
        this.adminPasswordText = 'Wählen Sie ein Passwort aus, mit dem auch andere die Terminanfrage bearbeiten können.';
      }
    }

  }

  onClickCheckbox() {
    if (!this.changePassword) {
      for (let controlName of Object.keys(this.securityForm.controls)) {
        this.securityForm.controls[controlName].setValue('');
      }
    }
  }

  over(field: string) {
    switch (field) {
      case 'adminPassword':
        this.adminPassword.nativeElement.type = 'text';
        break;
      case 'password':
        this.password.nativeElement.type = 'text';
        break;
    }
  }

  leave(field: string) {
    switch (field) {
      case 'adminPassword':
        this.adminPassword.nativeElement.type = 'password';
        break;
      case 'password':
        this.password.nativeElement.type = 'password';
        break;
    }
  }

  public hasErrors(controlName: string): boolean {
    let control = this.securityForm.controls[controlName];
    return !control.valid && control.touched;
  }
}
