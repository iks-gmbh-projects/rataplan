import { AuthEffects } from "./authentication/auth.effects";
import { VoteEffects } from "./vote/vote.effects";

export const appEffects = [AuthEffects, VoteEffects];
