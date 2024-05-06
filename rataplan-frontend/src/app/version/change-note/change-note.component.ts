import { Component, Input } from '@angular/core';
import { Change, MultiChange } from '../patch.notes';

@Component({
  selector: 'app-change-note',
  templateUrl: './change-note.component.html',
  styleUrls: ['./change-note.component.css']
})
export class ChangeNoteComponent {
  @Input() public change?: Change;
  
  get singleChange(): string | undefined {
    return this.change && typeof this.change === "string" ? this.change : undefined
  }
  get multiChange(): MultiChange | undefined {
    return this.change && typeof this.change !== "string" ? this.change : undefined
  }
}