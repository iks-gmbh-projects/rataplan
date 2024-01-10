export type SurveyHead = {
  id?: string|number,
  name: string,
  description: string,
  startDate: Date,
  endDate: Date,
  openAccess: boolean,
  anonymousParticipation: boolean,
  accessId?: string,
  participationId?: string,
  userId?: string|number,
};

export type Survey = SurveyHead & {
  questionGroups: QuestionGroup[]
};

export type QuestionGroup = {
  id?: string|number,
  title: string,
  questions: Question[]
};

export type Question = {
  id?: string|number,
  text: string,
  required: boolean,
  hasCheckbox?: boolean,
  checkboxGroup?: CheckboxGroup
};

export type CheckboxGroup = {
  id?: string|number,
  multipleSelect: boolean,
  minSelect: number,
  maxSelect: number,
  checkboxes: Checkbox[]
};

export type Checkbox = {
  id?: string|number,
  text: string,
  hasTextField: boolean
};

export type Answer = {
  id?: string|number,
  text?: string,
  checkboxes?: {[checkboxId: string|number]: boolean},
};

export type SurveyResponse = {
  id?: string|number,
  surveyId: string|number,
  userId?: string|number,
  answers: {[questionId: string|number]: Answer},
}