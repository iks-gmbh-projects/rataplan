import {
  DecisionType,
  deserializeDecisionType,
  SerializedDecisionType,
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
  userConsignees: (string | number)[],
  
  backendUserid?: number,
  expired?: boolean,
  participationToken?: string,
  editToken?: string,
  
  yesAnswerLimit?: number,
  options: VoteOptionModel<serialized>[],
  participants: VoteParticipantModel<serialized>[],
  
  endTime?: boolean,
  startTime?: boolean,
  decisionType: serialized extends false ? DecisionType : SerializedDecisionType,
  voteOptionConfig?: VoteOptionConfig
  
  personalisedInvitation?: string,
};

export function deserializeVoteModel(request: VoteModel<boolean>): VoteModel {
  return {
    ...request,
    decisionType: deserializeDecisionType(request.decisionType!),
    voteOptionConfig: ascertainVoteConfig(request),
    yesAnswerLimit: request.yesAnswerLimit,
    options: request.options.map(deserializeVoteOptionModel),
    participants: request.participants.map(participant => ({
      ...participant,
      decisions: participant.decisions.map(deserializeVoteOptionDecisionModel),
    })),
  };
}

export function ascertainVoteConfig(request: VoteModel): VoteOptionConfig {
  const config = {
    startDate: false,
    startTime: request.startTime,
    endDate: false,
    endTime: request.endTime,
    url: false,
    description: false,
  };
  for(const vo of request.options) {
    if(vo.startDate) config.startDate = true;
    if(vo.endDate) config.endDate = true;
    if(vo.url) config.url = true;
    if(vo.description) config.description = true;
  }
  return config;
}

export type VoteNotificationSettings = {
  recipientEmail: string,
  sendLinkMail: boolean,
  notifyParticipation: boolean,
  notifyExpiration: boolean,
};
