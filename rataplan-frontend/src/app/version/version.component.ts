import { Component } from '@angular/core';
import { patchNotes } from './patch.notes';

@Component({
  selector: 'app-version',
  templateUrl: './version.component.html',
  styleUrls: ['./version.component.css']
})
export class VersionComponent {
  readonly patchNotes = Object.entries(patchNotes)
      .map(([version, notes]) => ({version, releaseDate: notes.releaseDate, notes: notes.changes}))
      .sort((a, b) => a.releaseDate.getTime() - b.releaseDate.getTime());
}
