// converts a Date to a string of the form 'YYYY-MM-DD'
import {FormGroup} from "@angular/forms";
export function dateToString(date: Date): string {
  if (date) {
    let month = (date.getMonth() + 1) < 10 ? "0" + (date.getMonth() + 1) : "" + (date.getMonth() + 1);
    let day = date.getDate() < 10 ? "0" + date.getDate() : "" + date.getDate();
    return "" + date.getFullYear() + "-" + month + "-" + day;
  } else {
    return "";
  }
}

// converts a string of the form 'YYYY-MM-DD' to Date
export function stringToDate(dateString: String): Date {
  if (dateString && dateString.length == 10) {
    let day: number = parseInt(dateString.substr(8, 2));
    let month: number = parseInt(dateString.substr(5, 2)) - 1;
    let year: number = parseInt(dateString.substr(0, 4));

    if (isNaN(day) || isNaN(month) || isNaN(year)) {
      return new Date(1970, 0, 1);
    }

    return new Date(year, month, day);
  }

  return new Date(1970, 0, 1);
}

export function isDifferentTime(timeA: string, timeB: string) {
  timeA = (timeA == undefined ? "" : timeA);
  timeB = (timeB == undefined ? "" : timeB);

  return !(timeA === timeB || toTimeString(timeA) == toTimeString(timeB));

}

// time must have the form: ^([0-1][0-9]|2[0-3]|[0-9]):?[0-5][0-9]$
export function toTimeString(time: string): string {
  if (time.length < 3) {
    return '00:00';
  }

  if (time.length == 5) {
    return time;
  }

  let tmp = time.split(':');
  if (tmp.length > 1) {
    return '0' + time;
  }

  if (time.length == 4) {
    return time[0] + time[1] + ':' + time[2] + time[3];
  }

  if (time.length == 3) {
    return '0' + time[0] + ':' + time[1] + time[2];
  }
}

export function buildDateFormatString(hasDate: boolean, hasTime: boolean): string {
  let dateFormat;

  if (hasDate) {
    dateFormat = 'DD.MM.YYYY';

    if (hasTime) {
      dateFormat += " HH:mm [Uhr]";
    }
  } else if (hasTime) {
    dateFormat = "HH:mm [Uhr]";
  }

  return dateFormat;
}

/*
  Function to determine if at least one checkbox of a set of checkboxes in a FormGroup is checked
 */
export function isOneChecked(values: FormGroup): boolean {
  for (let key of Object.keys(values)) {
    if (this.appointmentConfiguration.get(key).value === true) {
      return true;
    }
  }
  return false;
}

export function shortenStringToWidth(name: string, allowedWidth: number): string {
  let shortened: string;
  let canvas = document.createElement('canvas');
  let ctx = canvas.getContext('2d');
  ctx.font = '15px Lato';
  let width = ctx.measureText(name).width;

  if (width < allowedWidth) {
    return null;
  } else {
    let length = name.length;
    length -= 2;
    while (width > allowedWidth) {
      length--;
      shortened = name.substring(0, length) + '...';
      width = ctx.measureText(shortened).width;
    }
    return shortened;
  }

}
