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

export type Question = OpenQuestion | ChoiceQuestion;

export type OpenQuestion = {
  type: 'OPEN',
  id?: string|number,
  rank: string|number,
  text: string,
  required: boolean,
  minSelect?: number,
  maxSelect?: number,
  choices?: Checkbox[],
};

export type ChoiceQuestion = {
  type: 'CHOICE',
  id?: string|number,
  rank: string|number,
  text: string,
  required?: boolean,
  minSelect: number,
  maxSelect: number,
  choices: Checkbox[],
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
  answers: {
    [questionGroupId: string|number]: {
      [questionRank: string|number]: Answer | undefined,
    } | undefined,
  },
}