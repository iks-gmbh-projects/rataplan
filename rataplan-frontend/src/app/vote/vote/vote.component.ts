import { ViewportScroller } from '@angular/common';
import { Component, ElementRef, OnDestroy, OnInit, ViewChild } from '@angular/core';
import { NgModel, ValidatorFn } from '@angular/forms';
import { MatDialog } from '@angular/material/dialog';
import { MatSnackBar } from '@angular/material/snack-bar';
import { ActivatedRoute } from '@angular/router';
import { Store } from '@ngrx/store';
import { BehaviorSubject, delay, exhaustMap, filter, of, Subject, take, takeUntil } from 'rxjs';
import { switchMap } from 'rxjs/operators';
import { authFeature } from '../../authentication/auth.feature';
import { FrontendUser } from '../../models/user.model';
import { VoteDecisionModel } from '../../models/vote-decision.model';
import { VoteOptionModel } from '../../models/vote-option.model';
import { VoteParticipantModel } from '../../models/vote-participant.model';
import { VoteModel } from '../../models/vote.model';
import { FormErrorMessageService } from '../../services/form-error-message-service/form-error-message.service';
import { DecisionType, VoteOptionDecisionType } from '../vote-form/decision-type.enum';
import { PostVoteAction } from '../vote.actions';
import { voteFeature } from '../vote.feature';
import { VoteDecisionSubformComponent } from './member-decision-subform/vote-decision-subform.component';
import { VoteService } from './vote-service/vote.service';

const decisionCycle = {
  [VoteOptionDecisionType.NO_ANSWER]: VoteOptionDecisionType.ACCEPT,
  [VoteOptionDecisionType.ACCEPT]: VoteOptionDecisionType.ACCEPT_IF_NECESSARY,
  [VoteOptionDecisionType.ACCEPT_IF_NECESSARY]: VoteOptionDecisionType.DECLINE,
  [VoteOptionDecisionType.DECLINE]: VoteOptionDecisionType.NO_ANSWER,
} as const;

@Component({
  selector: 'app-vote',
  templateUrl: './vote.component.html',
  styleUrls: ['./vote.component.scss'],
})
export class VoteComponent implements OnInit, OnDestroy {
  readonly DecisionType = DecisionType;
  destroySubject: Subject<boolean> = new Subject<boolean>();
  vote!: VoteModel;
  voteParticipant: VoteParticipantModel = {
    voteId: 0,
    decisions: [],
  };
  
  isPreview = false;
  busy = false;
  readonly submitBusy$ = new BehaviorSubject<boolean>(false);
  readonly delayedSubmitBusy$ = this.submitBusy$.pipe(
    switchMap(v => v ? of(v).pipe(delay(1000)) : of(v)),
  );
  isEditMember = false;
  isYesVoteLimitMet!: boolean;
  votes: Map<number, boolean> = new Map<number, boolean>();
  @ViewChild('nameField') nameField?: NgModel;
  @ViewChild('scrollTo') scrollTo!: ElementRef;
  
  currentUser?: FrontendUser;
  private userVoted = false;
  
  constructor(
    public dialog: MatDialog,
    private snackBar: MatSnackBar,
    private route: ActivatedRoute,
    private viewportScroller: ViewportScroller,
    private voteService: VoteService,
    private store: Store,
    public readonly errorMessageService: FormErrorMessageService,
  )
  {
  }
  
  ngOnInit(): void {
    this.store.select(authFeature.selectAuthState).pipe(
      filter(state => !state.busy),
      takeUntil(this.destroySubject),
    ).subscribe(state => this.currentUser = state.user);
    
    this.route.data.subscribe(({isPreview, vote}) => {
      this.isPreview = isPreview;
      this.vote = vote;
      if(!this.hasDeadlinePassed()) {
        this.setVoteOptions();
      }
      this.userVoted = this.vote!.participants.some(participant =>
        participant.userId === this.currentUser?.id);
      if(this.currentUser) {
        this.voteParticipant.name = this.currentUser.displayname;
      }
    });
    
    this.store.select(voteFeature.selectBusy)
      .pipe(
        takeUntil(this.destroySubject),
      ).subscribe(busy => this.busy = busy);
  }
  
  ngOnDestroy(): void {
    this.destroySubject.next(true);
    this.destroySubject.complete();
  }
  
