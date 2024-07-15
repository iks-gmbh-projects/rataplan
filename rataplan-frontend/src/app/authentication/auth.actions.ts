import { UrlTree } from '@angular/router';
import { createActionGroup, emptyProps, props } from '@ngrx/store';

import { DeletionChoices } from '../models/delete-profile.model';
import { FrontendUser, LoginData, RegisterData } from '../models/user.model';

const userProps = props<{user: FrontendUser}>();

export const authActions = createActionGroup({
  source: 'Authentication',
  events: {
    'error': (error: any) => ({error}),
    'register': props<{user: RegisterData, redirect?: UrlTree}>(),
    'register success': userProps,
    'auto login': emptyProps(),
    'login': props<{user: LoginData, redirect?: UrlTree}>(),
    'login success': props<{jwt: string}>(),
    'reset password': props<{password: string, token: string}>(),
    'reset password success': emptyProps(),
    'change profile details': userProps,
    'change profile details success': userProps,
    'update user data': emptyProps(),
    'update user data success': userProps,
    'logout': emptyProps(),
    'delete user': props<{choices: DeletionChoices}>(),
    'delete user success': emptyProps(),
  },
});