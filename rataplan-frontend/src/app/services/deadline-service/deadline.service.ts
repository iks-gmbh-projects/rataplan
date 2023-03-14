import { Injectable } from '@angular/core';

@Injectable({
  providedIn: 'root'
})
export class DeadlineService {
  private deadline = new Date();

  constructor() {}

  setDeadline(deadline: Date) {
    this.deadline = deadline;
  }

  hasDeadlinePassed(): boolean {
    if (!this.deadline) {
      throw new Error('Deadline has not been set.');
    }

    const now = new Date();
    return this.deadline.getTime() < now.getTime();
  }
}
