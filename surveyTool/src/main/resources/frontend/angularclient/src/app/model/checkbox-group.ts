import {Question} from "./question";
import {Checkbox} from "./checkbox";

export class CheckboxGroup {
  id: number | undefined;
  multipleSelect: boolean | undefined;
  minSelect: number | undefined;
  maxSelect: number | undefined;
  question: Question | undefined;
  checkboxes: Checkbox[] | undefined;
}
