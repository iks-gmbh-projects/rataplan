import { Component, OnInit } from '@angular/core';
import { FormBuilder, FormControl, Validators } from '@angular/forms';
import { switchMap, timer } from 'rxjs';
import { map } from 'rxjs/operators';

import { RegisterService } from '../services/register-service/register.service';


@Component({
  selector: 'app-register',
  templateUrl: './register.component.html',
  styleUrls: ['./register.component.css']
})
export class RegisterComponent implements OnInit {

  username: FormControl = new FormControl('', [Validators.required, Validators.minLength(3)],
    usernameExists => {
      return timer(1000).pipe(switchMap(() => {
        return this.registerService.checkIfUsernameExists(this.username.value)
          .pipe(map(resp => {
            if (resp) {
              return ({ usernameExists: true });
            } else {
              return (null);
            }
          }));
      }));
    });

  mail: FormControl = new FormControl('', [Validators.required, Validators.email],
    mailExists => {
      return timer(1000).pipe(switchMap(() => {
        return this.registerService.checkIfMailExists(this.mail.value)
          .pipe(map(resp => {
            if (resp) {
              return ({ mailExists: true });
            } else {
              return (null);
            }
          }));
      }));
    });

  password = new FormControl('', [Validators.required, Validators.minLength(3)]);
  confirmPassword = new FormControl('', Validators.required);
  displayname: FormControl = new FormControl('', [Validators.required]);
  hide = true;
  hideConfirm = true;

  registerForm = this.formBuilder.group({
    username: this.username,
    mail: this.mail,
    password: this.password,
    confirmPassword: this.confirmPassword,
    displayname: this.displayname,
  });

  constructor(private formBuilder: FormBuilder,
              private registerService: RegisterService) {
  }

  ngOnInit(): void {
  }

  submit() {
    this.username.updateValueAndValidity();
    const frontendUser: FrontendUser = {
      username: this.username.value,
      mail: this.mail.value,
      password: this.password.value,
      displayname: this.displayname.value,
    };

    this.registerService.registerUser(frontendUser).subscribe(responseData => {
      console.log(responseData);
    });

    //should route to profile, which doesnt exist yet
    //this.router.navigateByUrl('/');
  }

  getUsernameErrorMessage() {
    if (this.username.hasError('required')) {
      return 'Dieses Feld darf nicht leer bleiben';
    }

    if (this.username.hasError('usernameExists')) {
      return 'Benutzername wird bereits verwendet';
    }

    return this.username.hasError('minLength') ? '' : 'Mindestens 3 Zeichen';
  }

  getMailErrorMessage() {
    if (this.mail.hasError('required')) {
      return 'Dieses Feld darf nicht leer bleiben';
    }

    if (this.mail.hasError('mailExists')) {
      return 'Email wird bereits verwendet';
    }

    return this.mail.hasError('email') ? 'Keine gültige email' : '';
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

  getDisplaynameErrorMessage() {
    if (this.username.hasError('required')) {
      return 'Dieses Feld darf nicht leer bleiben';
    }
    return '';
  }
}

export interface FrontendUser {
  username: string;
  mail: string;
  password: string;
  displayname: string;
}
