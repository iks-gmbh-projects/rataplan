import { Component, OnInit } from '@angular/core';
import { FormBuilder, FormControl, Validators } from '@angular/forms';

import { RegisterService } from '../services/register-service/register.service';
import { ActivatedRoute, Router } from "@angular/router";
import { LocalstorageService } from "../services/localstorage-service/localstorage.service";
import { FrontendUser } from "../services/login.service/user.model";
import { userdataStorageService } from "../services/userdata-storage-service/userdata-storage.service";
import { FormErrorMessageService } from "../services/form-error-message-service/form-error-message.service";
import { ExtraValidators } from "../validator/validators";


@Component({
  selector: 'app-register',
  templateUrl: './register.component.html',
  styleUrls: ['./register.component.css']
})
export class RegisterComponent implements OnInit {

  username: FormControl = new FormControl('', [Validators.required, Validators.minLength(3), ExtraValidators.cannotContainWhitespace],
    ctrl => this.registerService.usernameExists(ctrl));

  mail: FormControl = new FormControl('', [Validators.required, Validators.email],
    ctrl => this.registerService.mailExists(ctrl));

  password = new FormControl('', [Validators.required, Validators.minLength(3)]);
  confirmPassword = new FormControl('', [Validators.required, ExtraValidators.valueMatching(this.password)]);
  displayname: FormControl = new FormControl('', [Validators.required, ExtraValidators.containsSomeWhitespace]);
  hide = true;
  hideConfirm = true;

  registerForm = this.formBuilder.group({
    username: this.username,
    mail: this.mail,
    password: this.password,
    confirmPassword: this.confirmPassword,
    displayname: this.displayname,
  });

  constructor(
    private formBuilder: FormBuilder,
    private registerService: RegisterService,
    private router: Router,
    private activatedRoute: ActivatedRoute,
    private localStorage: LocalstorageService,
    private userdataStorageService: userdataStorageService,
    public readonly errorMessages: FormErrorMessageService
  ) {
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
      this.userdataStorageService.id = responseData.id;
      this.userdataStorageService.username = responseData.username;
      this.userdataStorageService.mail = responseData.mail;
      this.userdataStorageService.displayName = responseData.displayname;
      this.localStorage.setLocalStorage(responseData);
      this.router.navigateByUrl(this.activatedRoute.snapshot.queryParams['redirect'] || "/");
    });

    //should route to profile, which doesnt exist yet
    //this.router.navigateByUrl('/');
  }
}
