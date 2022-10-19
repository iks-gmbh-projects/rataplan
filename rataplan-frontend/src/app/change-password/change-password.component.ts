import { Component, OnInit } from '@angular/core';
import {FormBuilder, FormControl, Validators} from "@angular/forms";
import {PasswordChangeModel} from "../models/password-change.model";
import {ChangePasswordService} from "../services/change-password/change-password.service";
import {MatSnackBar} from "@angular/material/snack-bar";

@Component({
  selector: 'app-change-password',
  templateUrl: './change-password.component.html',
  styleUrls: ['./change-password.component.css']
})
export class ChangePasswordComponent implements OnInit {

  hideOldPassword = true;
  hideNewPassword = true;
  hideConfirmPassword = true;

  oldPassword: FormControl = new FormControl('', Validators.required);
  newPassword: FormControl = new FormControl('', [Validators.required, Validators.minLength(3)]);
  confirmPassword: FormControl = new FormControl('', Validators.required);

  changePasswordForm = this.formBuilder.group({
    oldPassword: this.oldPassword.value,
    newPassword: this.newPassword.value,
    confirmPassword: this.confirmPassword.value,
  });


  constructor(private formBuilder: FormBuilder,
              private changePasswordService: ChangePasswordService,
              private snackBar: MatSnackBar) { }

  ngOnInit(): void {
  }

  submit() {
    const passwordChange = new PasswordChangeModel(this.oldPassword.value, this.newPassword.value);
    this.changePasswordService.changePassword(passwordChange).subscribe(responseData => {
        this.snackBar.open('Passwort erfolgreich geändert', '',{
          duration: 3000
        });
    },
      (error) => {
        this.oldPassword.setErrors({'wrongPassword': true});
    });
  }

  getOldPasswordErrorMessage() {
    if (this.oldPassword.hasError('required')) {
      return 'Dieses Feld darf nicht leer bleiben';
    } else if (this.oldPassword.hasError('wrongPassword')) {
      return 'Falsches Passwort';
    }
    return '';
  }

  getNewPasswordErrorMessage() {
    if (this.newPassword.hasError('required')) {
      return 'Dieses Feld darf nicht leer bleiben';
    }
    if (this.newPassword.hasError('minLength')) {
      return 'Mindestens 3 Zeichen';
    }
    return '';
  }

  getConfirmPasswordErrorMessage() {
    if (this.confirmPassword.hasError('required')) {
      return 'Dieses Feld darf nicht leer bleiben';
    }
    return this.confirmPassword.hasError('pattern') ? 'Passwort stimmt nicht überein' : '';
  }

}
