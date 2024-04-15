import { Component, EventEmitter, Input, OnInit, Output } from '@angular/core';
import { AbstractControl, FormGroup } from '@angular/forms';
import { FormErrorMessageService } from '../../../../services/form-error-message-service/form-error-message.service';

export type HeadFormFields = {
  id: string | number | null,
  accessId: string | null,
  participationId: string | null,
  name: string | null,
  description: string | null,
  startDate: Date | null,
  endDate: Date | null,
  openAccess: boolean | null,
  anonymousParticipation: boolean | null,
};

@Component({
  selector: 'app-survey-create-form-head',
  templateUrl: './survey-create-form-head.component.html',
  styleUrls: ['./survey-create-form-head.component.css']
})
export class SurveyCreateFormHeadComponent implements OnInit {
  @Input("form") formGroup?: FormGroup<{[K in keyof HeadFormFields]: AbstractControl<HeadFormFields[K]>}>;
  @Output() readonly submit = new EventEmitter<void>();

  public readonly yesterday = new Date();

  constructor(
    readonly errorMessageService: FormErrorMessageService
  ) { }

  ngOnInit(): void {
  }

  public headerComplete(): boolean {
    if (!this.formGroup) return false;
    return this.formGroup.controls.name.valid
      && this.formGroup.controls.description.valid
      && this.formGroup.controls.startDate.valid
      && this.formGroup.controls.endDate.valid;
  }
}