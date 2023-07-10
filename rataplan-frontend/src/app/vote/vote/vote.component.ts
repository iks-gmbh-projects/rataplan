import { Component, OnDestroy, OnInit, ViewChild } from '@angular/core';
import { NgModel } from '@angular/forms';
import { MatDialog } from '@angular/material/dialog';
import { ActivatedRoute } from '@angular/router';
import { Store } from '@ngrx/store';
import { exhaustMap, filter, Subject, take, takeUntil } from 'rxjs';
import { FrontendUser } from '../../models/user.model';
import { VoteModel } from '../../models/vote.model';
import { VoteOptionModel } from '../../models/vote-option.model';
import { VoteParticipantModel } from '../../models/vote-participant.model';
import { DeadlineService } from '../../services/deadline-service/deadline.service';
import { FormErrorMessageService } from '../../services/form-error-message-service/form-error-message.service';
import { PostVoteAction } from '../vote.actions';
import { DecisionType, VoteOptionDecisionType } from '../vote-form/decision-type.enum';
import { VoteDecisionSubformComponent } from './member-decision-subform/vote-decision-subform.component';
import { VoteService } from './vote-service/vote.service';
import { voteFeature } from '../vote.feature';
import { authFeature } from '../../authentication/auth.feature';
import { MatSnackBar } from '@angular/material/snack-bar';


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
  isEditMember = false;
  isYesVoteLimitMet!: boolean;
  yesVoteCount = 0;
  votes: Map<number, boolean> = new Map<number, boolean>();
  @ViewChild('nameField') nameField?: NgModel;

  currentUser?: FrontendUser;
  private userVoted = false;

  constructor(
    public dialog: MatDialog,
    private snackBar: MatSnackBar,
    private route: ActivatedRoute,
    private voteService: VoteService,
    public deadlineService: DeadlineService,
    private store: Store,
    public readonly errorMessageService: FormErrorMessageService,
  ) {
  }

  ngOnInit(): void {
    this.store.select(authFeature.selectAuthState).pipe(
      filter(state => !state.busy),
      takeUntil(this.destroySubject),
    ).subscribe(state => this.currentUser = state.user);

    this.route.data.subscribe(({ isPreview, vote }) => {
      this.isPreview = isPreview;
      this.vote = vote;
      this.deadlineService.setDeadline(new Date(vote.deadline));
      if(!this.deadlineService.hasDeadlinePassed()) {
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
    if(this.isEditMember) {
      this.updateVote();
      return;
    }
    if(this.currentUser != null && this.userVoted) {
      console.log(this.currentUser + ' hat schon abgestimmt');
      this.resetVote();
      return;
    }
    this.voteService.addVoteParticipant(this.vote!, this.voteParticipant)
      .pipe(takeUntil(this.destroySubject))
      .subscribe({
        next: participant => {
          this.vote!.participants.push(participant);
          this.resetVote();
        },
        error: err => {
          this.snackBar.open("Unbekannter Fehler beim Abstimmen", "OK");
          console.log(err);
        },
      });
  }

  updateVote() {
    this.voteService.updateVoteParticipant(this.vote!, this.voteParticipant)
      .pipe(takeUntil(this.destroySubject))
      .subscribe({
        next: () => {
          this.resetVote();
          this.isEditMember = false;
        },
        error: err => {
          this.snackBar.open("Unbekannter Fehler beim Ändern der Stimme", "OK");
          console.log(err);
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
      this.voteParticipant.decisions = this.vote!.options.map(vote => ({
        optionId: vote.id!,
        participants: 0,
      }));
    } else {
      this.voteParticipant.decisions = this.vote!.options.map((vote) => ({
        optionId: vote.id!,
        decision: VoteOptionDecisionType.NO_ANSWER,
      }));
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

  setParticipantNumber(vote: VoteOptionModel, participants: number) {
    const index = this.vote!.options.indexOf(vote);
    const voteDecision = this.voteParticipant.decisions[index];
    voteDecision.participants = participants;
  }

  setDecision(vote: VoteOptionModel, decision: number) {
    const index = this.vote!.options.indexOf(vote);
    const voteDecision = this.voteParticipant.decisions[index];

    switch (decision) {
      case 1:
        voteDecision.decision = VoteOptionDecisionType.ACCEPT;
        if (this.votes.size < this.vote!.voteConfig?.yesAnswerLimit! && !this.votes.get(index)){
          this.updateYesAnswerRestrictions(1,index);
        }break;
      case 2:
        voteDecision.decision = VoteOptionDecisionType.ACCEPT_IF_NECESSARY;
        break;
      default:
        voteDecision.decision = VoteOptionDecisionType.DECLINE;if (this.votes.get(index))this.updateYesAnswerRestrictions(3,index);
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
          this.snackBar.open("Unbekannter Fehler beim löschen der Stimme", "OK");
          console.log(err);
        },
      });
  }

  editMember(member: VoteParticipantModel) {
    this.isEditMember = true;
    if(member.name != undefined) {
      this.voteParticipant = member;
    }
  }

  checkVoteOfMember(vote: VoteOptionModel, number: number) {
    return this.voteParticipant.decisions.find(a => a.optionId === vote.id)?.decision === number;
  }

  updateYesAnswerRestrictions(voteType: number, index: number) {
    const voteConfig = this.vote?.voteConfig;
    if (!voteConfig?.yesLimitActive) return;
    console.log('fsd');
    switch (voteType) {
      case 1:
        this.votes.set(index, true);
        this.isYesVoteLimitMet = this.votes.size >= voteConfig.yesAnswerLimit!;
        break;
      case 3:
        if (this.votes.get(index)) {
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
}
