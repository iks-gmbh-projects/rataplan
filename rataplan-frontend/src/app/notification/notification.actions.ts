import { createActionGroup, emptyProps } from '@ngrx/store';

function notifyProps(type: string, message: string, count?: number): {
  notificationType: string,
  message: string,
  count: number,
};

function notifyProps(type: string, count: number): {
  notificationType: string,
  message: undefined,
  count: number,
};

function notifyProps(type: string, msgc: string|number, count?: number) {
  const c = Number(msgc);
  let message: string|undefined;
  if(Number.isNaN(c)) {
    message = `${msgc}`;
    count = count ?? 1;
  } else {
    message = undefined;
    count = c;
  }
  return {
    notificationType: type,
    message,
    count,
  };
}

export const notificationActions = createActionGroup({
  source: "Notification",
  events: {
    notify: notifyProps,
    clear: (type: string) => ({notificationType: type}),
    clearAll: emptyProps(),
  },
});
