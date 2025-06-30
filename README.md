# Schurke

Schurke ist ein actionreiches Top-Down-Survival-Spiel, entwickelt mit [libGDX](https://libgdx.com/).

## Spielbeschreibung

Du steuerst einen Helden, der sich durch endlose Gegnerwellen kämpft. Überlebe so lange wie möglich, sammle Erfahrung, steige im Level auf und schalte neue Fähigkeiten und Waffen frei. Nutze Powerups, um länger zu überleben und deine Chancen zu verbessern!

## Features
- **Dynamische Gegnerwellen**: Mit jedem Level steigen Schwierigkeit und Gegnerzahl.
- **Waffen-Inventar**: Verschiedene Waffen (Laserpistole, Schrotflinte, Sturmgewehr) können per 1-9 ausgewählt werden. Neue Waffen werden mit höheren Leveln freigeschaltet.
- **Fähigkeiten**:
  - **Dash (Leertaste)**: Schneller Ausweich-Move mit Cooldown.
  - **Schutz-Orb**: Ab Level 15 kreist ein schützender Orb um den Spieler und schadet Gegnern.
  - **Burst Shot (Q)**: Ab Level 10 kann eine Spezialfähigkeit ausgelöst werden, die Laser in alle Richtungen abfeuert (mit Cooldown).
- **Powerups**: Heile dich mit Powerups, die regelmäßig auf der Karte erscheinen (häufiger auf höheren Leveln).
- **Level- und XP-Anzeige**: Oben im UI, modern und übersichtlich.
- **Waffen-UI**: Unten links, zeigt freigeschaltete Waffen mit Icons und Slotnummern.

## Steuerung
- **WASD**: Bewegung
- **Maus**: Zielen
- **Linke Maustaste**: Schießen
- **Leertaste**: Dash
- **Q**: Burst Shot (ab Level 10)
- **1-9**: Waffe auswählen (sofern freigeschaltet)
- **ESC**: Pausemenü

## Freischaltbare Inhalte
- **Schrotflinte**: Ab Level 5 (Slot 2)
- **Burst Shot (Q)**: Ab Level 10
- **Schutz-Orb**: Ab Level 15
- **Sturmgewehr**: Ab Level 20 (Slot 3)

## Build & Run

This project uses [Gradle](https://gradle.org/) for building and running.

**To run the game on desktop:**
```sh
./gradlew lwjgl3:run
```

**To build a runnable JAR:**
```sh
./gradlew lwjgl3:jar
```
The JAR will be in `lwjgl3/build/libs`.

---

Viel Spaß beim Überleben in Schurke!
