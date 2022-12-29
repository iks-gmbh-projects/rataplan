import { Component, OnInit } from '@angular/core';
import {FormBuilder, FormControl, Validators} from "@angular/forms";

import {LoginService} from "../services/login.service/login.service";
import {HttpErrorResponse} from "@angular/common/http";
import {Subject} from "rxjs";
import {User} from "../services/login.service/user.model";
import {Router} from "@angular/router";
import {LocalstorageService} from "../services/localstorage-service/localstorage.service";
import {userdataStorageService} from "../services/userdata-storage-service/userdata-storage.service";




@Component({
  selector: 'app-login',
  templateUrl: './login.component.html',
  styleUrls: ['./login.component.css']
})
export class LoginComponent implements OnInit {


  submitted: boolean = false;
  hide = true;
  isLoading = false;
  isLoggedIn = false;
  user = new Subject<User>()
  inputField = new FormControl('', [Validators.required, Validators.minLength(3), this.loginService.cannotContainWhitespace,]);
  password = new FormControl('', [Validators.required, this.loginService.cannotContainWhitespace]);

  loginForm = this.formBuilder.group({
      inputField: this.inputField,
      password: this.password
    }
  );

  loggedIn() {
    return this.isLoggedIn;
  }


  login() {
    if (this.inputField.valid && this.password.valid) {


      let frontendUser: FrontendUser = {
        username: this.inputField.value,
        password: this.password.value

      }
      if (this.inputField.value.indexOf('@') !== -1) {
        frontendUser = {
          mail: this.inputField.value,
          password: this.password.value
        }
      }
      this.isLoading = true;
      this.loginService.loginUser(frontendUser).subscribe(responseData => {
        console.log(responseData);
        this.userdataStorageService.id = responseData.id;
        this.userdataStorageService.username = responseData.username;
        this.userdataStorageService.mail = responseData.mail;
        this.userdataStorageService.displayName = responseData.displayName;
        this.localStorage.setLocalStorage(responseData);
        this.router.navigateByUrl('/')
        this.isLoggedIn = true;
        this.isLoading = false;
      }, error => {
        this.handleError(error);
        console.log(error);
        this.isLoading = false;
      })
    }
  }

  private handleError(errorRes: HttpErrorResponse) {
    if (errorRes.error.errorCode === "WRONG_CREDENTIALS") {

    }
  }

  // private handleError(errorRes: HttpErrorResponse) {
  //   let errorMessage = 'An unknown error occurred!';
  //   if (!errorRes.error || !errorRes.error.error) {
  //     return throwError(errorMessage);
  //   }
  //   switch (errorRes.error.error.message) {
  //     case
  //   }
  // }






  inputFieldError() {
    if (this.inputField.hasError('required')) {
      return 'Dieses Feld darf nicht leer bleiben!';
    }

    if (this.inputField.hasError('usernameNotExists')) {
      return 'Benutzername existiert nicht';
    }

    if (this.inputField.hasError('cannotContainWhitespace')) {
      return 'Zeichen nicht erlaubt'
    }

    return this.inputField.hasError('minLength') ? '' : 'Benutzername muss mindestens 3 Zeichen lang sein!'
  }

  getPasswordErrorMessage() {
    if (this.password.hasError('required')) {
      return 'Dieses Feld darf nicht leer bleiben!';
    }
    if (this.password.hasError('cannotContainWhitespace')) {
      return 'Zeichen nicht erlaubt'
    }
    return this.password.hasError('invalidPassword') ? 'Das eingegebene Passwort ist inkorrekt!' : '';

  }

  constructor(private formBuilder: FormBuilder,
              private loginService: LoginService,
              private router: Router,
              private localStorage: LocalstorageService,
              private userdataStorageService: userdataStorageService
               ) {
  }

  ngOnInit(): void {
  }
}
  export interface FrontendUser {


    username?: string;
    id?: number;
     mail?: string;
     password: string;
     displayname?: string;

}


