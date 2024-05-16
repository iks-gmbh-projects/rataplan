export type MultiChange = {
  readonly name: string,
  readonly changes: readonly Change[],
};
export type Change = string | MultiChange;

export const patchNotes: {
  readonly [version: `${number}.${number}.${number}`]: {
    readonly releaseDate: Date,
    readonly changes: readonly Change[],
  }
} = {
  '2.1.0': {
    releaseDate: new Date(Date.UTC(2023, 9)),
    changes: [
      'Einführung von öffentlichen Patchnotes',
    ],
  },
  '2.2.0': {
    releaseDate: new Date(Date.UTC(2024, 4, 16)),
    changes: [
      {
        name: 'Änderungen:',
        changes: [
          'Neues Layout',
          'Neue Farbgebung',
          'Umfrageergebnisse werden wenn möglich als Kreisdiagramm angezeigt',
          'Anzahl der Ja-Stimmen werden bei Abstimmungen bereits in der Übersicht angezeigt',
          'Download von Abstimmungsergebnissen',
          'Feedbackfunktion hinzugefügt',
          'Email-Benachrichtigungen sind konfigurierbar',
          'Kontakte hinzugefügt',
        ],
      },
      {
        name: 'Fehlerbehebungen:',
        changes: [
          'Bearbeitungslink in Emails korrigiert',
          'Verhalten bei ungültigen Tokens angepasst',
        ],
      },
    ],
  },
};