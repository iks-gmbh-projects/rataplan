import { ClipboardModule } from '@angular/cdk/clipboard';
import { CommonModule } from '@angular/common';
import { NgModule } from '@angular/core';
import { FormsModule, ReactiveFormsModule } from '@angular/forms';
import { MatBadgeModule } from '@angular/material/badge';
import { MatLegacyButtonModule as MatButtonModule } from '@angular/material/legacy-button';
import { MatButtonToggleModule } from '@angular/material/button-toggle';
import { MatLegacyCardModule as MatCardModule } from '@angular/material/legacy-card';
import { MatCheckboxModule } from '@angular/material/checkbox';
import { MatLegacyChipsModule as MatChipsModule } from '@angular/material/legacy-chips';
import { MatDatepickerModule } from '@angular/material/datepicker';
import { MatDividerModule } from '@angular/material/divider';
import { MatExpansionModule } from '@angular/material/expansion';
import { MatIconModule } from '@angular/material/icon';
import { MatInputModule } from '@angular/material/input';
import { MatListModule } from '@angular/material/list';
import { MatMenuModule } from '@angular/material/menu';
import { MatProgressSpinnerModule } from '@angular/material/progress-spinner';
import { MatRadioModule } from '@angular/material/radio';
import { MatSelectModule } from '@angular/material/select';
import { MatLegacySlideToggleModule as MatSlideToggleModule } from '@angular/material/legacy-slide-toggle';
import { MatSortModule } from '@angular/material/sort';
import { MatStepperModule } from '@angular/material/stepper';
import { MatLegacyTableModule as MatTableModule } from '@angular/material/legacy-table';
import { MatLegacyTabsModule as MatTabsModule } from '@angular/material/legacy-tabs';
import { MatLegacyTooltipModule as MatTooltipModule } from '@angular/material/legacy-tooltip';
import { MtxButtonModule } from '@ng-matero/extensions/button';
import { MtxDatetimepickerModule } from '@ng-matero/extensions/datetimepicker';
import { ConfirmDialogComponent } from './dialogs/confirm-dialog/confirm-dialog.component';
import { DisplayNamePipe } from './pipes/display-name.pipe';
import { CheckboxCountMaxDirective } from './validator/checkbox-count-max.directive';
import { CheckboxCountMinDirective } from './validator/checkbox-count-min.directive';
import { IntegerDirective } from './validator/integer.directive';
import { NoWhitespaceDirective } from './validator/no-whitespace.directive';
import { SomeNonWhitespaceDirective } from './validator/some-non-whitespace.directive';

const declarations = [
  DisplayNamePipe,
  NoWhitespaceDirective,
  SomeNonWhitespaceDirective,
  CheckboxCountMinDirective,
  CheckboxCountMaxDirective,
  IntegerDirective,
  ConfirmDialogComponent,
];

const imports = [
  CommonModule,
  FormsModule,
  ReactiveFormsModule,
  ClipboardModule,
  MatMenuModule,
  MatSelectModule,
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
  MatSlideToggleModule,
  MatDividerModule,
  MatChipsModule,
  MatListModule,
  MatBadgeModule,
  MatStepperModule,
  MatTabsModule,
  MtxDatetimepickerModule,
  MtxButtonModule,
];

@NgModule({
  declarations: declarations,
  imports: imports,
  exports: [
    ...imports,
    ...declarations,
  ],
})
export class AppCommonModule {}