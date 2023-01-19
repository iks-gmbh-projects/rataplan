import {Injectable} from "@angular/core";
import {FrontendUser} from "../login.service/user.model";

@Injectable({
  providedIn:"root"
})


export class LocalstorageService {
  username: string = 'username';
  id: string = 'id';
  mail: string = 'mail'
  displayname: string = 'displayname'

  constructor() {
  }



   setLocalStorage(frontendUser: FrontendUser) {
    localStorage.setItem(this.username,JSON.stringify(frontendUser.username));
    localStorage.setItem(this.id, JSON.stringify(frontendUser.id));
    localStorage.setItem(this.mail, JSON.stringify(frontendUser.mail));
    localStorage.setItem(this.displayname, JSON.stringify(frontendUser.displayname));
  }


  public isLoggedIn(){
    return localStorage.getItem(this.username) && localStorage.getItem(this.id);
  }

}
