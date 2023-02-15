import { Component, OnDestroy, OnInit } from '@angular/core';
import { FormBuilder, FormControl, Validators } from '@angular/forms';

import { UsernameEmailValidatorsService } from '../services/username-email-validators-service/username-email-validators.service';
import { ActivatedRoute, Router } from "@angular/router";
import { RegisterData } from "../services/login.service/user.model";
import { FormErrorMessageService } from "../services/form-error-message-service/form-error-message.service";
import { ExtraValidators } from "../validator/validators";
import { appState } from "../app.reducers";
import { Store } from "@ngrx/store";
import { RegisterAction } from "../authentication/auth.actions";
import { Subscription } from "rxjs";
import { map } from "rxjs/operators";


@Component({
  selector: 'app-register',
  templateUrl: './register.component.html',
  styleUrls: ['./register.component.css']
})
export class RegisterComponent implements OnInit, OnDestroy {

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

  private busySub?: Subscription;

  constructor(
    private formBuilder: FormBuilder,
    private registerService: UsernameEmailValidatorsService,
    private router: Router,
    private activatedRoute: ActivatedRoute,
    private store: Store<appState>,
    public readonly errorMessages: FormErrorMessageService
  ) {
  }

  ngOnInit(): void {
    this.busySub = this.store.select("auth").pipe(
      map(auth => auth.busy)
    ).subscribe(busy => {
      if (busy) this.registerForm.disable();
      else this.registerForm.enable();
    })
  }

  ngOnDestroy(): void {
    this.busySub?.unsubscribe();
  }

  submit() {
    this.username.updateValueAndValidity();
    const frontendUser: RegisterData = {
      username: this.username.value,
      mail: this.mail.value,
      password: this.password.value,
      displayname: this.displayname.value,
    };

    this.store.dispatch(new RegisterAction(frontendUser, this.activatedRoute.snapshot.queryParams['redirect']));

    //should route to profile, which doesnt exist yet
    //this.router.navigateByUrl('/');
  }
}
