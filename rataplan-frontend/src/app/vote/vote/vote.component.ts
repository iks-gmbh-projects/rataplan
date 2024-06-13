import { Component, ElementRef, ViewChild } from '@angular/core';
import { NgModel } from '@angular/forms';
import { MatDialog } from '@angular/material/dialog';
import { MatSnackBar } from '@angular/material/snack-bar';
import { Store } from '@ngrx/store';
import { delay, Observable, of } from 'rxjs';
import { map, switchMap } from 'rxjs/operators';
import { authFeature } from '../../authentication/auth.feature';
import { FrontendUser } from '../../models/user.model';
import { VoteDecisionModel } from '../../models/vote-decision.model';
import { VoteOptionModel } from '../../models/vote-option.model';
import { VoteParticipantModel } from '../../models/vote-participant.model';
import { VoteModel } from '../../models/vote.model';
import { defined } from '../../operators/non-empty';
import { FormErrorMessageService } from '../../services/form-error-message-service/form-error-message.service';
import { DecisionType, VoteOptionDecisionType } from '../vote-form/decision-type.enum';
import { voteFormAction } from '../vote-form/state/vote-form.action';
import { VoteDecisionSubformComponent } from './member-decision-subform/vote-decision-subform.component';
import { voteAction } from './state/vote.action';
import { voteFeature } from './state/vote.feature';

@Component({
  selector: 'app-vote',
  templateUrl: './vote.component.html',
  styleUrls: ['./vote.component.scss'],
})
export class VoteComponent {
  readonly currentUser$: Observable<FrontendUser | undefined>;
  readonly vote$: Observable<VoteModel>;
  readonly preview$: Observable<boolean>;
  readonly participant$: Observable<VoteParticipantModel>;
  readonly editing$: Observable<boolean>;
  readonly busy$: Observable<boolean>;
  readonly delayedBusy$;
  @ViewChild('nameField') nameField?: NgModel;
  @ViewChild('scrollTo') scrollTo!: ElementRef;
  
  constructor(
    protected readonly dialog: MatDialog,
    private readonly snackBar: MatSnackBar,
    private readonly store: Store,
    protected readonly errorMessageService: FormErrorMessageService,
  )
  {
    this.currentUser$ = store.select(authFeature.selectUser);
    this.vote$ = store.select(voteFeature.selectVote).pipe(defined);
    this.preview$ = store.select(voteFeature.selectPreview);
    this.editing$ = store.select(voteFeature.selectParticipantIndex).pipe(
      map(i => i >= 0),
    );
    this.participant$ = store.select(voteFeature.selectCurrentParticipant);
    this.busy$ = store.select(voteFeature.selectBusy);
    this.delayedBusy$ = this.busy$.pipe(
      switchMap(v => v ? of(v).pipe(delay(1000)) : of(v)),
    );
  }
  
  saveVote() {
    this.store.dispatch(voteAction.submitParticipant());
  }
  
  openDialog(voteOption: VoteOptionModel, vote: VoteModel) {
    this.dialog.open(VoteDecisionSubformComponent, {
      data: {
        vote: voteOption,
        voteParticipants: vote.participants,
        decisionType: vote.voteConfig.decisionType,
      },
      autoFocus: false,
    });
  }
  
  countParticipants(option: VoteOptionModel, vote: VoteModel): `${number} (${number})` | `${number}` {
    const decisions = vote.participants.map(p => p.decisions.find(v => v.optionId === option.id)!);
    const accepted = decisions.reduce((a, d) => a + (
      (
        d.decision ?? VoteOptionDecisionType.ACCEPT
      ) === VoteOptionDecisionType.ACCEPT
        ? d.participants ?? 1
        : 0
    ), 0);
    if(vote.voteConfig.decisionType !== DecisionType.EXTENDED) return `${accepted}`;
    return `${accepted} (${decisions.reduce((a, d) => a + (
      d.decision === VoteOptionDecisionType.ACCEPT_IF_NECESSARY ? 1 : 0
    ), accepted)})`;
  }
  
  setParticipantNumber(vote: VoteOptionModel, participants: number | `${number}`) {
    this.store.dispatch(voteAction.setDecision({
      option: vote.id!,
      participants: Number(participants),
    }))
  }
  
  cycleDecision(vote: VoteOptionModel): void {
    this.store.dispatch(voteAction.cycleDecision({option: vote.id!}));
  }
  
  decisionDescription(voteOption: VoteOptionModel, participant: VoteParticipantModel): {
    class: string,
    icon?: string,
    tooltip?: string,
    disabled?: boolean,
  } | null
  {
    const voteDecision: VoteDecisionModel | undefined = participant.decisions.find(d => d.optionId === voteOption.id);
    switch(voteDecision?.decision) {
    case VoteOptionDecisionType.ACCEPT:
      return {
        class: 'checkedAccept',
        tooltip: 'Ja',
        icon: 'done',
      };
    case VoteOptionDecisionType.ACCEPT_IF_NECESSARY:
      return {
        class: 'checkedAcceptIfNecessary',
        tooltip: 'Vielleicht',
        icon: 'question_mark',
      };
    case VoteOptionDecisionType.DECLINE:
      return {
        class: 'checkedDecline',
        tooltip: 'Nein',
        icon: 'close',
      };
    case VoteOptionDecisionType.NO_ANSWER:
      return {
        class: 'checkedNoAnswer',
        tooltip: 'Keine Antwort',
      };
    case undefined:
      return {
        class: '',
        tooltip: 'Abstimmung abgelaufen',
        icon: 'history_toggle_off',
        disabled: true,
      };
    default:
      return {
        class: '',
      };
    }
  }
  
  deleteMember(index: number) {
    this.store.dispatch(voteAction.deleteParticipant({index}));
  }
  
  editMember(index: number) {
    this.store.dispatch(voteAction.selectParticipant({index}));
    this.scrollTo.nativeElement.scrollIntoView({
      behavior: 'smooth',
      block: 'start',
      inline: 'nearest',
    });
  }
  
  acceptPreview() {
    this.store.dispatch(voteFormAction.post());
  }
  
  hasDeadlinePassed(vote: VoteModel): boolean {
    const deadline = new Date(vote.deadline);
    if(!deadline) {
      throw new Error('Deadline has not been set.');
    }
    return deadline.getTime() < Date.now();
  }
  
  protected readonly DecisionType = DecisionType;
}