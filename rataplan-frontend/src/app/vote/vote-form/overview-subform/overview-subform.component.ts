import { Component, OnDestroy, OnInit } from '@angular/core';
import { FormControl, FormGroup, Validators } from '@angular/forms';
import { Store } from '@ngrx/store';
import { filter, map, Subscription } from 'rxjs';

import { VoteOptionConfig, VoteOptionModel } from '../../../models/vote-option.model';
import { FormErrorMessageService } from '../../../services/form-error-message-service/form-error-message.service';
import { AddVoteOptionsAction, EditVoteOptionAction, RemoveVoteOptionAction } from '../../vote.actions';
import { combineDateTime } from '../vote-form.service';
import { voteFeature } from '../../vote.feature';

function extractTime(date: string | undefined | null): string | null {
  if (!date) return null;
  const dateObject = new Date(date);
  return dateObject.getHours().toString().padStart(2, '0') + ':' + dateObject.getMinutes().toString().padStart(2, '0');
}

type formValue = {
  voteIndex: number | null,
  startDateInput: string | null,
  endDateInput: string | null,
  startTimeInput: string | null,
  endTimeInput: string | null,
  descriptionInput: string | null,
  linkInput: string | null,
};

@Component({
  selector: 'app-overview-subform',
  templateUrl: './overview-subform.component.html',
  styleUrls: ['./overview-subform.component.css'],
})
export class OverviewSubformComponent implements OnInit, OnDestroy {
  voteOptions: VoteOptionModel[] = [];
  voteConfig: VoteOptionConfig = {
    startDate: true,
    startTime: false,
    endDate: false,
    endTime: false,
    description: false,
    url: false,
  };
  vote = new FormGroup({
    voteIndex: new FormControl(null),
    startDateInput: new FormControl(null),
    endDateInput: new FormControl(null),
    startTimeInput: new FormControl(null),
    endTimeInput: new FormControl(null),
    descriptionInput: new FormControl(null, [
      Validators.maxLength(100),
    ]),
    linkInput: new FormControl(null, [
      Validators.max(150),
    ]),
  });

  private storeSub?: Subscription;

  constructor(
    private store: Store,
    public errorMessageService: FormErrorMessageService,
  ) {
  }

  ngOnInit(): void {
    this.storeSub = this.store.select(voteFeature.selectVoteState).pipe(
      filter(request => !!request.vote),
      map(state => state.vote!),
    ).subscribe(request => {
      this.voteConfig = request.voteConfig.voteOptionConfig;
      this.voteOptions = request.options;
    });
  }

  ngOnDestroy(): void {
    this.storeSub?.unsubscribe();
  }

  clearContent() {
    this.vote.reset();
  }

  addVoteOption() {
    if (!this.isInputInForm()) {
      return;
    }

    const input: formValue = this.vote.value;
    const voteOption: VoteOptionModel = {};
    voteOption.startDate = combineDateTime(
      input.startDateInput, input.startTimeInput,
    )!;
    if (this.voteConfig.endDate || this.voteConfig.endTime) {
      voteOption.endDate = combineDateTime(
        input.endDateInput || input.startDateInput, input.endTimeInput,
      )!;
    }

    voteOption.description = input.descriptionInput || undefined;
    voteOption.url = input.linkInput || undefined;

    if (input.voteIndex !== null) {
      this.store.dispatch(new EditVoteOptionAction(input.voteIndex, voteOption));
    } else {
      this.store.dispatch(new AddVoteOptionsAction(voteOption));
    }

    console.log(this.vote.get('timeInput')?.value);
    console.log(this.voteOptions);
    this.clearContent();
  }

  isInputInForm() {
    let isInputInForm = false;
    console.log(this.voteOptions);
    Object.values(this.vote.value).forEach(value => {
      if (value) {
        isInputInForm = true;
      }
    });
    return isInputInForm;
  }

  deleteVoteOption(index: number) {
    this.store.dispatch(new RemoveVoteOptionAction(index));
  }

  editVoteOption(index: number) {
    const voteOption = this.voteOptions[index];
    this.vote.setValue({
      startDateInput: voteOption.startDate || null,
      endDateInput: voteOption.endDate || null,
      startTimeInput: extractTime(voteOption.startDate),
      endTimeInput: extractTime(voteOption.endDate),
      descriptionInput: voteOption.description || null,
      linkInput: voteOption.url || null,
      voteIndex: index
    });
  }
}
