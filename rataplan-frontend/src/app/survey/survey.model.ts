export type Survey = {
  id: string,
  name: string,
  description: string,
  startDate: Date,
  endDate: Date,
  openAccess: boolean,
  anonymousParticipation: boolean,
  accessId: string,
  participationId: string,
  userId: number,
  userName: string,
  questionGroups?: QuestionGroup[]
};

export type QuestionGroup = {
  id: string,
  title: string,
  questions: Question[]
};

export type Question = {
  id: string,
  text: string,
  required: boolean,
  checkboxGroup?: CheckboxGroup
};

export type CheckboxGroup = {
  id: string,
  multipleSelect: boolean,
  minSelect: number,
  maxSelect: number,
  checkboxes: Checkbox[]
};

export type Checkbox = {
  id: string,
  text: string,
  hasTextField: boolean
};

export type Answer = {
  id: string,
  text: string,
  userId: number,
  userName: string,
  checkboxId?: string,
  questionId: string
};