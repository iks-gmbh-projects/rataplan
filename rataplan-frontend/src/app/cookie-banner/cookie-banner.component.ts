import { Component } from '@angular/core';
import { MatBottomSheetRef } from '@angular/material/bottom-sheet';
import { Store } from '@ngrx/store';
import { AcceptCookieAction } from './cookie.actions';

@Component({
  selector: 'app-cookie-banner',
  templateUrl: './cookie-banner.component.html',
  styleUrls: ['./cookie-banner.component.css']
})
export class CookieBannerComponent {

  constructor(
    readonly store: Store,
    readonly ref: MatBottomSheetRef<CookieBannerComponent>,
  ) { }

  accept(): void {
    this.store.next(new AcceptCookieAction());
    this.ref.dismiss();
  }

  reject(): void {
    this.ref.dismiss();
  }
}
