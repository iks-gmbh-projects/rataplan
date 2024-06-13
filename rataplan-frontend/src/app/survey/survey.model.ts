export type SurveyHead = {
  id?: string | number,
  name: string,
  description: string,
  startDate: Date,
  endDate: Date,
  timezone: string | undefined,
  openAccess: boolean,
  anonymousParticipation: boolean,
  accessId?: string,
  participationId?: string,
  userId?: string | number,
  timezoneActive?: boolean
};

export type Survey = SurveyHead & {
  questionGroups: QuestionGroup[]
};

export type QuestionGroup = {
  id?: string | number,
  title: string,
  questions: Question[]
};

export type Question = OpenQuestion | ChoiceQuestion | OrderQuestion;

type BaseQuestion = {
  type: string,
  id?: string | number,
  rank: string|number,
  text: string,
};

export type OpenQuestion = BaseQuestion & {
  type: 'OPEN',
  required: boolean,
  minSelect?: undefined,
  maxSelect?: undefined,
  choices?: undefined,
};

export type ChoiceQuestion = BaseQuestion & {
  type: 'CHOICE',
  required?: undefined,
  minSelect: number,
  maxSelect: number,
  choices: Checkbox[],
};

export type Checkbox = {
  id?: string | number,
  text: string,
  hasTextField: boolean,
};

export type OrderQuestion = BaseQuestion & {
  type: 'ORDER',
  required?: undefined,
  minSelect?: undefined,
  maxSelect?: undefined,
  choices: OrderChoice[],
};

export type OrderChoice = {
  id?: string|number,
  text: string,
}

export type Answer = {
  id?: string | number,
  text?: string,
  checkboxes?: {[checkboxId: string | number]: boolean},
  order?: (string|number)[],
};

export type SurveyResponse = {
  id?: string | number,
  surveyId: string | number,
  userId?: string | number,
  answers: Partial<Record<string|number, Partial<Record<string | number, Answer>>>>,
}