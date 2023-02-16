import { AuthEffects } from "./authentication/auth.effects";
import { AppointmentRequestEffects } from "./appointment/appointment.effects";

export const appEffects = [AuthEffects, AppointmentRequestEffects];
