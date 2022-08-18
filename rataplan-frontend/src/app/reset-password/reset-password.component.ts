import {Component, OnInit} from '@angular/core';
import {FormBuilder, FormControl, Validators} from "@angular/forms";
import {ResetPasswordService} from "../services/reset-password-service/reset-password.service";
import {ActivatedRoute} from "@angular/router";
import {ResetPasswordData} from "../models/resetPasswordData";

@Component({
  selector: 'app-reset-password',
  templateUrl: './reset-password.component.html',
  styleUrls: ['./reset-password.component.css']
})
export class ResetPasswordComponent implements OnInit {

  password = new FormControl('', [Validators.required, Validators.minLength(3)]);
  confirmPassword = new FormControl('', Validators.required);
  hide = true;
  hideConfirm = true;

  resetPasswordForm = this.formBuilder.group({
    password: this.password,
    confirmPassword: this.confirmPassword
  })

  constructor(private formBuilder: FormBuilder,
              private resetPasswortService: ResetPasswordService,
              private route: ActivatedRoute) {
  }

  ngOnInit(): void {

  }

  resetPassword() {
    let token: string;
    this.route.queryParams.subscribe(params => {
      token = params['token'];
      let resetPassword = new ResetPasswordData(token, this.password.value);
      this.resetPasswortService.resetPassword(resetPassword).subscribe(response => {

      });
    })
  }

  getPasswordErrorMessage() {
    if (this.password.hasError('required')) {
      return 'Dieses Feld darf nicht leer bleiben';
    }
    if (this.password.hasError('minLength')) {
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
