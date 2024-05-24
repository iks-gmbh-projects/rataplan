export class User {

  constructor(public email: string,
              public id: string,
              private _token: string,
              private _tokenExpirationDate: Date) {
  }

  get token() {
    if (!this._tokenExpirationDate || new Date() > this._tokenExpirationDate) {
      return null;
    }
    return this._token;
  }

}

export type LoginData = {
  username: string,
  password: string,
};
export type FrontendUser = {
  username: string,
  id: number,
  mail: string,
  displayname: string,
};
export type RegisterData = {
  username: string,
  mail: string,
  displayname: string,
  password: string,
};