  saveVote() {
    this.voteParticipant.name = this.voteParticipant.name?.trim();
    if(this.isEditMember) {
      this.updateVote();
      return;
    }
    if(this.currentUser != null && this.userVoted) {
      this.snackBar.open('Sie haben schon abgestimmt, bitte bearbeiten Sie ihre schon abgegebenen Antworten', 'OK');
      this.resetVote();
      return;
    }
    this.submitBusy$.next(true);
    this.voteService.addVoteParticipant(this.vote!, this.voteParticipant)
      .pipe(takeUntil(this.destroySubject))
      .subscribe({
        next: participant => {
          this.submitBusy$.next(false);
          this.vote!.participants.push(participant);
          this.resetVote();
        },
        error: err => {
          this.submitBusy$.next(false);
          if(err.status == 401) this.snackBar.open(
            'Authorisierungsfehler (wahrscheinlich existiert ein veraltetes Cookie)',
            'OK',
          );
          else if(err.status == 403) this.snackBar.open('Sie sind nicht zum Abstimmen berechtigt.', 'OK');
          else this.snackBar.open('Unbekannter Fehler beim Abstimmen', 'OK');
        },
      });
  }
  
  updateVote() {
    this.submitBusy$.next(true);
    this.voteService.updateVoteParticipant(this.vote!, this.voteParticipant)
      .pipe(takeUntil(this.destroySubject))
      .subscribe({
        next: () => {
          this.submitBusy$.next(false);
          this.resetVote();
          this.isEditMember = false;
        },
        error: err => {
          this.submitBusy$.next(false);
          if(err.status == 401) this.snackBar.open(
            'Authorisierungsfehler (wahrscheinlich existiert ein veraltetes Cookie)',
            'OK',
          );
          else if(err.status == 403) this.snackBar.open('Sie sind nicht zum Ändern dieser Stimme berechtigt', 'OK');
          else this.snackBar.open('Unbekannter Fehler beim Ändern der Stimme', 'OK');
        },
      });
  }
  
  resetVote() {
    this.voteParticipant = {
      voteId: this.vote!.id!,
      decisions: [],
    };
    this.nameField?.reset();
    this.setVoteOptions();
    if(this.currentUser !== null) {
      this.voteParticipant.name = this.currentUser?.displayname;
    }
  }
  
  setVoteOptions() {
    if(this.vote?.voteConfig.decisionType === DecisionType.NUMBER) {
      this.voteParticipant.decisions = this.vote!.options.map(vote => (
        {
          optionId: vote.id!,
          participants: 0,
        }
      ));
    } else {
      this.voteParticipant.decisions = this.vote!.options.map((vote) => (
        {
          optionId: vote.id!,
          decision: VoteOptionDecisionType.NO_ANSWER,
        }
      ));
    }
  }
  
  openDialog(vote: VoteOptionModel) {
    this.dialog.open(VoteDecisionSubformComponent, {
      data: {
        vote: vote,
        voteParticipants: this.vote!.participants,
        decisionType: this.vote!.voteConfig.decisionType,
      },
      autoFocus: false,
    });
  }
  
  countParticipants(option: VoteOptionModel): `${number} (${number})` | `${number}` {
    const decisions = this.vote.participants.map(p => p.decisions.find(v => v.optionId === option.id)!);
    const accepted = decisions.reduce((a, d) => a + (
      (
        d.decision ?? VoteOptionDecisionType.ACCEPT
      ) === VoteOptionDecisionType.ACCEPT
        ? d.participants ?? 1
        : 0
    ), 0);
    if(this.vote.voteConfig.decisionType !== DecisionType.EXTENDED) return `${accepted}`;
    return `${accepted} (${decisions.reduce((a, d) => a + (
      d.decision === VoteOptionDecisionType.ACCEPT_IF_NECESSARY ? 1 : 0
    ), accepted)})`;
  }
  
  setParticipantNumber(vote: VoteOptionModel, participants: number | `${number}`) {
    const index = this.vote!.options.indexOf(vote);
    const voteDecision = this.voteParticipant.decisions[index];
    voteDecision.participants = Number(participants);
  }
  
  cycleDecision(vote: VoteOptionModel): void {
    const index = this.vote!.options.indexOf(vote);
    const voteDecision = this.voteParticipant.decisions[index];
    
    do {
      voteDecision.decision = decisionCycle[voteDecision.decision || VoteOptionDecisionType.NO_ANSWER];
    } while(!this.canChooseDecision(voteDecision.decision, vote));
  }
  
