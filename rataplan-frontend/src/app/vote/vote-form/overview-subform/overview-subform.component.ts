import { Component, OnInit, OnDestroy } from '@angular/core';
import { FormGroup, FormControl, Validators } from '@angular/forms';
import { MatDialog } from '@angular/material/dialog';
import { Store } from '@ngrx/store';
import { filter, Subscription } from 'rxjs';

import { VoteOptionConfig, VoteOptionModel } from '../../../models/vote-option.model';
import { FormErrorMessageService } from '../../../services/form-error-message-service/form-error-message.service';
import { AddVoteOptionsAction, EditVoteOptionAction, RemoveVoteOptionAction } from '../../vote.actions';
import { ConfirmChoiceComponent } from '../confirm-choice/confirm-choice.component';
import { CONFIRM_CHOICE_OPTIONS, VoteOptionDecisionType } from '../decision-type.enum';
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
  participantLimitActive: boolean,
  participantLimit: number | null
};

@Component({
  selector: 'app-overview-subform',
  templateUrl: './overview-subform.component.html',
  styleUrls: ['./overview-subform.component.css'],
})
export class OverviewSubformComponent implements OnInit, OnDestroy {
  voteOptions: VoteOptionModel[] = [];
  originalParticipationLimit: Map<number, number> = new Map<number, number>();
  voteConfig: VoteOptionConfig = {
    startDate: true,
    startTime: false,
    endDate: false,
    endTime: false,
    description: false,
    url: false,
  };

  participantLimitActive: FormControl = new FormControl(false);
  participantLimit: FormControl = new FormControl(null, [Validators.min(1)]);
  vote = new FormGroup({
    voteIndex: new FormControl(null),
    startDateInput: new FormControl(null),
    endDateInput: new FormControl(null),
    startTimeInput: new FormControl(null),
    endTimeInput: new FormControl(null),
    descriptionInput: new FormControl(null),
    linkInput: new FormControl(null),
    participantLimitActive: this.participantLimitActive,
    participantLimit: this.participantLimit
  });

  private storeSub?: Subscription;

  constructor(
    private store: Store,
    public errorMessageService: FormErrorMessageService,
    private dialog: MatDialog
  ) {
  }

  ngOnInit(): void {
    this.storeSub = this.store.select(voteFeature.selectVote).pipe(
      filter(vote => !!vote),
    ).subscribe(request => {
      this.voteConfig = request!.voteConfig.voteOptionConfig;
      this.voteOptions = request!.options;
      if (this.originalParticipationLimit.size === 0) {
        this.voteOptions.forEach(vo => {
          if (vo.id && vo.participantLimitActive && vo.participantLimit != null) this.originalParticipationLimit.set(vo.id, vo.participantLimit!);
          else if (vo.id != undefined) {
            const participantCount = request!.participants
              .map(p => p.decisions).flatMap(s1 => s1)
              .filter(d => d.optionId == vo.id)
              .filter(d => d.decision == VoteOptionDecisionType.ACCEPT)
              .length;
            this.originalParticipationLimit.set(vo.id!, participantCount);
            if (participantCount != 0) this.originalParticipationLimit.set(vo.id,participantCount);
          }
        }
        );
      }
    });
  }

  ngOnDestroy(): void {
    this.storeSub?.unsubscribe();
  }

  clearContent() {
    this.vote.reset();
    this.editIndex = null;
  }

  sanitiseParticipationLimit(checked: boolean) {
    if (!checked) {
      this.participantLimit.setValue(null);
      this.participantLimit.markAsPristine();
      this.participantLimit.setErrors(null);
    }
  }
  addVoteOption() {
    if (!this.isInputInForm()) {
      return;
    }
    const input: formValue = this.vote.value;
    const updateOption = () => {
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
      voteOption.participantLimitActive = input.participantLimitActive || false;
      voteOption.participantLimit = input.participantLimitActive ? input.participantLimit : null;
      voteOption.id = this.editIndex === null ? undefined : this.voteOptions[this.editIndex!].id;
      if (voteOption.id !== undefined) {
        this.store.dispatch(new EditVoteOptionAction(input.voteIndex!, voteOption));
      } else {
        this.store.dispatch(new AddVoteOptionsAction(voteOption));
      }
      this.clearContent();
    };
    if (this.editIndex != null && input.participantLimitActive) {
      if (input.participantLimit! < this.originalParticipationLimit.get(this.voteOptions[this.editIndex].id!)!) {
        this.dialog.open(ConfirmChoiceComponent, { data: { option: CONFIRM_CHOICE_OPTIONS.PARTICIPANT_LIMIT }}).afterClosed()
          .subscribe(choice => {
            if (choice){
              const key = this.voteOptions[this.editIndex!].id;
              this.originalParticipationLimit.set(key!,-1);
              updateOption();
            }
            else this.clearContent();
          });
      } else updateOption();
    } else updateOption();
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
      voteIndex: index,
      participantLimitActive: voteOption.participantLimitActive || false,
      participantLimit: voteOption.participantLimit || null,
    });
    if (voteOption.id) this.editIndex = index;
  }

  editIndex: number | null = null;
}
