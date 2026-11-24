# Dungeon Escape

A small, choice-based ASCII dungeon survival game for the console, written in Java
as a university OOP project (Prog 2).

> **Status: work in progress.** The game is fully playable. Saving/loading, unit tests
> and the design documentation (UML, user stories) are still to come.

## The game

You wake up on the lowest floor of a dungeon and must climb through **5 floors** to
reach the exit. Every floor has **3-4 random events**:

| Event | What can happen |
|---|---|
| Exploration | Careful path (costs Food) or risky shortcut |
| Enemy Encounter | Fight or try to escape an Ogre, Orc or Goblin (stronger on higher floors) |
| Trap | Disarm it or run through it |
| Abandoned Camp | Search for Bread - but the camp may not be abandoned |
| Treasure Chest | May contain a Health Potion |
| Resting Place | Rest to recover HP |

You lose 10 Food after every event; with no Food left you start starving and lose HP.
Use **Bread** (+25 Food) and **Health Potions** (+30 HP) from your inventory to survive.
If your HP reaches 0, the game is over.

**Difficulties:** EASY (100 HP, 100 Food, 5 Bread), NORMAL (100 HP, 100 Food, no Bread),
CHAOS (random starting HP, Food and Bread).

## Requirements

- Java 25
- IntelliJ IDEA (the project uses the IntelliJ build system, no Maven/Gradle)

## How to run

1. Open the project folder in IntelliJ IDEA (**File -> Open**).
2. Open `src/dungeon/Main.java`.
3. Click the green run button next to `main` and play in the Run window.

**Controls:** type the number of an option and press ENTER. Invalid input is rejected
and you are asked again.

## Project structure

```text
src/dungeon/
  Main.java      entry point, wires the objects together
  domain/        game model: Player, Inventory, items, enemies, difficulty, game state
  event/         GameEvent and the six concrete events
  factory/       Factory Method: GameEventCreator, six creators, EventSelector
  controller/    GameController: menus, floors, game loop
  ui/            ConsoleUI (validated input/output) and AsciiArt (screens)
```

## Design pattern: Factory Method

Game events are created with the **Factory Method** pattern:

- `GameEvent` is the abstract **product**; the six event classes are concrete products.
- `GameEventCreator` is the abstract **creator**: its `prepareEvent()` method calls the
  factory method `createEvent()`.
- Six concrete creators (e.g. `TrapEventCreator`) override `createEvent()` and decide
  which event is instantiated.
- `EventSelector` picks a random creator and delegates creation to it, so the game loop
  never needs a switch on event types. A new event type only needs a new event class
  and a new creator.

## Contributor

- Bálint (GitHub: [Baluciusz](https://github.com/Baluciusz))

## License

This project is licensed under the MIT License - see [LICENSE](LICENSE).
