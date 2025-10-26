#language: de
Funktionalität: Umfragen

    Grundlage:
        Gegeben sei der Benutzer hat die Umfragen-Seite geöffnet und eine neue Umfrage erstellt

    Szenario: Umfrage Startseite
        Wenn der Benutzer die erste Seite ausfüllt
        Und der Benutzer weiter navigiert
        Dann landet der Benutzer auf der zweiten Seite

    Szenario: Umfrage zweite Seite
        Wenn der Benutzer die erste Seite ausfüllt
        Und der Benutzer weiter navigiert
        Und der Benutzer die zweite Seite ausfüllt
        Und der Benutzer auf Vorschau klickt
        Dann ist die Frage "Test Frage" sichtbar