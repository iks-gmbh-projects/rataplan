import { Component, Input } from '@angular/core';

@Component({
  selector: 'app-group-display',
  templateUrl: './group-display.component.html',
  styleUrls: ['./group-display.component.css']
})
export class GroupDisplayComponent {
  @Input() contacts: (string|number)[] = [];
}
