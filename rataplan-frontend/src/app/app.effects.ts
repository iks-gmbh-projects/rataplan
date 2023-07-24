import { AuthEffects } from "./authentication/auth.effects";
import { VoteEffects } from "./vote/vote.effects";
import { CookieEffects } from './cookie-banner/cookie.effects';

export const appEffects = [CookieEffects, AuthEffects, VoteEffects];
