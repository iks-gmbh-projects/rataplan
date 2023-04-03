import { Action } from "@ngrx/store";
import { FrontendUser, LoginData, RegisterData } from "../models/user.model";
import { UrlTree } from "@angular/router";
import { deletionChoices } from "../models/delete-profile.model";
import { ResetPasswordDataModel } from "../models/reset-password-data.model";

export const AuthActions = {
  REGISTER_ACTION: "[Auth] Register",
  REGISTER_SUCCESS_ACTION: "[Auth] Register Success",
  REGISTER_ERROR_ACTION: "[Auth] Register Error",
  AUTO_LOGIN_ACTION: "[Auth] Auto Login",
  LOGIN_ACTION: "[Auth] Login",
  LOGIN_SUCCESS_ACTION: "[Auth] Login Success",
  LOGIN_ERROR_ACTION: "[Auth] Login Error",
  RESET_PASSWORD_ACTION: "[Auth] Reset Password",
  RESET_PASSWORD_SUCCESS_ACTION: "[Auth] Reset Password Success",
  RESET_PASSWORD_ERROR_ACTION: "[Auth] Reset Password Error",
  CHANGE_EMAIL_ACTION: "[Auth] Change Email",
  CHANGE_EMAIL_ERROR_ACTION: "[Auth] Change Email Error",
  CHANGE_PROFILE_DETAILS_ACTION:"[Auth] Change Profile Details",
  CHANGE_DISPLAYNAME_ACTION: "[Auth] Change Displayname",
  CHANGE_PROFILE_DETAILS_SUCCESS_ACTION:"[Auth] Change Profile Details Success",
  CHANGE_DISPLAYNAME_ERROR_ACTION: "[Auth] Change Displayname Error",
  UPDATE_USERDATA_ACTION: "[Auth] Update Userdata",
  UPDATE_USERDATA_SUCCESS_ACTION: "[Auth] Update Userdata Success",
  LOGOUT_ACTION: "[Auth] Logout",
  DELETE_USER_ACTION: "[Auth] Delete User",
  DELETE_USER_SUCCESS_ACTION: "[Auth] Delete User Success",
  DELETE_USER_ERROR_ACTION: "[Auth] Delete User Error",
} as const;


export class ChangeProfileDetailsAction implements Action {

  readonly type = AuthActions.CHANGE_PROFILE_DETAILS_ACTION;

  constructor(readonly payload:FrontendUser) {
  }

}
export class ChangeProfileDetailsSuccessAction implements Action {

  readonly type = AuthActions.CHANGE_PROFILE_DETAILS_SUCCESS_ACTION;

  constructor(readonly payload:FrontendUser) {
  }

}

export class RegisterAction implements Action {
  readonly type = AuthActions.REGISTER_ACTION;

  constructor(
    readonly payload: RegisterData,
    readonly redirect?: UrlTree | string
  ) {
  }
}

export class RegisterSuccessAction implements Action {
  readonly type = AuthActions.REGISTER_SUCCESS_ACTION;

  constructor(
    readonly payload: FrontendUser
  ) {
  }
}

export class RegisterErrorAction implements Action {
  readonly type = AuthActions.REGISTER_ERROR_ACTION;
  constructor(
    readonly error: any
  ) {
  }
}

export class AutoLoginAction implements Action {
  readonly type = AuthActions.AUTO_LOGIN_ACTION;
}

export class LoginAction implements Action {
  readonly type = AuthActions.LOGIN_ACTION;

  constructor(
    readonly payload: LoginData,
    readonly redirect?: UrlTree | string
  ) {
  }
}

export class LoginSuccessAction implements Action {
  readonly type = AuthActions.LOGIN_SUCCESS_ACTION;

  constructor(
    readonly payload: FrontendUser
  ) {
  }
}

export class LoginErrorAction implements Action {
  readonly type = AuthActions.LOGIN_ERROR_ACTION;
  constructor(
    readonly error: any
  ) {
  }
}

export class ResetPasswordAction implements Action {
  readonly type = AuthActions.RESET_PASSWORD_ACTION;

  constructor(
    readonly payload: ResetPasswordDataModel
  ) {
  }
}

export class ResetPasswordSuccessAction implements Action {
  readonly type = AuthActions.RESET_PASSWORD_SUCCESS_ACTION;

  constructor() {
  }
}

export class ResetPasswordErrorAction implements Action {
  readonly type = AuthActions.RESET_PASSWORD_ERROR_ACTION;
  constructor(
    readonly error: any
  ) {
  }
}

export class ChangeEmailAction implements Action {
  readonly type = AuthActions.CHANGE_EMAIL_ACTION;
  constructor(
    readonly email: string
  ) {
  }
}

export class ChangeEmailErrorAction implements Action {
  readonly type = AuthActions.CHANGE_EMAIL_ERROR_ACTION;
  constructor(
    readonly error: any
  ) {
  }
}

export class ChangeDisplaynameAction implements Action {
  readonly type = AuthActions.CHANGE_DISPLAYNAME_ACTION;
  constructor(
    readonly displayname: string
  ) {
  }
}

export class ChangeDisplaynameErrorAction implements Action {
  readonly type = AuthActions.CHANGE_DISPLAYNAME_ERROR_ACTION;
  constructor(
    readonly error: any
  ) {
  }
}

export class UpdateUserdataAction implements Action {
  readonly type = AuthActions.UPDATE_USERDATA_ACTION;
}

export class UpdateUserdataSuccessAction implements Action {
  readonly type = AuthActions.UPDATE_USERDATA_SUCCESS_ACTION;

  constructor(
    readonly payload: FrontendUser
  ) {
  }
}

export class LogoutAction implements Action {
  readonly type = AuthActions.LOGOUT_ACTION;
}

export class DeleteUserAction implements Action {
  readonly type = AuthActions.DELETE_USER_ACTION;
  constructor(
    readonly payload: deletionChoices
  ) {
  }
}

export class DeleteUserSuccessAction implements Action {
  readonly type = AuthActions.DELETE_USER_SUCCESS_ACTION;
}

export class DeleteUserErrorAction implements Action {
  readonly type = AuthActions.DELETE_USER_ERROR_ACTION;
  constructor(
    readonly error: any
  ) {
  }
}

export type AuthActions =
  RegisterAction
  | RegisterSuccessAction
  | RegisterErrorAction
  | AutoLoginAction
  | LoginAction
  | LoginSuccessAction
  | LoginErrorAction
  | ResetPasswordAction
  | ResetPasswordSuccessAction
  | ResetPasswordErrorAction
  | ChangeEmailAction
  | ChangeEmailErrorAction
  | ChangeDisplaynameAction
  | ChangeDisplaynameErrorAction
  | UpdateUserdataAction
  | UpdateUserdataSuccessAction
  | LogoutAction
  | DeleteUserAction
  | DeleteUserSuccessAction
  | DeleteUserErrorAction
  |ChangeProfileDetailsAction
  |ChangeProfileDetailsSuccessAction;
