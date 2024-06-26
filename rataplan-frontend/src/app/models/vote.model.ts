import {
  DecisionType,
  deserializeDecisionType,
  SerializedDecisionType
} from '../vote/vote-form/decision-type.enum';
import { deserializeVoteOptionDecisionModel } from './vote-decision.model';
import { deserializeVoteOptionModel, VoteOptionConfig, VoteOptionModel } from './vote-option.model';
import { VoteParticipantModel } from './vote-participant.model';

export type VoteModel<serialized extends boolean = false> = {
  id?: number,
  title: string,
  description?: string,
  deadline: string,
  organizerName?: string,
  notificationSettings?: VoteNotificationSettings,
  consigneeList: string[],
  userConsignees: (string|number)[],

  backendUserid?: number,
  expired?: boolean,
  participationToken?: string,
  editToken?: string,

  voteConfig: VoteConfig<serialized>,
  options: VoteOptionModel<serialized>[],
  participants: VoteParticipantModel<serialized>[],
  
  personalisedInvitation?:string,
};

export function deserializeVoteModel(request: VoteModel<boolean>): VoteModel {
  return {
    ...request,
    voteConfig: {
      ...request.voteConfig,
      decisionType: deserializeDecisionType(request.voteConfig.decisionType)
    },
    options: request.options.map(deserializeVoteOptionModel),
    participants: request.participants.map(participant => ({
      ...participant,
      decisions: participant.decisions.map(deserializeVoteOptionDecisionModel),
    })),
  };
}

export type VoteNotificationSettings = {
  recipientEmail: string,
  sendLinkMail: boolean,
  notifyParticipation: boolean,
  notifyExpiration: boolean,
};

export type VoteConfig<serialized extends boolean = false> = {
  id?: number,
  voteOptionConfig: VoteOptionConfig,
  decisionType: serialized extends false ? DecisionType : SerializedDecisionType,
  yesLimitActive?:boolean,
  yesAnswerLimit?:number|null,
};
