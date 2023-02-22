import { Component, OnInit } from '@angular/core';
import { FormBuilder, FormControl, Validators } from '@angular/forms';

import { ForgotPasswordService } from '../services/forgot-password-service/forgot-password.service';
import { RegisterService } from '../services/register-service/register.service';
import { FormErrorMessageService } from "../services/form-error-message-service/form-error-message.service";
import { MatSnackBar } from "@angular/material/snack-bar";

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

  constructor(
    private formBuilder: FormBuilder,
    private registerService: RegisterService,
    private forgotPasswordService: ForgotPasswordService,
    public readonly errorMessageService: FormErrorMessageService,
    private snackBar: MatSnackBar
  ) {

  }

  ngOnInit(): void {
  }

  submit() {
    this.forgotPasswordService.forgotPassword(this.mail.value).subscribe({
      next: () => this.snackBar.open('Eine Email zum Zurücksetzen des Passworts wurde versandt.', 'Ok', {
        duration: 3000
      })
    });
  }

}
