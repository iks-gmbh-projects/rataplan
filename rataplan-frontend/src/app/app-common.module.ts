import { NgModule } from '@angular/core';
import { CommonModule } from '@angular/common';
import { DisplayNamePipe } from './pipes/display-name.pipe';
import { HttpClientModule } from '@angular/common/http';
import { NoWhitespaceDirective } from "./validator/no-whitespace.directive";
import { SomeNonWhitespaceDirective } from "./validator/some-non-whitespace.directive";



@NgModule({
  declarations: [
    DisplayNamePipe,
    NoWhitespaceDirective,
    SomeNonWhitespaceDirective,
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
  ],
})
export class AppCommonModule { }
