export function combineDateTime(date: string|null|undefined, time: string|null|undefined): string {
  console.log(date);
  let dateString = '';
  if (date) {
    const dateValue = new Date(date);
    dateString = dateValue.getFullYear() + '-' +
      ('00' + (dateValue.getMonth() + 1)).slice(-2) + '-' +
      ('00' + dateValue.getDate()).slice(-2);
  }
  if (time) {
    dateString = dateString + ' ' + time + ':00';
  }
  return new Date(dateString).toISOString();
}
