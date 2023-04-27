import { NgModule } from '@angular/core';
import { CommonModule } from '@angular/common';
import { DisplayNamePipe } from './pipes/display-name.pipe';
import { HttpClientModule } from '@angular/common/http';
import { NoWhitespaceDirective } from "./validator/no-whitespace.directive";
import { SomeNonWhitespaceDirective } from "./validator/some-non-whitespace.directive";
import { CheckboxCountMinDirective } from "./validator/checkbox-count-min.directive";
import { CheckboxCountMaxDirective } from "./validator/checkbox-count-max.directive";
import { MatTooltipModule } from "@angular/material/tooltip";
import { CombinedDatetimeMaxDirective } from './validator/combined-datetime-max.directive';
import { CombinedDatetimeMinDirective } from './validator/combined-datetime-min.directive';



@NgModule({
  declarations: [
    DisplayNamePipe,
    NoWhitespaceDirective,
    SomeNonWhitespaceDirective,
    CheckboxCountMinDirective,
    CheckboxCountMaxDirective,
    CombinedDatetimeMaxDirective,
    CombinedDatetimeMinDirective,
  ],
  imports: [
    CommonModule,
    HttpClientModule,
    MatTooltipModule,
  ],
  exports: [
    CommonModule,
    HttpClientModule,
    MatTooltipModule,
    DisplayNamePipe,
    SomeNonWhitespaceDirective,
    CheckboxCountMinDirective,
    CheckboxCountMaxDirective,
    CombinedDatetimeMaxDirective,
    CombinedDatetimeMinDirective,
  ],
})
export class AppCommonModule { }