  private canChooseDecision(decision: VoteOptionDecisionType, vote: VoteOptionModel): boolean {
    switch(decision) {
    case VoteOptionDecisionType.ACCEPT_IF_NECESSARY:
      return this.vote!.voteConfig.decisionType == DecisionType.EXTENDED;
    case VoteOptionDecisionType.ACCEPT:
      if(this.isParticipantLimitMet(vote)) return false;
      if(!this.vote!.voteConfig.yesLimitActive) return true;
      return this.voteParticipant.decisions
        .map(d => d.decision)
        .filter(d => d == VoteOptionDecisionType.ACCEPT)
        .length < this.vote!.voteConfig.yesAnswerLimit!;
    }
    return true;
  }
  
  decisionDescription(vote: VoteOptionModel): {
    class: string,
    icon?: string,
    tooltip?: string,
    disabled?: boolean,
  } | null
  {
    const index = this.vote!.options.indexOf(vote);
    const voteDecision: VoteDecisionModel | undefined = this.voteParticipant.decisions[index];
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
  
  setDecision(vote: VoteOptionModel, decision: number) {
    const index = this.vote!.options.indexOf(vote);
    const voteDecision = this.voteParticipant.decisions[index];
    
    switch(decision) {
    case 1:
      voteDecision.decision = VoteOptionDecisionType.ACCEPT;
      if(this.votes.size < this.vote!.voteConfig?.yesAnswerLimit! && !this.votes.get(index)) {
        this.updateYesAnswerRestrictions(1, index);
      }
      break;
    case 2:
      voteDecision.decision = VoteOptionDecisionType.ACCEPT_IF_NECESSARY;
      break;
    default:
      voteDecision.decision = VoteOptionDecisionType.DECLINE;
      if(this.votes.get(index)) this.updateYesAnswerRestrictions(3, index);
      break;
    }
  }
  
  deleteMember(member: VoteParticipantModel) {
    this.voteService.deleteVoteParticipant(this.vote!, member)
      .pipe(
        exhaustMap(() => this.voteService.getVoteByParticipationToken(this.vote!.participationToken!)),
        take(1),
      )
      .subscribe({
        next: (updatedRequest: VoteModel) => {
          this.isEditMember = false;
          this.resetVote();
          this.vote = updatedRequest;
        },
        error: err => {
          this.snackBar.open('Unbekannter Fehler beim löschen der Stimme', 'OK');
        },
      });
  }
  
  editMember(member: VoteParticipantModel) {
    this.isEditMember = true;
    if(member.name != undefined) {
      this.voteParticipant = member;
    }
    this.scrollTo.nativeElement.scrollIntoView({
      behavior: 'smooth',
      block: 'start',
      inline: 'nearest',
    });
  }
  
  checkVoteOfMember(vote: VoteOptionModel, number: number) {
    return this.voteParticipant.decisions.find(a => a.optionId === vote.id)?.decision === number;
  }
  
  updateYesAnswerRestrictions(voteType: number, index: number) {
    const voteConfig = this.vote?.voteConfig;
    if(!voteConfig?.yesLimitActive) return;
    switch(voteType) {
    case 1:
      this.votes.set(index, true);
      this.isYesVoteLimitMet = this.votes.size >= voteConfig.yesAnswerLimit!;
      break;
    case 3:
      if(this.votes.get(index)) {
        this.votes.delete(index);
        this.isYesVoteLimitMet = false;
      }
      break;
    default:
      return;
    }
  }
  
  acceptPreview() {
    this.busy = true;
    this.store.dispatch(new PostVoteAction());
  }
  
  isParticipantLimitMet(voteOption: VoteOptionModel) {
    if(!voteOption.participantLimitActive) return false;
    const voteDecisions = this.vote.participants
      .flatMap(participant => participant.decisions)
      .filter(decision => decision.decision === 1 && decision.optionId === voteOption.id)
      .length;
    return !(
      voteDecisions < voteOption.participantLimit!
    );
  }
  
  hasDeadlinePassed(): boolean {
    const deadline = new Date(this.vote.deadline);
    if(!deadline) {
      throw new Error('Deadline has not been set.');
    }
    deadline.setDate(deadline.getDate() + 1);
    return deadline.getTime() < Date.now();
  }
  
  static yesAnswerLimitMoreThanZeroOrNull(): ValidatorFn {
    return (c) => {
      if(c.parent?.get('yesLimitActive')?.value) return c.value > 0 ? null : {'invalid yes answer limit': true};
      else return null;
    };
  }
  
  checkEnteredValue(index: number, participant: HTMLInputElement) {
    if(!participant.value.match(/^\d+$/)) {
      this.voteParticipant.decisions[index]!.participants = 0;
      participant.value = '0';
    }
  }
  
}