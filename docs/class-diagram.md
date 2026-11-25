# UML Class Diagram

The diagrams are written in [Mermaid](https://mermaid.js.org/). GitHub renders them
directly, and the same text is the editable source.

Image versions (open in any web browser, no Mermaid support needed):
[class-diagram.svg](class-diagram.svg) (overview) and
[factory-method.svg](factory-method.svg) (Factory Method).
They were exported from the Mermaid source below; after changing the source, export them again.

Notation: `+` public, `-` private, `#` protected, `*` abstract method, `$` static.
Relationship arrows:

| Arrow | Meaning |
|---|---|
| `<\|--` | inheritance / extends |
| `*--` | composition (the part lives only inside the whole) |
| `o--` | aggregation (the whole holds parts that can exist on their own) |
| `-->` | association |
| `..>` | dependency (uses / creates / throws) |

Constants and getters are left out unless they matter for the design.

## 1. Overview

```mermaid
classDiagram
    direction TB

    class Main {
        +main(String[] args)$
    }

    class GameController {
        -ConsoleUI ui
        -EventSelector eventSelector
        -HighScoreRepository highScores
        -Random random
        +GameController(ConsoleUI, EventSelector, HighScoreRepository, Random)
        +run()
    }

    class GameState {
        -String playerName
        -Player player
        -Difficulty difficulty
        -int clearedFloors
        -int score
        +newGame(String, Difficulty, Random)$ GameState
        +getCurrentFloor() int
        +hasEscaped() boolean
        +recordEnemyDefeated() int
        +completeCurrentFloor() int
    }

    class Difficulty {
        <<enumeration>>
        EASY
        NORMAL
        CHAOS
        +createPlayer(Random)* Player
        +rollEventsPerFloor(Random)* int
    }

    class Player {
        -int hp
        -int food
        -Inventory inventory
        -Weapon equippedWeapon
        +takeDamage(int) int
        +heal(int) int
        +eat(int) int
        +applyHunger() boolean
        +addItem(Item)
        +useItem(String) String
        +equipWeapon(Weapon) String
        +unequipWeapon() String
        +getWeaponBonus() int
    }

    class Inventory {
        -List~Item~ items
        +addItem(Item)
        +removeItem(String) Item
        +countOf(String) int
        +getSortedItems() List~Item~
    }

    class Item {
        <<abstract>>
        -String name
        +getName() String
        +getDescription()* String
        +applyTo(Player)* String
    }
    class Bread
    class HealthPotion
    class Weapon {
        -int winChanceBonus
        +rustySword()$ Weapon
        +steelSword()$ Weapon
        +getWinChanceBonus() int
    }

    class ItemNotFoundException {
        -String itemName
    }

    class Enemy {
        -EnemyType type
        -int floor
        +getWinChance() int
        +getWinChanceWith(int) int
        +getDamage() int
        +getEscapeFailDamage() int
    }
    class EnemyType {
        <<enumeration>>
        OGRE
        ORC
        GOBLIN
        +random(Random)$ EnemyType
    }

    class GameEvent {
        <<abstract>>
        -Random random
        +getTitle()* String
        +getArt()* String
        +getOptions(Player)* List~String~
        +resolve(int, Player) EventOutcome
        #handleChoice(int, Player)* EventOutcome
        #chance(int) boolean
    }
    class EventOutcome {
        -String message
        -GameEvent followUpEvent
        -boolean enemyDefeated
        +of(String)$ EventOutcome
        +enemyDefeated(String)$ EventOutcome
        +withFollowUp(String, GameEvent)$ EventOutcome
    }
    class ExplorationEvent
    class EnemyEncounterEvent {
        -Enemy enemy
    }
    class TrapEvent
    class AbandonedCampEvent {
        -int floor
    }
    class TreasureChestEvent
    class RestingPlaceEvent

    class GameEventCreator {
        <<abstract>>
        +prepareEvent(int, Random) GameEvent
        #createEvent(int, Random)* GameEvent
    }
    class EventSelector {
        -List~GameEventCreator~ creators
        -Random random
        +nextEvent(int) GameEvent
    }

    class HighScoreEntry {
        -String playerName
        -int score
    }
    class Leaderboard {
        -List~HighScoreEntry~ entries
        +addEntry(HighScoreEntry) int
        +getEntries() List~HighScoreEntry~
    }
    class HighScoreRepository {
        -Path file
        -int skippedLines
        +load() Leaderboard
        +save(Leaderboard)
    }
    class HighScoreFileException

    class ConsoleUI {
        -Scanner scanner
        -PrintStream out
        +readChoice(int, int) int
        +readName(int) String
        +printOptions(List~String~)
    }
    class AsciiArt {
        <<utility>>
        +dungeonMap(GameState)$ String
        +statusBar(GameState)$ String
        +highScoreTable(Leaderboard)$ String
    }
    class InputClosedException

    Main ..> GameController : creates and wires
    GameController "1" --> "1" ConsoleUI
    GameController "1" --> "1" EventSelector
    GameController "1" --> "1" HighScoreRepository
    GameController ..> GameState : plays one run
    GameController ..> AsciiArt : renders

    GameState "1" *-- "1" Player
    GameState "1" --> "1" Difficulty
    Difficulty ..> Player : creates
    Player "1" *-- "1" Inventory
    Player "1" --> "0..1" Weapon : equipped
    Inventory "1" o-- "0..*" Item
    Inventory ..> ItemNotFoundException : throws
    Item <|-- Bread
    Item <|-- HealthPotion
    Item <|-- Weapon

    GameEvent <|-- ExplorationEvent
    GameEvent <|-- EnemyEncounterEvent
    GameEvent <|-- TrapEvent
    GameEvent <|-- AbandonedCampEvent
    GameEvent <|-- TreasureChestEvent
    GameEvent <|-- RestingPlaceEvent
    GameEvent ..> EventOutcome : returns
    EventOutcome "1" --> "0..1" GameEvent : follow-up
    EnemyEncounterEvent "1" *-- "1" Enemy
    Enemy "1" --> "1" EnemyType

    EventSelector "1" o-- "1..*" GameEventCreator
    GameEventCreator ..> GameEvent : creates

    Leaderboard "1" o-- "0..10" HighScoreEntry
    HighScoreRepository ..> Leaderboard : loads / saves
    HighScoreRepository ..> HighScoreFileException : throws
    ConsoleUI ..> InputClosedException : throws
```

## 2. Factory Method in detail

`GameEventCreator` is the **Creator**, `GameEvent` the **Product**. Each concrete creator
overrides the factory method `createEvent()` and decides which concrete product is built.
`prepareEvent()` is the shared algorithm that calls the factory method.

```mermaid
classDiagram
    direction LR

    class GameEventCreator {
        <<abstract>>
        +prepareEvent(int floor, Random random) GameEvent
        #createEvent(int floor, Random random)* GameEvent
    }
    class ExplorationEventCreator {
        #createEvent(int, Random) GameEvent
    }
    class EnemyEncounterEventCreator {
        #createEvent(int, Random) GameEvent
    }
    class TrapEventCreator {
        #createEvent(int, Random) GameEvent
    }
    class AbandonedCampEventCreator {
        #createEvent(int, Random) GameEvent
    }
    class TreasureChestEventCreator {
        #createEvent(int, Random) GameEvent
    }
    class RestingPlaceEventCreator {
        #createEvent(int, Random) GameEvent
    }

    class GameEvent {
        <<abstract>>
        +resolve(int, Player) EventOutcome
    }

    class EventSelector {
        -List~GameEventCreator~ creators
        +nextEvent(int floor) GameEvent
    }

    EventSelector "1" o-- "1..*" GameEventCreator : delegates to
    EventSelector ..> GameEvent : returns

    GameEventCreator <|-- ExplorationEventCreator
    GameEventCreator <|-- EnemyEncounterEventCreator
    GameEventCreator <|-- TrapEventCreator
    GameEventCreator <|-- AbandonedCampEventCreator
    GameEventCreator <|-- TreasureChestEventCreator
    GameEventCreator <|-- RestingPlaceEventCreator

    GameEvent <|-- ExplorationEvent
    GameEvent <|-- EnemyEncounterEvent
    GameEvent <|-- TrapEvent
    GameEvent <|-- AbandonedCampEvent
    GameEvent <|-- TreasureChestEvent
    GameEvent <|-- RestingPlaceEvent

    ExplorationEventCreator ..> ExplorationEvent : creates
    EnemyEncounterEventCreator ..> EnemyEncounterEvent : creates
    TrapEventCreator ..> TrapEvent : creates
    AbandonedCampEventCreator ..> AbandonedCampEvent : creates
    TreasureChestEventCreator ..> TreasureChestEvent : creates
    RestingPlaceEventCreator ..> RestingPlaceEvent : creates
```

## 3. Packages

| Package | Classes |
|---|---|
| `dungeon` | `Main` |
| `dungeon.controller` | `GameController` |
| `dungeon.domain` | `GameState`, `Difficulty`, `Player`, `Inventory`, `Item`, `Bread`, `HealthPotion`, `Weapon`, `ItemNotFoundException`, `Enemy`, `EnemyType`, `HighScoreEntry`, `Leaderboard` |
| `dungeon.event` | `GameEvent`, `EventOutcome` and the six concrete events |
| `dungeon.factory` | `GameEventCreator`, six concrete creators, `EventSelector` |
| `dungeon.persistence` | `HighScoreRepository`, `HighScoreFileException` |
| `dungeon.ui` | `ConsoleUI`, `AsciiArt`, `InputClosedException` |
