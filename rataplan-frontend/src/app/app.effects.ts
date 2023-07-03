import { AuthEffects } from "./authentication/auth.effects";
import { CookieEffects } from './cookie-banner/cookie.effects';
import { NotificationEffects } from './notification/notification.effects';

export const appEffects = [CookieEffects, AuthEffects, NotificationEffects];
