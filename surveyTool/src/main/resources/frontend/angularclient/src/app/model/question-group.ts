import {Survey} from "./survey";
import {Question} from "./question";

export class QuestionGroup {
  id: number | undefined;
  title: string | undefined;
  survey: Survey | undefined;
  questions: Question[] | undefined;
}
