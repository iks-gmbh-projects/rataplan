import {Question} from "./question";

export class CheckboxGroup {
  id: number | undefined;
  multipleSelect: boolean | undefined;
  minSelect: number | undefined;
  maxSelect: number | undefined;
  question: Question | undefined;
  checkboxes: Object | undefined;
}
