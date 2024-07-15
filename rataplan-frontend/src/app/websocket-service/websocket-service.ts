import { Injectable } from '@angular/core';
import { MatSnackBar } from '@angular/material/snack-bar';
import { Actions } from '@ngrx/effects';
import { Store } from '@ngrx/store';
import { Client, IFrame, IMessage } from '@stomp/stompjs';
import { withLatestFrom } from 'rxjs';
import { authFeature } from '../authentication/auth.feature';
import { notificationActions } from '../notification/notification.actions';

export type Notification = {link: string, notificationType: string, recipientId: number}

@Injectable()
export class WebSocketService {
  
  stompClient!: Client;
  
  constructor(private store: Store, private readonly snackbar: MatSnackBar,
  )
  {
    store.select(authFeature.selectUser).pipe(withLatestFrom(store.select(authFeature.selectToken)))
      .subscribe(([user, token]) => {
          if(!token && this.stompClient.connected) this.stompClient.deactivate();
          else if(token && !this.stompClient?.connected && user) {
            const headers = {'Authorization': token};
            this.stompClient = new Client(
              {
                brokerURL: 'ws://localhost:8081/notifications',
                onConnect: (frame: IFrame) => {
                  this.stompClient.subscribe(
                    `/notifications`,
                    (message) => this.processNotification(message),
                    headers,
                  );
                },
              },
            );
            this.stompClient.activate();
          }
        },
      );
  }
  
  processNotification(message: IMessage) {
    const tempNotification: {
      link: string,
      type: string,
      recipientId: number
    } = JSON.parse(message.body);
    switch(tempNotification.type) {
    case 'vote/invite':
      this.store.dispatch(notificationActions.invitation({
        link: tempNotification.link,
        notificationType: tempNotification.type,
        recipientId: tempNotification.recipientId,
      }!));
      this.snackbar.open('Sie wurden soeben zu einer neuen Abstimmung eingeladen', 'Ok', {duration: 5000});
      break;
    }
  }
  
}