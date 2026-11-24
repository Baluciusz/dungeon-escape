# Dungeon Escape

A small, choice-based ASCII dungeon survival game for the console, written in Java
as a university OOP project (Prog 2).

> **Status: early version (v0.5.0).** The basics and a rough but complete gameplay
> loop are in place. Saving/loading, unit tests and the full documentation are
> still to come.

## The game

You wake up on the lowest floor of a dungeon and must climb through **5 floors** to
reach the exit. Every floor has **3-4 random events**:

| Event | What can happen |
|---|---|
| Exploration | Careful path (costs Food) or risky shortcut |
| Enemy Encounter | Fight or try to escape an Ogre, Orc or Goblin |
| Trap | Disarm it or run through it |
| Abandoned Camp | Search for Bread - but the camp may not be abandoned |
| Treasure Chest | May contain a Health Potion |
| Resting Place | Rest to recover HP |

You lose 10 Food after every event; with no Food left you start starving and lose HP.
If your HP reaches 0, the game is over.

## Requirements

- Java 25
- IntelliJ IDEA (the project uses the IntelliJ build system)

## How to run

1. Open the project folder in IntelliJ IDEA (**File -> Open**).
2. Open `src/dungeon/Main.java`.
3. Click the green run button next to `main`.

## Design pattern

Game events are created with the **Factory Method** pattern
(`GameEventCreator` and its six concrete creators in the `dungeon.factory` package).

## Contributor

- Balint (GitHub: [Baluciusz](https://github.com/Baluciusz))
