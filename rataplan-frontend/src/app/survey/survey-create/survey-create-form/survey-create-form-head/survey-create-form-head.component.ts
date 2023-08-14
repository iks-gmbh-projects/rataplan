import { Component, EventEmitter, Input, OnInit, Output } from '@angular/core';
import { FormErrorMessageService } from '../../../../services/form-error-message-service/form-error-message.service';
import { FormGroup } from '@angular/forms';

@Component({
  selector: 'app-survey-create-form-head',
  templateUrl: './survey-create-form-head.component.html',
  styleUrls: ['./survey-create-form-head.component.css']
})
export class SurveyCreateFormHeadComponent implements OnInit {
  @Input("form") formGroup?: FormGroup;
  @Output() readonly submit = new EventEmitter<void>();

  public get yesterday(): Date {
    let ms = Date.now();
    ms -= 24*3600000;
    ms -= ms % 60000;
    return new Date(ms);
  }

  constructor(
    readonly errorMessageService: FormErrorMessageService
  ) { }

  ngOnInit(): void {
  }

  public headerComplete(): boolean {
    if (!this.formGroup) return false;
    return this.formGroup.get("name")!.valid
      && this.formGroup.get("description")!.valid
      && this.formGroup.get("startDate")!.valid
      && this.formGroup.get("endDate")!.valid;
  }
}
