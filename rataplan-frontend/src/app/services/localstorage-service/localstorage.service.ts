import {Injectable} from "@angular/core";
import {FrontendUser} from "../../login/login.component";

@Injectable({
  providedIn:"root"
})


export class LocalstorageService {
  username: string = 'username';
  id: string = 'id';

  constructor() {
  }



   setLocalStorage(frontendUser: FrontendUser) {
    localStorage.setItem(this.username,JSON.stringify(frontendUser.username));
    localStorage.setItem(this.id, JSON.stringify(frontendUser.id))
  }


  public isLoggedIn(){
    return localStorage.getItem(this.username) && localStorage.getItem(this.id);
  }

}
