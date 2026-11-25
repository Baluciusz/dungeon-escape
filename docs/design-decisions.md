# Design Documentation

## 1. Problem statement

**Dungeon Escape** is a short, choice-based survival game played in the console.

- **User:** a single player who wants a quick, replayable game session (5-20 minutes).
- **Goal of the player:** climb from the bottom of a dungeon through 5 floors and escape,
  while managing two resources (HP and Food) and a small inventory, and reach a high score.
- **How it works:** every floor consists of random events. For each event the player picks
  one of a few options; the result depends on the choice and on chance. Between events the
  player can check the status, the map and the inventory.
- **Problem it solves / purpose:** it provides a small but complete example of a game that is
  easy to extend with new event types, keeps results between runs (high scores), and
  demonstrates object-oriented design: abstraction, polymorphism, composition, a design
  pattern, collections, exceptions, file persistence and unit tests.

## 2. Architecture

The code is split into packages by responsibility (see also the
[class diagram](class-diagram.md)):

| Package | Responsibility |
|---|---|
| `domain` | The game model: player, items, inventory, enemies, difficulty, run state, leaderboard. No console input/output. |
| `event` | The six event types behind the abstract `GameEvent`. Events return an `EventOutcome` instead of printing. |
| `factory` | Event creation with the Factory Method pattern. |
| `controller` | `GameController` runs menus, floors and events and connects all other parts. |
| `persistence` | Reading and writing `highscores.txt`. |
| `ui` | Console input with validation (`ConsoleUI`) and ASCII screens (`AsciiArt`). |

`Main` is the only class that knows every concrete creator; it builds the objects and
passes them to the controller (dependencies are injected through constructors).

## 3. Design pattern: Factory Method

### The design problem

The game has six kinds of events, and every floor needs many random ones. Without a pattern,
the game loop would contain something like
`switch (type) { case TRAP -> new TrapEvent(...); case CHEST -> ... }`.
That couples the loop to every concrete event class, and every new event type would require
editing the loop (violating the open-closed principle).

### Why Factory Method solves it

The lecture defines Factory Method as: *the superclass declares a creation method and the
subclass decides which concrete type is created.* In this project:

