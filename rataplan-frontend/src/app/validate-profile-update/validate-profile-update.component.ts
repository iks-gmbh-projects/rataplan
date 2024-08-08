import { Component, Inject, OnInit } from '@angular/core';
import { FormControl, Validators } from '@angular/forms';
import { MAT_DIALOG_DATA } from '@angular/material/dialog';
import { Store } from '@ngrx/store';
import { authActions } from '../authentication/auth.actions';
import { FrontendUser } from '../models/user.model';
import { FormErrorMessageService } from '../services/form-error-message-service/form-error-message.service';

@Component({
  selector: 'app-validate-profile-update',
  templateUrl: './validate-profile-update.component.html',
  styleUrls: ['./validate-profile-update.component.css'],
})
export class ValidateProfileUpdateComponent implements OnInit {

  enterPassword = false;
  password = new FormControl<string | undefined>(undefined, Validators.required);

  constructor(
    public errorMessagingService: FormErrorMessageService,
    @Inject(MAT_DIALOG_DATA) public data: FrontendUser,
    private store: Store,
  ) {
  }

  ngOnInit(): void {
  }

  confirmProfileChanges() {
    this.store.dispatch(authActions.changeProfileDetails({
      user: {
        ...this.data,
        password: this.password.value!,
      },
    }));
  }

}