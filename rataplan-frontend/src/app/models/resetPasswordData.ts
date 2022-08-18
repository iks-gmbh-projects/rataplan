export class ResetPasswordData {

  private token: string;
  private password: string;

  constructor(token: string, password: string) {
    this.token = token;
    this.password = password;
  }
}
