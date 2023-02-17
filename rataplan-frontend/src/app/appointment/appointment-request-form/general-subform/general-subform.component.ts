import { Component, OnDestroy, OnInit } from '@angular/core';
import { FormControl, FormGroup, Validators } from '@angular/forms';
import { ActivatedRoute, Router } from '@angular/router';
import { Subscription } from 'rxjs';
import { FormErrorMessageService } from '../../../services/form-error-message-service/form-error-message.service';
import { ExtraValidators } from '../../../validator/validators';
import { appState } from "../../../app.reducers";
import { Store } from "@ngrx/store";
import { SetGeneralValuesAppointmentAction } from "../../appointment.actions";

@Component({
  selector: 'app-general-subform',
  templateUrl: './general-subform.component.html',
  styleUrls: ['./general-subform.component.css'],
})
export class GeneralSubformComponent implements OnInit, OnDestroy {
  minDate: Date;
  maxDate: Date;

  generalSubform = new FormGroup({
    'title': new FormControl(null, [Validators.required, ExtraValidators.containsSomeWhitespace]),
    'description': new FormControl(null),
    'deadline': new FormControl(null, Validators.required),
    'decision': new FormControl('0', Validators.required),
  });

  showDescription = false;

  private storeSub?: Subscription;

  constructor(
    private store: Store<appState>,
    public readonly errorMessageService: FormErrorMessageService,
    private router: Router,
    private activeRoute: ActivatedRoute
  ) {
    const currentYear = new Date().getFullYear();
    this.minDate = new Date();
    this.minDate.setHours(0, 0, 0, 0);
    this.maxDate = new Date(currentYear + 2, 11, 31);
  }

  ngOnInit(): void {
    this.storeSub = this.store.select("appointmentRequest")
      .subscribe(state => {
        const appointmentRequest = state.appointmentRequest;
        const title = appointmentRequest?.title;
        const deadline = appointmentRequest?.deadline;

        if (title || deadline) {
          this.generalSubform.get('title')?.setValue(title);
          this.generalSubform.get('description')?.setValue(appointmentRequest.description);
          this.generalSubform.get('deadline')?.setValue(deadline);
          this.generalSubform.get('decision')?.setValue('0');
        }
        if (this.generalSubform.get('description')?.value) {
          this.showDescription = true;
        }
      });
  }

  ngOnDestroy(): void {
    this.storeSub?.unsubscribe();
  }

  addAndDeleteDescription() {
    this.showDescription = !this.showDescription;
    if (!this.showDescription) {
      this.generalSubform.get('description')?.setValue(null);
    }
  }

  nextPage() {
    this.store.dispatch(new SetGeneralValuesAppointmentAction({
      title: this.generalSubform.value.title,
      description: this.generalSubform.value.description,
      deadline: new Date(this.generalSubform.value.deadline),
    }));
    console.log(this.generalSubform.get('title'));
    console.log(this.generalSubform.get('deadline'));
    this.router.navigate(['..', 'configurationOptions'], { relativeTo: this.activeRoute });
  }
}
