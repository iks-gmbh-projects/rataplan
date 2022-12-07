import { Component } from '@angular/core';
import { FormControl, FormGroup, Validators } from "@angular/forms";
import { DeleteProfileService } from "../services/delete-profile-service/delete-profile.service";

@Component({
  selector: 'app-delete-profile',
  templateUrl: './delete-profile.component.html',
  styleUrls: ['./delete-profile.component.css']
})
export class DeleteProfileComponent {
  readonly formGroup = new FormGroup({
    backendChoice: new FormControl("DELETE", Validators.required),
    surveyToolChoice: new FormControl("DELETE", Validators.required),
    password: new FormControl(null, Validators.required)
  });

  constructor(private deleteProfileService: DeleteProfileService) { }

  submit() {
    this.deleteProfileService.deleteProfile(this.formGroup.value);
  }
}
