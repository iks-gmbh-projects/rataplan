import { Component, OnInit } from '@angular/core';
import { FormBuilder, FormControl, Validators } from '@angular/forms';

import { ForgotPasswordService } from '../services/forgot-password-service/forgot-password.service';
import { RegisterService } from '../services/register-service/register.service';
import {MatSnackBar} from "@angular/material/snack-bar";

@Component({
  selector: 'app-forgot-password',
  templateUrl: './forgot-password.component.html',
  styleUrls: ['./forgot-password.component.css']
})
export class ForgotPasswordComponent implements OnInit {

  mail: FormControl = new FormControl('', [Validators.required, Validators.email]);

  forgotPasswordForm = this.formBuilder.group({
    mail: this.mail,
  });

  constructor(private formBuilder: FormBuilder,
              private registerService: RegisterService,
              private forgotPasswordService: ForgotPasswordService,
              private snackBar: MatSnackBar) {}

  ngOnInit(): void {
  }

  submit() {
    this.forgotPasswordService.forgotPassword(this.mail.value).subscribe((res: any) => {
      this.snackBar.open('Eine Email zum Zurücksetzen des Passworts wurde versandt.', '',{
        duration: 3000
      });
    }, (error) => {});
  }

  getMailErrorMessage() {
    if (this.mail.hasError('required')) {
      return 'Dieses Feld darf nicht leer bleiben';
    }

    if (this.mail.hasError('mailDoesNotExist')) {
      return 'Email wird nicht verwendet';
    }

    return this.mail.hasError('email') ? 'Keine gültige email' : '';
  }

}
