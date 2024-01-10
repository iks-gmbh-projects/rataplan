export type VoteOptionModel<serialized extends boolean = false> = {
  id?: number,
  requestId?: number,
  description?: string,
  startDate?: serialized extends false ? string : (string|number),
  endDate?: serialized extends false ? string : (string|number),
  url?: string,
  participantLimitActive?:boolean,
  participantLimit?:number | null,
};

export function deserializeVoteOptionModel(voteOption: VoteOptionModel<boolean>): VoteOptionModel {
  if(voteOption.startDate) voteOption.startDate = new Date(voteOption.startDate).toISOString();
  if(voteOption.endDate) voteOption.endDate = new Date(voteOption.endDate).toISOString();
  return voteOption as VoteOptionModel;
}

export type VoteOptionConfig = {
  startDate?: boolean,
  startTime?: boolean,
  endDate?: boolean,
  endTime?: boolean,
  url?: boolean,
  description?: boolean,
}

export function isConfiguredEqual(a: VoteOptionConfig, b: VoteOptionConfig): boolean {
  if(a == b) return true;
  if("object" !== typeof a) return false;
  if("object" !== typeof b) return false;
  return !a.startDate == !b.startDate &&
    !a.startTime == !b.startTime &&
    !a.endDate == !b.endDate &&
    !a.endTime == !b.endTime &&
    !a.description == !b.description &&
    !a.url == !b.url;
}

export function matchesConfiguration(a: VoteOptionModel, config: VoteOptionConfig): boolean {
  return ((config.startDate || config.startTime) == !!a.startDate) &&
    ((config.endDate || config.endTime) == !!a.endDate) &&
    (config.description == !!a.description) &&
    (config.url == !!a.url);
}

export function matchConfiguration(a: VoteOptionModel[], config: VoteOptionConfig): boolean {
  return a.every(a => matchesConfiguration(a, config));
}
