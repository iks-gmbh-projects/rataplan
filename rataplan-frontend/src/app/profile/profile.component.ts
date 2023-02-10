import { Component, OnDestroy, OnInit } from '@angular/core';
import {FormControl, Validators} from "@angular/forms";
import {ActivatedRoute, Router} from "@angular/router";
import {ChangeEmailService} from "../services/change-profile-service/change-email-service";
import {ChangeDisplayNameService} from "../services/change-profile-service/change-displayName-service";
import { FormErrorMessageService } from "../services/form-error-message-service/form-error-message.service";
import { ExtraValidators } from "../validator/validators";
import { Store } from "@ngrx/store";
import { appState } from "../app.reducers";
import { UpdateUserdataAction } from "../authentication/auth.actions";
import { FrontendUser } from "../services/login.service/user.model";
import { Subscription } from "rxjs";
import { map } from "rxjs/operators";

@Component({
  selector: 'app-profile',
  templateUrl: './profile.component.html',
  styleUrls: ['./profile.component.css']
})
export class ProfileComponent implements OnInit, OnDestroy {
  userData?: FrontendUser;
  private userDataSub?: Subscription;

  constructor(
    private router: Router,
    private route: ActivatedRoute,
    private store: Store<appState>,
    private changeEmailService: ChangeEmailService,
    private changeDisplayNameService: ChangeDisplayNameService,
    public readonly errorMessageService: FormErrorMessageService
  ) { }

  ngOnInit(): void {
    this.userDataSub = this.store.select("auth").pipe(
      map(auth => auth.user)
    ).subscribe(user => this.userData = user);
  }

  ngOnDestroy() {
    this.userDataSub?.unsubscribe();
  }

  payloadArray={
    enabled:false
  }

  displayNameField = new FormControl('', [Validators.required, ExtraValidators.containsSomeWhitespace])
  emailField = new FormControl('', [Validators.required , Validators.email])

  changeDisplayName(){
      const displayName = this.displayNameField.value
    if (this.displayNameField.valid) {
      this.changeDisplayNameService.changeDisplayName(displayName).subscribe(() => {
        this.displayNameField.markAsUntouched()
        this.displayNameField.setValue('');
        this.store.dispatch(new UpdateUserdataAction());
      })
    }

  }
  changeEmail() {
    const email = this.emailField.value
    if (this.emailField.valid){
      this.changeEmailService.changeEmail(email).subscribe({next: () => {
          this.emailField.setValue('');
          this.emailField.markAsUntouched()
          this.store.dispatch(new UpdateUserdataAction())
        }, error: error => {
          console.log(error)
        }})
    }
  }



}