| Role | Class | Responsibility |
|---|---|---|
| Product | `GameEvent` (abstract) | Common interface of all events: title, art, options, `resolve()`. |
| Concrete products | `ExplorationEvent`, `EnemyEncounterEvent`, `TrapEvent`, `AbandonedCampEvent`, `TreasureChestEvent`, `RestingPlaceEvent` | Event-specific rules in `handleChoice()`. |
| Creator | `GameEventCreator` (abstract) | Declares the factory method `createEvent(floor, random)` and the shared algorithm `prepareEvent()` that validates the input, calls the factory method and checks the result (like `order()` in the lecture's `PizzaStore`). |
| Concrete creators | `ExplorationEventCreator`, `EnemyEncounterEventCreator`, `TrapEventCreator`, `AbandonedCampEventCreator`, `TreasureChestEventCreator`, `RestingPlaceEventCreator` | Override `createEvent()` and instantiate exactly one concrete event. `EnemyEncounterEventCreator` also builds the `Enemy` for the current floor. |
| Client | `EventSelector` (used by `GameController`) | Holds a `List<GameEventCreator>`, picks one at random and delegates creation to it. |

This is **not** a Simple Factory: there is no central `create(String type)` method with a
switch. The decision is made by polymorphism - by whichever subclass of `GameEventCreator`
is called.

### Runtime collaboration

1. `GameController` asks `eventSelector.nextEvent(floor)`.
2. `EventSelector` picks a random `GameEventCreator` from its list and calls
   `creator.prepareEvent(floor, random)`.
3. `prepareEvent()` validates the floor and calls the overridden `createEvent()`;
   e.g. `TrapEventCreator` returns `new TrapEvent(random)`.
4. The controller receives a `GameEvent` and only uses the abstract type: it prints
   `getArt()`, `getTitle()` and `getOptions(player)`, then calls `resolve(choice, player)`.
   The concrete event's rules run through dynamic dispatch.

### Benefits and trade-offs

- **Open for extension:** a new event type needs one event class and one creator class,
  plus one line in `Main`. `GameController` and `EventSelector` do not change.
- **Loose coupling:** the controller depends only on `GameEvent` and `EventSelector`.
- **Testability:** each creator can be tested on its own (`EventCreatorTest`).
- **Trade-off:** more classes - each event needs a small creator class. For six event types
  this is acceptable; a Simple Factory would be shorter but would not satisfy the
  open-closed principle and is not a GoF pattern.

## 4. How the OOP requirements are met

| Requirement | Where in the code |
|---|---|
| Encapsulation | All fields are private; constructors validate (`Player`, `GameState`, `Enemy`, `HighScoreEntry`); HP and Food can only change through methods that keep them in 0-100. `Inventory` and `Leaderboard` return read-only lists. |
| Abstraction | Abstract classes `GameEvent`, `GameEventCreator`, `Item`; abstract methods in the `Difficulty` enum. |
| Inheritance and polymorphism | 6 events override `getArt()`, `getOptions()`, `handleChoice()`; 6 creators override `createEvent()`; `Bread`, `HealthPotion`, `Weapon` override `applyTo()` (eat, heal or equip); each `Difficulty` constant overrides `createPlayer()` and `rollEventsPerFloor()`. |
| Composition | `GameState` *-- `Player` *-- `Inventory`; `EnemyEncounterEvent` *-- `Enemy`. |
| Aggregation | `Inventory` o-- `Item` (0..*); `EventSelector` o-- `GameEventCreator` (1..*); `Leaderboard` o-- `HighScoreEntry` (0..10). |
| Association | `GameController` --> `ConsoleUI`, `EventSelector`, `HighScoreRepository`; `Player` --> `Weapon` (0..1); `Enemy` --> `EnemyType`. |
| Generic collections | `List<Item>`, `List<GameEventCreator>`, `List<HighScoreEntry>`, `List<String>`. |
| Sorting | `Comparator.comparing(Item::getName)` for the inventory; `Comparator.comparingInt(HighScoreEntry::getScore).reversed().thenComparing(...)` for the leaderboard. |
| Custom exceptions | `ItemNotFoundException` (checked, domain rule: you can only use what you have); `HighScoreFileException` (checked, file problems); `InputClosedException` (unchecked, console closed). |
| Input validation | `ConsoleUI.readChoice()` and `readName()` re-ask on invalid input; `GameEvent.resolve()` rejects invalid choices. |
| Persistence | `HighScoreRepository` reads and writes `highscores.txt` with try-with-resources. |
| Testing | 52 JUnit 5 tests in 10 test classes (`.\mvnw.cmd clean test`). |

## 5. Main design decisions

1. **Maven with the Maven Wrapper.** The project builds, tests and packages from a clean
   checkout with `.\mvnw.cmd clean package`; no separately installed Maven and no IDE
   settings are needed. The JAR has a fixed name (`target/dungeon-escape.jar`).
2. **Events do not print.** Events return an `EventOutcome` with a message. This keeps the
   game rules independent of the console and makes them unit-testable.
3. **Randomness is injected.** Every class that rolls dice receives a `Random` object.
   Tests pass a `FixedRandom` with predefined numbers, so random outcomes can be tested exactly.
4. **High scores instead of saved games.** A run is short, so instead of saving the game
   state the project keeps a persistent top-10 leaderboard. This satisfies the persistence
   requirement with a simpler, easy-to-validate text format (`NAME;SCORE` per line).
5. **Robust file handling.** A missing file means an empty leaderboard; invalid lines are
   skipped and counted; real I/O errors become a checked `HighScoreFileException`, which the
   controller turns into a warning, so file problems never stop the game.
6. **Swords are items.** `Weapon` extends `Item`, so it is stored in the same inventory and
   "using" it equips it. This reuses existing code and shows polymorphism instead of adding a
   separate equipment system.
7. **Difficulty as an enum with behaviour.** Each constant overrides its own start values and
   event count, so no `switch` on the difficulty is needed anywhere.
8. **Simple scoring.** 100 x floor number per defeated enemy and 1000 per cleared floor;
   a good run reaches roughly 6,000-10,000 points.

## 6. Known limitations

- Console only; the ASCII art assumes a monospaced font and at least 80 columns.
- No saving of an unfinished run - closing the program ends the current run (by design).
- `highscores.txt` is written to the folder the program is started from; it is local to that
  computer and not protected against manual editing.
- Player names are limited to 12 characters and the `;` character is removed.
- Game balance was tuned with simulated runs, not with large-scale play testing.
- The console UI itself (`GameController` menus) is tested manually; unit tests cover the
  game rules, Factory Method, leaderboard, file handling and input validation.
