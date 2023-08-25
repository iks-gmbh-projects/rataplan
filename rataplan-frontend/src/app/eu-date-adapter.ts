import { Injectable } from '@angular/core';
import { NativeDateAdapter } from '@angular/material/core';

@Injectable()
export class EUDateAdapter extends NativeDateAdapter {
  
  override parse(date: string): Date | null {
    const match = date.match(/^(\d{1,2})[-/\\.](\d{1,2})[-/\\.](\d+)$/);
    if(match) {
      const year = (
        match[3].length > 2 ? 0 : 2000
      ) + Number(match[3]);
      const month = Number(match[2]) - 1;
      const day = Number(match[1]);
      const d = new Date(year, month, day);
      if(d.getFullYear() != year || d.getMonth() != month || d.getDate() != day) {
        return null;
      }
      return d;
    }
    return null;
  }
  
}
