import { NgModule } from '@angular/core';
import { CommonModule } from '@angular/common';
import { DisplayNamePipe } from './pipes/display-name.pipe';
import { NoWhitespaceDirective } from "./validator/no-whitespace.directive";
import { SomeNonWhitespaceDirective } from "./validator/some-non-whitespace.directive";
import { CheckboxCountMinDirective } from "./validator/checkbox-count-min.directive";
import { CheckboxCountMaxDirective } from "./validator/checkbox-count-max.directive";
import { MatTooltipModule } from "@angular/material/tooltip";
import { IntegerDirective } from './validator/integer.directive';
import { MatButtonModule } from '@angular/material/button';
import { MatInputModule } from '@angular/material/input';
import { MatDatepickerModule } from '@angular/material/datepicker';
import { MatCardModule } from '@angular/material/card';
import { MatIconModule } from '@angular/material/icon';
import { MatProgressSpinnerModule } from '@angular/material/progress-spinner';
import { MatTableModule } from '@angular/material/table';
import { MatExpansionModule } from '@angular/material/expansion';
import { MatSortModule } from '@angular/material/sort';
import { MatCheckboxModule } from '@angular/material/checkbox';
import { MatRadioModule } from '@angular/material/radio';
import { MatDividerModule } from '@angular/material/divider';
import { MatStepperModule } from '@angular/material/stepper';
import { FormsModule, ReactiveFormsModule } from '@angular/forms';
import { MatChipsModule } from '@angular/material/chips';
import { NgxMaterialTimepickerModule } from 'ngx-material-timepicker';
import { MatButtonToggleModule } from '@angular/material/button-toggle';
import { MatListModule } from '@angular/material/list';
import { MatBadgeModule } from '@angular/material/badge';
import { MatTabsModule } from '@angular/material/tabs';
import { ClipboardModule } from '@angular/cdk/clipboard';

const declarations = [
  DisplayNamePipe,
  NoWhitespaceDirective,
  SomeNonWhitespaceDirective,
  CheckboxCountMinDirective,
  CheckboxCountMaxDirective,
  IntegerDirective,
];

const imports = [
  CommonModule,
  FormsModule,
  ReactiveFormsModule,
  ClipboardModule,
  MatTooltipModule,
  MatIconModule,
  MatButtonModule,
  MatButtonToggleModule,
  MatInputModule,
  MatDatepickerModule,
  MatCardModule,
  MatIconModule,
  MatProgressSpinnerModule,
  MatTableModule,
  MatExpansionModule,
  MatSortModule,
  MatCheckboxModule,
  MatRadioModule,
  MatDividerModule,
  MatChipsModule,
  MatListModule,
  MatBadgeModule,
  MatStepperModule,
  MatTabsModule,
  NgxMaterialTimepickerModule,
  ClipboardModule,
];

@NgModule({
  declarations: declarations,
  imports: imports,
  exports: [
    ...imports,
    ...declarations,
  ],
})
export class AppCommonModule { }
