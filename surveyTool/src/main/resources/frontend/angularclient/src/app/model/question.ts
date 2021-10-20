import {QuestionGroup} from "./question-group";
import {CheckboxGroup} from "./checkbox-group";

export class Question {
  id: number | undefined;
  text: string | undefined;
  required: boolean | undefined;
  hasCheckbox: boolean | undefined;
  questionGroup: QuestionGroup | undefined;
  checkboxGroup: CheckboxGroup | undefined;
}
