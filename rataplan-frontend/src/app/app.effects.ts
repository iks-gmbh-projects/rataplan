import { AuthEffects } from "./authentication/auth.effects";
import { ConfigEffects } from './config/config.effects';
import { ContactsEffects } from './contact-list/contacts.effects';
import { CookieEffects } from './cookie-banner/cookie.effects';
import { EmailNotificationSettingsEffect } from './email-notification-settings/state/email-notification-settings.effect';
import { NotificationEffects } from './notification/notification.effects';
import { VoteListEffects } from './vote-list/state/vote-list.effects';

export const appEffects = [ConfigEffects, CookieEffects, AuthEffects, EmailNotificationSettingsEffect, VoteListEffects, NotificationEffects, ContactsEffects];