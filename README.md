# Dungeon Escape

A small, choice-based **ASCII dungeon survival game** for the console, written in Java as a
university OOP project (Programming 2). Event creation uses the **Factory Method** design
pattern, and the best results are kept on a persistent **high score** list.

## Game concept

You wake up on the lowest floor of a dungeon and must climb through **5 floors** to reach the
exit. Every floor is a series of random events. For each event you choose what to do; the
outcome depends on your choice and on chance. Survive by managing **HP** and **Food**, use
the items you find, and try to escape with the highest score.

## Features

- Six event types, each with its own ASCII picture and choices:

  | Event | Choices |
  |---|---|
  | Exploration | careful path (costs Food) or risky shortcut |
  | Enemy Encounter | fight or try to escape an Ogre, Orc or Goblin (stronger on higher floors) |
  | Trap | disarm it or run through it (a failed trap may turn out to be defective) |
  | Abandoned Camp | search for Bread - but the camp may not be abandoned |
  | Treasure Chest | Bread, Health Potion, Rusty Sword (+10% win chance) or Steel Sword (+20%) |
  | Resting Place | rest to recover HP |

- Three difficulties:

  | Difficulty | Start | Events per floor |
  |---|---|---|
  | EASY | 100 HP, 100 Food, 5 Bread | 6 |
  | NORMAL | 100 HP, 100 Food, no Bread | 12 |
  | CHAOS | random 40-100 HP, 30-100 Food, 0-5 Bread | random 13-16 |

- Survival: every event costs 5 Food; with no Food left you lose 5 HP per event.
- Inventory sorted alphabetically: **Bread** (+25 Food), **Health Potion** (+20 HP) and
  swords that can be equipped and unequipped.
- ASCII main menu, dungeon map, status bar, event pictures, game over and victory screens.
- Scoring: **100 x floor number** for each defeated enemy and **1000** for each cleared floor.
- **High scores:** the top 10 results are saved to `highscores.txt` and shown in a retro table.
- Every input is validated - typos never crash the game.

## Prerequisites

- **Java JDK 25** (check with `java -version`).
- Nothing else: Maven is downloaded automatically by the included **Maven Wrapper**
  (`mvnw` / `mvnw.cmd`) on the first build, so an internet connection is needed once.

## Build, test and run

Clone the repository and open a terminal in the project folder:

```powershell
git clone https://github.com/Baluciusz/dungeon-escape.git
cd dungeon-escape
```

| Task | Windows (PowerShell / cmd) | macOS / Linux |
|---|---|---|
| Run all unit tests | `.\mvnw.cmd clean test` | `./mvnw clean test` |
| Build the runnable JAR (also runs the tests) | `.\mvnw.cmd clean package` | `./mvnw clean package` |
| Start the game | `java -jar target\dungeon-escape.jar` | `java -jar target/dungeon-escape.jar` |

