import { NgModule } from '@angular/core';
import { CommonModule } from '@angular/common';
import { DisplayNamePipe } from './pipes/display-name.pipe';
import { HttpClientModule } from '@angular/common/http';
import { NoWhitespaceDirective } from "./validator/no-whitespace.directive";
import { SomeNonWhitespaceDirective } from "./validator/some-non-whitespace.directive";
import { CheckboxCountMinDirective } from "./validator/checkbox-count-min.directive";
import { CheckboxCountMaxDirective } from "./validator/checkbox-count-max.directive";



@NgModule({
  declarations: [
    DisplayNamePipe,
    NoWhitespaceDirective,
    SomeNonWhitespaceDirective,
    CheckboxCountMinDirective,
    CheckboxCountMaxDirective,
  ],
  imports: [
    CommonModule,
    HttpClientModule,
  ],
  exports: [
    DisplayNamePipe,
    CommonModule,
    HttpClientModule,
    SomeNonWhitespaceDirective,
    CheckboxCountMinDirective,
    CheckboxCountMaxDirective,
  ],
})
export class AppCommonModule { }
