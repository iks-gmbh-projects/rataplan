import { Component, OnInit } from '@angular/core';
import {LoginService} from "../services/login.service/login.service";
import {FormControl, Validators} from "@angular/forms";
import {ActivatedRoute, Router} from "@angular/router";
import {ChangeEmailService} from "../services/change-profile-service/change-email-service";
import {ChangeDisplayNameService} from "../services/change-profile-service/change-displayName-service";
import { FormErrorMessageService } from "../services/form-error-message-service/form-error-message.service";
import { ExtraValidators } from "../validator/validators";

@Component({
  selector: 'app-profile',
  templateUrl: './profile.component.html',
  styleUrls: ['./profile.component.css']
})
export class ProfileComponent implements OnInit {

  constructor(private loginService: LoginService,
              private router: Router,
              private route: ActivatedRoute,
              private changeEmailService: ChangeEmailService,
              private updateUser: LoginService,
              private changeDisplayNameService: ChangeDisplayNameService,
              public readonly errorMessageService: FormErrorMessageService) { }

  ngOnInit(): void {
  }


  setDisplayName() {
    return localStorage.getItem('displayname');

  }

  setEmail() {
    return localStorage.getItem('mail');

  }

  payloadArray={
    enabled:false
  }

  displayNameField = new FormControl('', [Validators.required, Validators.minLength(3), ExtraValidators.containsSomeWhitespace])
  emailField = new FormControl('', [Validators.required , Validators.minLength(3), Validators.email])

  redirect(){
    this.router.navigateByUrl('/change-password')
  }

  changeDisplayName(){
      const displayName = this.displayNameField.value
    if (this.displayNameField.valid) {
      this.changeDisplayNameService.changeDisplayName(displayName).subscribe(() => {
        this.displayNameField.markAsUntouched()
        this.displayNameField.setValue('');
        this.updateUser.getUserData().subscribe(res => {
          console.log(res)
        }, error => {
          console.log(error)
        })
      })
    }

  }
  changeEmail() {
    const email = this.emailField.value
    if (this.emailField.valid){
      this.changeEmailService.changeEmail(email).subscribe({next: () => {
          this.emailField.setValue('');
          this.emailField.markAsUntouched()
          this.updateUser.getUserData().subscribe(res => {
            console.log(res)
          }, error => {
            console.log(error)
          })
        }, error: error => {
          console.log(error)
        }})
    }
  }



}
