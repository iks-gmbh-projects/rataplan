import { Component, OnInit } from '@angular/core';
import { FormBuilder, FormControl, Validators } from '@angular/forms';
import { switchMap, timer } from 'rxjs';
import { map } from 'rxjs/operators';

import { ForgotPasswordService } from '../../services/forgot-password-service/forgot-password.service';
import { RegisterService } from '../../services/register-service/register.service';

@Component({
  selector: 'app-forgot-password',
  templateUrl: './forgot-password.component.html',
  styleUrls: ['./forgot-password.component.css']
})
export class ForgotPasswordComponent implements OnInit {

  mail: FormControl = new FormControl('', [Validators.required, Validators.email],
    mailExists => {
      return timer(1000).pipe(switchMap(() => {
        return this.registerService.checkIfMailExists(this.mail.value)
          .pipe(map(resp => {
            if (resp) {
              return (null);
            } else {
              return ({ mailDoesNotExist: true });
            }
          }));
      }));
    });

  forgotPasswordForm = this.formBuilder.group({
    mail: this.mail,
  });

  constructor(private formBuilder: FormBuilder,
              private registerService: RegisterService,
              private forgotPasswordService: ForgotPasswordService) {

  }

  ngOnInit(): void {
  }

  submit() {

    this.forgotPasswordService.forgotPassword(this.mail.value).subscribe((res: any) => {

    }, (error) => {

    });
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
