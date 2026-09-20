# Java Console Games

A Java 17 console application with four games, a shared menu, input validation,
file-based quiz questions, and background game events. No database, GUI, or
external Java libraries are required.

## Run on Windows

Install a JDK 17 or newer and check `java -version` and `javac -version`.
Extract the ZIP before running it.

From the project folder, run:

```powershell
.\run.ps1
```

Alternatively, run `run.bat` from Command Prompt. Both launchers compile the
sources and set the working directory so the question file can be found.
If Windows blocks PowerShell scripts, use the batch launcher.

In VS Code, open this project folder and run `src/Main.java` using the Java
extension. Set the working directory to the project root if questions cannot load.

For macOS/Linux with a JDK installed:

```sh
mkdir -p out
find src -name '*.java' > out/sources.txt
javac -encoding UTF-8 -d out @out/sources.txt
java -cp out Main
```

## Games

| Menu | Rules |
| --- | --- |
| 1. Treasure Hunt | Find treasure in one of eight rooms in 30 seconds and at most five searches. Empty rooms give higher/lower hints. Repeated or invalid rooms do not use a search. |
| 2. Quiz Battle | Choose Easy (90 seconds), Medium (75 seconds), or Hard (60 seconds). Five shuffled questions per level, one point per correct answer. Invalid answers are retried within the same total time limit. |
| 3. Zombie Survival | Survive 45 seconds. Zombies spawn and attack every four seconds, even while you are idle. Bat deals 25 damage; pistol deals 50 with six bullets. Zombies have 50 health. Three medkits restore 30 health each, capped at 100. Each defeated zombie earns ten points. |
| 4. Snake & Ladder | Two to four players start at zero. Press Enter to roll. Each player has one roll per turn. An exact roll to 100 wins; overshoots stay in place. |
| 5. Exit | End the application. |

During gameplay, enter `Q` to return to the menu. Setup prompts request names or
numeric selections. Timed games end without requiring another Enter press.
Input is line-based, so press Enter to submit every command. Countdown and wave
notifications may appear beside an active prompt; the next submitted line still
goes to that prompt. After a timeout, use the newly displayed menu prompt.

## Quiz question file

Edit `data/questions.txt` using UTF-8. Each question occupies one line:

```text
EASY|Which keyword creates an object?|new|make|class|object|A
```

Fields are difficulty, question, four options, and the correct letter. Allowed
levels are EASY, MEDIUM and HARD; answers are A-D. Do not put `|` inside a field.
Blank lines and lines starting with `#` are ignored. Invalid rows produce a line
number warning and are skipped. A missing file or empty level returns to the menu.
The original six-field format is still accepted and defaults to EASY. Legacy
option prefixes such as `A)` are removed before display.

## Verify

```powershell
.\test.ps1
python tests/integration_test.py
```

The first command compiles production and test sources and runs 31 checks.
The second requires Python 3 (standard library only) and runs 10 console scenarios,
including actual open-stdin timeouts, wins, cancellation, invalid input, replay,
EOF, and checks that game workers stop. Python is not required to play.

See `documentation/IMPLEMENTATION.md` for the architecture and Java concept map.

## Scope

Scores last for the current game. Persistent score storage was optional in the
proposal and is not implemented. `PlayerRepository.java` and `FileManager.java`
remain the original empty placeholders for that future work. No changes have
been pushed to GitHub by this local implementation.
