# User Stories

Each story describes one main player interaction and how it can be checked.

| # | User story | Acceptance criteria |
|---|---|---|
| 1 | As a **player**, I want to **start a new game from a main menu**, so that I can begin a run quickly. | The main menu shows `NEW GAME`, `HIGH SCORES` and `EXIT`. Choosing `NEW GAME` asks for a name and a difficulty. |
| 2 | As a **player**, I want to **enter my name**, so that my result can appear on the leaderboard. | Empty names are rejected; the name is stored for the whole run (max. 12 characters, upper case). |
| 3 | As a **player**, I want to **choose a difficulty**, so that the game matches my experience. | EASY: 100 HP, 100 Food, 5 Bread, 6 events per floor. NORMAL: 100 HP, 100 Food, no Bread, 12 events. CHAOS: random start values and 13-16 events. |
| 4 | As a **player**, I want to **face random events on every floor**, so that each run feels different. | Each event is one of six types (exploration, enemy, trap, camp, chest, resting place); repetitions are possible. |
| 5 | As a **player**, I want to **decide how to handle each event**, so that my choices matter. | Every event shows an ASCII picture and numbered choices; the result is explained in a message. |
| 6 | As a **player**, I want to **fight or escape from enemies**, so that I can manage risk. | The fight option shows the win chance; winning gives points, losing costs HP; a failed escape costs half the damage. |
| 7 | As a **player**, I want to **find and equip swords**, so that fights become easier. | Chests can contain a Rusty Sword (+10%) or Steel Sword (+20%); only one sword is equipped at a time; it can be unequipped. |
| 8 | As a **player**, I want to **use Bread and Health Potions from my inventory**, so that I can survive hunger and damage. | The inventory is listed alphabetically; using an item applies its effect and removes it. |
| 9 | As a **player**, I want to **see my status and the dungeon map**, so that I know where I am. | Between events the HP/Food bars, floor, difficulty, score and weapon are shown; the map marks cleared floors and my position. |
| 10 | As a **player**, I want to **see my final score and rank when a run ends**, so that I know how well I did. | After death or escape the final score is shown, saved to `highscores.txt` and my rank is displayed. |
| 11 | As a **player**, I want to **view the best 10 results**, so that I can compare my runs. | `HIGH SCORES` shows a retro ASCII table sorted by score, highest first, which survives restarting the program. |
| 12 | As a **player**, I want **invalid input to be handled safely**, so that the game never crashes because of a typo. | Letters, empty input or out-of-range numbers print an error message and the question is asked again. |
