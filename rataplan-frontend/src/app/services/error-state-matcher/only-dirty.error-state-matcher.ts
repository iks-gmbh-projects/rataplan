import { ErrorStateMatcher } from "@angular/material/core";
import { AbstractControl, FormGroupDirective, NgForm } from "@angular/forms";
import { Injectable } from "@angular/core";


@Injectable({
  providedIn: "root",
})
export class OnlyDirtyErrorStateMatcher implements ErrorStateMatcher {
  isErrorState(control: AbstractControl | null, form: FormGroupDirective | NgForm | null): boolean {
    return !!(control?.invalid && control?.dirty);
  }

}
