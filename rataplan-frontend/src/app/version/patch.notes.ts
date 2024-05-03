export const patchNotes: {
  readonly [version: `${number}.${number}.${number}`]: {
    readonly releaseDate: Date,
    readonly changes: readonly string[],
  }
} = {
  '2.1.0': {
    releaseDate: new Date(Date.UTC(2023, 9)),
    changes: [
      "Einführung von öffentlichen Patchnotes",
    ],
  },
  '2.2.0': {
    releaseDate: new Date(Date.UTC(2024, 5)),
    changes: [
      "Änderungen:",
      "Neues Layout",
      "Neue Farbgebung",
      "Umfrageergebnisse werden wenn möglich als Kreisdiagramm angezeigt",
      "Anzahl der Ja-Stimmen werden bei Abstimmungen bereits in der Übersicht angezeigt",
      "Download von Abstimmungsergebnissen",
      "Feedbackfunktion hinzugefügt",
      "Email-Benachrichtigungen sind konfigurierbar",
      "Kontakte hinzugefügt",
      "Fehlerbehebungen:",
      "Bearbeitungslink in Emails korrigiert",
      "Verhalten bei ungültigen Tokens angepasst",
    ],
  },
};