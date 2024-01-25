import { AuthEffects } from "./authentication/auth.effects";
import { ContactsEffects } from './contact-list/contacts.effects';
import { CookieEffects } from './cookie-banner/cookie.effects';
import { EmailNotificationSettingsEffect } from './email-notification-settings/state/email-notification-settings.effect';
import { NotificationEffects } from './notification/notification.effects';

export const appEffects = [CookieEffects, AuthEffects, EmailNotificationSettingsEffect, NotificationEffects, ContactsEffects];
