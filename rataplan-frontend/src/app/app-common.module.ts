import { NgModule } from '@angular/core';
import { CommonModule } from '@angular/common';
import { DisplayNamePipe } from './pipes/display-name.pipe';
import { HttpClientModule } from '@angular/common/http';



@NgModule({
  declarations: [
    DisplayNamePipe,
  ],
  imports: [
    CommonModule,
    HttpClientModule,
  ],
  exports: [
    DisplayNamePipe,
    CommonModule,
    HttpClientModule,
  ],
})
export class AppCommonModule { }
