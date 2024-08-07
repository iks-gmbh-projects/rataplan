import { Component, OnDestroy, OnInit } from '@angular/core';
import { FormControl, FormGroup, Validators } from '@angular/forms';
import { MatDialog } from '@angular/material/dialog';
import { MtxDatetimepickerType } from '@ng-matero/extensions/datetimepicker';
import { Store } from '@ngrx/store';
import { filter, Observable, of, Subscription } from 'rxjs';

import { VoteOptionConfig, VoteOptionModel } from '../../../models/vote-option.model';
import { FormErrorMessageService } from '../../../services/form-error-message-service/form-error-message.service';
import { ConfirmChoiceComponent } from '../confirm-choice/confirm-choice.component';
import { CONFIRM_CHOICE_OPTIONS, VoteOptionDecisionType } from '../decision-type.enum';
import { AddVoteOptionsAction, EditVoteOptionAction, RemoveVoteOptionAction } from '../state/vote.actions';
import { voteFeature } from '../state/vote.feature';
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
  
  get startLabel(): string {
    const end = this.voteConfig.endDate || this.voteConfig.endTime;
    if(this.voteConfig.startTime) return end ? 'Startzeit' : 'Uhrzeit';
    else return end ? 'Startdatum' : 'Datum';
  }
  
  get startType(): MtxDatetimepickerType {
    if(this.voteConfig.startTime) {
      return `${this.voteConfig.startDate ? 'date' : ''}time`;
    }
    return 'date';
  }
  
  get endLabel(): string {
    const start = this.voteConfig.startDate || this.voteConfig.startTime;
    if(this.voteConfig.endTime) return start ? 'Endzeit' : 'Uhrzeit';
    else return start ? 'Enddatum' : 'Datum';
  }
  
  get endType(): MtxDatetimepickerType {
    if(this.voteConfig.endTime) {
      return `${this.voteConfig.endDate ? 'date' : ''}time`;
    }
    return 'date';
  }

  participantLimitActive = new FormControl<boolean>(false);
  participantLimit = new FormControl<number | null>(null, [Validators.min(1)]);
  vote = new FormGroup({
    voteIndex: new FormControl<number | null>(null),
    startDateInput: new FormControl<Date | null>(null),
    endDateInput: new FormControl<Date | null>(null),
    descriptionInput: new FormControl<string | null>(null),
    linkInput: new FormControl<string | null>(null),
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
    const input = this.vote.value;
    let proceed: Observable<boolean> = of(true);
    if (input.voteIndex != null && input.participantLimitActive) {
      if (input.participantLimit! < this.originalParticipationLimit.get(this.voteOptions[input.voteIndex].id!)!) {
        proceed = this.dialog.open(ConfirmChoiceComponent, { data: { option: CONFIRM_CHOICE_OPTIONS.PARTICIPANT_LIMIT }}).afterClosed();
      }
    }
    proceed.subscribe(proceed => {
      if(proceed) {
        const voteOption: VoteOptionModel = {};
        voteOption.startDate = input.startDateInput?.toISOString();
        voteOption.endDate = input.endDateInput?.toISOString();
        voteOption.description = input.descriptionInput ?? undefined;
        voteOption.url = input.linkInput ?? undefined;
        voteOption.participantLimitActive = input.participantLimitActive ?? false;
        voteOption.participantLimit = input.participantLimitActive ? input.participantLimit : null;
        if (input.voteIndex !== null) {
          this.store.dispatch(new EditVoteOptionAction(input.voteIndex!, voteOption));
        } else {
          this.store.dispatch(new AddVoteOptionsAction(voteOption));
        }
      }
      this.clearContent();
    });
  }

  isInputInForm() {
    let isInputInForm = false;
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
      startDateInput: voteOption.startDate ? new Date(voteOption.startDate) : null,
      endDateInput: voteOption.endDate ? new Date(voteOption.endDate) : null,
      descriptionInput: voteOption.description || null,
      linkInput: voteOption.url || null,
      voteIndex: index,
      participantLimitActive: voteOption.participantLimitActive || false,
      participantLimit: voteOption.participantLimit || null,
    });
  }
}