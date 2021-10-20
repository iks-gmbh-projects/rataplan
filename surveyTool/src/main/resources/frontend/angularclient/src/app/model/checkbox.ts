import {CheckboxGroup} from "./checkbox-group";

export class Checkbox {
  id: number | undefined;
  text: string | undefined;
  hasTextField: boolean | undefined;
  checkboxGroup: CheckboxGroup | undefined;
  answers: Object[] | undefined
}