A ready-made `dungeon-escape.jar` is also attached to the latest
[GitHub release](https://github.com/Baluciusz/dungeon-escape/releases); it only needs
`java -jar dungeon-escape.jar`.

**In an IDE:** open the folder (or `pom.xml`) in IntelliJ IDEA as a Maven project and run
`dungeon.Main`; run the tests by right-clicking `src/test/java` and choosing *Run All Tests*.

## Controls

Type the number of an option and press **ENTER**. On a new game you also type your name.
Between events you can choose `[1] Continue`, `[2] Inventory` (use Bread or a potion, equip
or unequip a sword) or `[3] Map`.

## Sample usage

```text
================================
       CHOOSE DIFFICULTY
================================

[1] EASY   - 100 HP, 100 Food, 5 Bread
[2] NORMAL - 100 HP, 100 Food, no Bread
[3] CHAOS  - random HP, Food and Bread
Choose: 2

Good luck, KNIGHT. You wake up on the lowest floor of the dungeon. Find your way out!

HP [##########] 100/100 | FOOD [##########] 100/100 | FLOOR 1/5 | NORMAL | SCORE 000000
WEAPON: none (bare hands)
Floor 1 - encounter 1 of 12
[1] Continue
[2] Inventory
[3] Map
Choose: 1

        /\
       /  \          (
      /    \        ) )
     /  ||  \      (__)
    /___||___\    /____\

YOU DISCOVER AN OLD CAMP.

[1] Search the camp
[2] Leave it alone
Choose: 1

>> You find 2 Bread in an old bag!
>> Time passes. Food -5.
```

After the run ends, the final score and the leaderboard are shown:

```text
================================
          HIGH SCORES
================================
+------+--------------+--------+
| RANK | NAME         |  SCORE |
+------+--------------+--------+
|   1. | DAVE         | 007200 |
|   2. | KNIGHT       | 006600 |
+------+--------------+--------+
```

## Design pattern: Factory Method

Creating the random events is the design problem: the game loop should not contain a
`switch` that knows every concrete event class.

- `GameEvent` is the abstract **product**; the six event classes are concrete products.
- `GameEventCreator` is the abstract **creator**. Its `prepareEvent()` method validates the
  floor and calls the **factory method** `createEvent()`.
- Six **concrete creators** (e.g. `TrapEventCreator`) override `createEvent()` and decide
  which concrete event is instantiated.
- `EventSelector` keeps a `List<GameEventCreator>`, picks one at random and delegates the
  creation to it. `GameController` only ever works with the abstract `GameEvent`.

A new event type therefore needs one event class and one creator - the game loop does not
change. Full explanation: [docs/design-decisions.md](docs/design-decisions.md#3-design-pattern-factory-method).

## Implemented requirements

| Requirement | Implementation |
|---|---|
| Encapsulation | private fields, validating constructors, controlled changes (`Player`, `GameState`, `HighScoreEntry`) |
| Abstraction | abstract `GameEvent`, `GameEventCreator`, `Item`; abstract methods in `Difficulty` |
| Inheritance and runtime polymorphism | 6 events, 6 creators, 3 item types (`applyTo()` eats, heals or equips), 3 difficulties |
| Relationships | composition `GameState`-`Player`-`Inventory`, aggregation `Inventory`-`Item`, associations from `GameController` (see the [UML](docs/class-diagram.md)) |
| Generic collections and sorting | `List<Item>` sorted with `Comparator.comparing(Item::getName)`; `List<HighScoreEntry>` sorted highest score first |
| Exceptions | custom `ItemNotFoundException`, `HighScoreFileException`, `InputClosedException`; validated input |
| Persistence | `HighScoreRepository` reads/writes `highscores.txt` with try-with-resources; missing or damaged files are handled |
| Design pattern | Factory Method (`dungeon.factory`) |
| Tests | 52 JUnit 5 tests: `.\mvnw.cmd clean test` |
| Build | Maven + Maven Wrapper, runnable JAR |

## Project structure

```text
dungeon-escape/
+-- pom.xml                 Maven build (Java 25, JUnit 5, runnable JAR)
+-- mvnw, mvnw.cmd, .mvn/   Maven Wrapper
+-- README.md, LICENSE
+-- docs/
|   +-- class-diagram.md    UML class diagrams (Mermaid)
|   +-- design-decisions.md problem statement, Factory Method, decisions, limitations
|   +-- user-stories.md
+-- src/main/java/dungeon/
|   +-- Main.java           entry point, wires the objects together
|   +-- controller/         GameController: menus, floors, game loop
|   +-- domain/             Player, Inventory, items, enemies, difficulty, GameState, Leaderboard
|   +-- event/              GameEvent, EventOutcome and the six concrete events
|   +-- factory/            Factory Method: GameEventCreator, six creators, EventSelector
|   +-- persistence/        HighScoreRepository (highscores.txt)
|   +-- ui/                 ConsoleUI (validated input) and AsciiArt (screens)
+-- src/test/java/dungeon/  JUnit 5 tests
```

## Documentation

- [UML class diagram](docs/class-diagram.md)
- [Problem statement, design pattern, design decisions and known limitations](docs/design-decisions.md)
- [User stories](docs/user-stories.md)

## Known limitations

- Console only; the ASCII art needs a monospaced font and about 80 columns.
- An unfinished run is not saved - only finished runs reach the high score list.
- `highscores.txt` is created in the folder the game is started from.
- Names are limited to 12 characters; the `;` character is removed.

## Contributor

- Bálint (GitHub: [Baluciusz](https://github.com/Baluciusz))

## License

This project is licensed under the MIT License - see [LICENSE](LICENSE).
