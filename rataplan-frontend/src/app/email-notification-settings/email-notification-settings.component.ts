import { Component, OnDestroy, OnInit } from '@angular/core';
import { AbstractControl, FormControl, FormGroup } from '@angular/forms';
import { Store } from '@ngrx/store';
import { Observable, Subscription } from 'rxjs';
import { filter, map } from 'rxjs/operators';
import { NotificationCategoryTypeService } from './NotificationCategoryType.service';
import { emailNotificationSettingsActions } from './state/email-notification-settings.actions';
import { emailNotificationSettingsFeature } from './state/email-notification-settings.feature';
import { EmailCycle } from './state/email-notification-settings.model';

@Component({
  selector: 'app-email-notification-settings',
  templateUrl: './email-notification-settings.component.html',
  styleUrls: ['./email-notification-settings.component.css'],
})
export class EmailNotificationSettingsComponent implements OnInit, OnDestroy {
  private readonly categorySettings = new FormGroup<Record<string, AbstractControl<EmailCycle | undefined>>>({});
  private readonly typeSettings = new FormGroup<Record<string, AbstractControl<EmailCycle | undefined>>>({});
  private readonly defaultCycle = new FormControl<EmailCycle>(EmailCycle.INSTANT);
  readonly form = new FormGroup({
    defaultCycle: this.defaultCycle,
    categorySettings: this.categorySettings,
    typeSettings: this.typeSettings,
  });
  readonly busy$: Observable<boolean>;
  readonly notificationCategoryTranslations: Record<string, string> = {
    account: 'Profil',
    misc: 'Sonstiges',
    survey: 'Umfragen',
    vote: 'Abstimmungen',
  } as const;
  readonly notificationTypeTranslations: Record<string, string> = {
    'vote/invite': 'Einladung erhalten',
    'vote/participation': 'Teilnehmer hat abgestimmt',
    'vote/participation-invalidation': 'Teilnahme wurde ungültig',
  } as const;
  
  private subCat?: Subscription;
  private sub?: Subscription;
  private subDefault?: Subscription;
  
  constructor(
    private readonly store: Store,
    readonly notificationTypeService: NotificationCategoryTypeService,
  )
  {
    this.busy$ = store.select(emailNotificationSettingsFeature.selectBusy);
  }
  
  public ngOnInit(): void {
    this.subCat = this.notificationTypeService.categoryTypes$.subscribe(catTypes => {
      for(const cat in catTypes) {
        const catCtrl = new FormControl<EmailCycle | null>(null);
        this.categorySettings.addControl(cat, catCtrl);
        for(const type of catTypes[cat]) {
          const typeCtrl = new FormControl<EmailCycle | null>(null);
          this.typeSettings.addControl(type, typeCtrl);
        }
      }
    });
    this.subDefault = this.defaultCycle.valueChanges.pipe(
      filter(v => v !== null),
    ).subscribe(v => this.store.dispatch(emailNotificationSettingsActions.setDefaultSetting({
      cycle: v!,
    })));
    this.sub = this.store.select(emailNotificationSettingsFeature.selectEmailNotificationSettingsState)
      .pipe(
        filter(({busy, settings}) => !busy && settings !== undefined),
        map(({settings}) => settings!),
      ).subscribe(settings => {
        this.form.patchValue(settings);
      });
  }
  
  public ngOnDestroy(): void {
    this.sub?.unsubscribe();
    this.subCat?.unsubscribe();
    this.subDefault?.unsubscribe();
  }
  
  updateSettings(): void {
    this.store.dispatch(emailNotificationSettingsActions.saveSettings());
  }
  
  protected readonly EmailCycle = EmailCycle;
}
