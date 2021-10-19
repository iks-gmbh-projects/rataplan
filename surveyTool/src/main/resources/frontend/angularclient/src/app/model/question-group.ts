import {Survey} from "./survey";

export class QuestionGroup {
  id: number | undefined;
  title: string | undefined;
  survey: Survey | undefined;
  questions: object | undefined;
}
