import { Component, OnInit } from '@angular/core';
import { UntypedFormBuilder, UntypedFormControl, Validators } from '@angular/forms';

import { ForgotPasswordService } from '../services/forgot-password-service/forgot-password.service';
import { UsernameEmailValidatorsService } from '../services/username-email-validators-service/username-email-validators.service';
import { FormErrorMessageService } from "../services/form-error-message-service/form-error-message.service";
import { MatSnackBar } from "@angular/material/snack-bar";

@Component({
  selector: 'app-forgot-password',
  templateUrl: './forgot-password.component.html',
  styleUrls: ['./forgot-password.component.css']
})
export class ForgotPasswordComponent implements OnInit {

  mail: UntypedFormControl = new UntypedFormControl('', [Validators.required, Validators.email]);

  forgotPasswordForm = this.formBuilder.group({
    mail: this.mail,
  });

  constructor(
    private formBuilder: UntypedFormBuilder,
    private registerService: UsernameEmailValidatorsService,
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
