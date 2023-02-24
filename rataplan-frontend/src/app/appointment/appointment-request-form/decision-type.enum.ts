export enum DecisionType {
  DEFAULT,
  EXTENDED,
  NUMBER,
}

export type SerializedDecisionType = DecisionType | keyof typeof DecisionType;

export function deserializeDecisionType(decisionType: SerializedDecisionType): DecisionType {
  if(typeof decisionType === "number" || /^\d+$/.test(decisionType)) {
    return +decisionType;
  } else {
    return DecisionType[decisionType];
  }
}

export enum AppointmentDecisionType {
  NO_ANSWER,
  ACCEPT,
  ACCEPT_IF_NECESSARY,
  DECLINE
}

export type SerializedAppointmentDecisionType = AppointmentDecisionType | keyof typeof AppointmentDecisionType;

export function deserializeAppointmentDecisionType(decisionType: SerializedAppointmentDecisionType): AppointmentDecisionType {
  if(typeof decisionType === "number" || /^\d+$/.test(decisionType)) {
    return +decisionType;
  } else {
    return AppointmentDecisionType[decisionType];
  }
}
