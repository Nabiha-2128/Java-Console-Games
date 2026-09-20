# Implementation and review notes

## Changes from the supplied repository

The original project had a working basic Quiz Battle, file loading, and a menu
with placeholders. This version preserves the abstract Game class, Player and
Question models, Main entry point, and package structure.

Implemented TreasureService, ZombieService and SnakeLadderService; added quiz
difficulty and timing; replaced independent Scanner instances with one shared
line reader; validated question rows and supported the original file format.
Replaced a time-sensitive "latest Java LTS" question with stable Java concepts.

## Where each Java concept appears

| Concept | Source and purpose |
| --- | --- |
| Abstraction | `model/Game.java` declares `startGame()`. |
| Inheritance | Each of the four services extends Game. |
| Polymorphism | Menu stores services in `Game[]` and calls the chosen implementation. |
| Encapsulation | Player, Question, Room and SurvivalState keep their state private. |
| Custom exception | InvalidChoiceException reports invalid answers, rooms and numbers. |
| Built-in exceptions | Numeric conversion and file IO failures are handled safely. |
| Collections | ArrayList stores rooms, players and questions; Map stores board jumps; ArrayDeque stores zombies. |
| File handling | QuestionRepository reads UTF-8 questions with try-with-resources. |
| Multithreading | GameTimer runs scheduled countdowns; ZombieService schedules waves; ConsoleInput reads stdin on a daemon thread. |
| Synchronization | SurvivalState serializes player actions and background attacks using synchronized methods. |
| Resource cleanup | Game timers close automatically, and the zombie worker shuts down in finally. |

## Input and timing

Only ConsoleInput reads System.in. Its daemon thread places complete lines on a
thread-safe queue. Games poll for input while checking whether their timer or
health still permits play. This avoids a common issue where a blocking Scanner
prevents the game from ending until the player presses Enter.

EOF is represented separately from a timeout. Menu catches EOF and exits cleanly;
the enclosing finally/try-with-resources blocks still clean up workers.
Timers use System.nanoTime for elapsed time, so changes to the computer's clock
do not change the game duration. Countdown notifications run on a scheduled thread.

Game state is created fresh on each startGame call, so replay resets health,
inventory, scores, board positions, room visits and the selected treasure room.
The shared reader lives for the application session; it is not a game-worker leak.

## Zombie rules

One zombie is present at the start. Every four seconds, another is added and the
group attacks for five health per zombie, capped at 25 damage per wave. Actions
target the oldest zombie. A full-health heal and an attack without a target do not
waste supplies. Health and ammunition cannot become negative. Dead players cannot
heal. Survival means health remains positive until the 45-second deadline.

## Board rules

Ladders: 4-14, 9-31, 20-38, 28-84, 40-59, 63-81, 71-91.

Snakes: 17-7, 54-34, 62-19, 64-60, 87-24, 93-73, 99-78.

Each turn has one dice roll, with no additional turn for six. A move beyond 100
leaves the player at the previous position. Names identify players on screen;
their positions are stored independently even if names match.

## Validation performed

- Compiled with JDK 17 and `-Xlint:all`.
- 31 regression checks: snakes, ladders, exact finish, overshoot, invalid dice,
  damage, scores, ammunition, healing, death, malformed files, missing files,
  legacy file format, difficulty banks, input validation, timer expiry and cleanup.
- 10 integration scenarios: bad menu input; EOF; launch/cancel all four games;
  full quiz, invalid answer and replay; deterministic treasure win; complete
  multiplayer board game; idle treasure timeout; idle quiz timeout; idle zombie
  survival; idle zombie death. Each scenario checks the process exit and output.
- Timed integration scenarios deliberately leave stdin open without further input.

The tests use deterministic random sources or short injected durations where
needed; normal game defaults remain 30/90/75/60/45 seconds as documented.

## Suggested classroom demonstration

1. Run the menu and enter a non-number to show validation.
2. Play Treasure Hunt and retry an already searched room.
3. Play Quiz Battle, choose a level, and enter an invalid answer before a valid one.
4. Start Zombie Survival and wait to show the independent spawn thread, then use
   the bat, pistol and a medkit.
5. Start Snake & Ladder with two players and demonstrate alternating turns.
6. Explain the Game array in Menu and the synchronized SurvivalState methods.

Screenshots are not included. Capture your own console while following these
steps if your review requires screenshots of your local run.
