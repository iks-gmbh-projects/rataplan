import { NgModule } from '@angular/core';
import { BrowserModule } from '@angular/platform-browser';

import { AppRoutingModule } from './app-routing.module';
import { AppComponent } from './app.component';
import { HeaderComponent } from './header/header.component';
import { BrowserAnimationsModule } from '@angular/platform-browser/animations';
import { MatButtonModule } from '@angular/material/button';
import { MatToolbarModule } from '@angular/material/toolbar';
import { ConditionsComponent } from './legals/conditions/conditions.component';
import { HomepageComponent } from './homepage/homepage.component';
import { ImprintComponent } from './legals/imprint/imprint.component';

@NgModule({
  declarations: [
    AppComponent,
    HeaderComponent,
    ConditionsComponent,
    HomepageComponent,
    ImprintComponent,
  ],
  imports: [
    BrowserModule,
    AppRoutingModule,
    BrowserAnimationsModule,
    MatButtonModule,
    MatToolbarModule
  ],
  providers: [],
  bootstrap: [AppComponent]
})
export class AppModule { }
