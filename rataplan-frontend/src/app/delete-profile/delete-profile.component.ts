import { Component } from '@angular/core';
import { FormControl, FormGroup, Validators } from "@angular/forms";
import { DeleteProfileService, deletionChoices } from "../services/delete-profile-service/delete-profile.service";
import { Router } from "@angular/router";
import { MatSnackBar } from "@angular/material/snack-bar";

@Component({
  selector: 'app-delete-profile',
  templateUrl: './delete-profile.component.html',
  styleUrls: ['./delete-profile.component.css']
})
export class DeleteProfileComponent {
  busy: boolean = false;
  readonly formGroup = new FormGroup({
    backendChoice: new FormControl("DELETE", Validators.required),
    surveyToolChoice: new FormControl("DELETE", Validators.required),
    password: new FormControl(null, Validators.required)
  });

  constructor(
    private deleteProfileService: DeleteProfileService,
    private router: Router,
    private snackbar: MatSnackBar
  ) {
  }

  submit() {
    this.busy = true;
    const request: deletionChoices = this.formGroup.value;
    this.formGroup.disable();
    this.deleteProfileService.deleteProfile(request)
      .subscribe({
        next: _ => {
          this.busy = false;
          this.formGroup.enable();
          this.router.navigate(["/"]);
        },
        error: err => {
          console.log(err);
          this.busy = false;
          this.formGroup.enable();
          this.snackbar.open("Es ist ein Fehler aufgetreten", "OK");
        },
        complete: () => {
          this.busy = false;
          this.formGroup.enable();
        },
      });
  }
}
