export function combineDateTime(date: string|undefined|null, time: string|null): string|undefined {
  let dateString = '';
  if (date) {
    const dateValue = new Date(date);
    dateString = dateValue.getFullYear() + '-' +
      ('00' + (dateValue.getMonth() + 1)).slice(-2) + '-' +
      ('00' + dateValue.getDate()).slice(-2);
  } else return undefined;
  if (time) {
    dateString = dateString + ' ' + time + ':00';
  }
  return new Date(dateString).toISOString();
}
