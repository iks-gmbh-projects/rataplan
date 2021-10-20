import {QuestionGroup} from "./question-group";

export class Survey {
  id: number | undefined;
  name: string | undefined;
  description: string | undefined;
  startDate: string | undefined;
  endDate: string | undefined;
  open: boolean | undefined;
  accessID: string | undefined;
  uuid: string | undefined;
  user: Object | undefined;
  questionGroups: QuestionGroup[] | undefined;
}
