export function combineDateTime(date: string|null, time: string|null): string|null {
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
  try {
    return new Date(dateString).toISOString();
  } catch {
    return null;
  }
}
