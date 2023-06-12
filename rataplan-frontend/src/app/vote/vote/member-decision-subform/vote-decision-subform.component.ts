import { Component, Inject, OnInit } from '@angular/core';
import { MAT_DIALOG_DATA } from '@angular/material/dialog';

import { VoteOptionModel } from '../../../models/vote-option.model';
import { VoteParticipantModel } from '../../../models/vote-participant.model';
import { DecisionType,VoteOptionDecisionType } from '../../vote-form/decision-type.enum';


export type DialogData = {
  vote: VoteOptionModel,
  voteParticipants: VoteParticipantModel[],
  decisionType: DecisionType,
};

@Component({
  selector: 'app-member-decision-subform',
  templateUrl: './vote-decision-subform.component.html',
  styleUrls: ['./vote-decision-subform.component.css']
})
export class VoteDecisionSubformComponent implements OnInit {
  readonly DecisionType = DecisionType;
  readonly VoteOptionDecisionType = VoteOptionDecisionType;
  allDecision: VoteOptionDecisionType[] | number[] = [];
  clicked: VoteOptionDecisionType = VoteOptionDecisionType.NO_ANSWER;

  constructor(@Inject(MAT_DIALOG_DATA) public data: DialogData) { }

  ngOnInit(): void {
    this.data.voteParticipants.forEach(
      participant => {
        participant.decisions.forEach(
          voteDecision => {
            if(voteDecision.optionId === this.data.vote.id) {
              if (voteDecision.decision) {
                this.allDecision.push(voteDecision.decision);
              } else if(voteDecision.participants) {
                this.allDecision.push(voteDecision.participants);
              }
            }
          });
      });
  }

  countDecision(number?: VoteOptionDecisionType) {
    if(this.data.decisionType === DecisionType.NUMBER) return (this.allDecision as number[]).reduce((a, b) => a+b, 0);
    return (this.allDecision as VoteOptionDecisionType[]).reduce((a, x) => x === number ? a+1 : a, 0);
  }

  checkMembers(voteParticipants: VoteParticipantModel[], number?: VoteOptionDecisionType) : string[] {
    const name: string[] = [];

    if(this.data.decisionType === DecisionType.NUMBER) {
      voteParticipants.forEach(participant => {
        participant.decisions.forEach(vote => {
          if (vote.optionId == this.data.vote.id && vote.participants) {
            if (participant.name == null) name.push('anonym: '+vote.participants);
            else name.push(participant.name+': '+vote.participants);
          }
        });
      });
    } else {
      voteParticipants.forEach(participant => {
        participant.decisions.forEach(vote => {
          if (vote.optionId == this.data.vote.id &&
            vote.decision == number) {
            if (participant.name == null) name.push('anonym');
            else name.push(participant.name);
          }
        });
      });
    }

    return name;
  }
}
