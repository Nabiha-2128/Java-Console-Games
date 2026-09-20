package service;

import java.util.concurrent.*;
import model.Game;
import model.SurvivalState;
import util.*;

public class ZombieService extends Game {
    private final int seconds;
    private final long spawnMillis;
    public ZombieService() { this(45, 4000); }
    public ZombieService(int seconds, long spawnMillis) {
        if (seconds < 1 || spawnMillis < 1) throw new IllegalArgumentException("Invalid game timing");
        this.seconds = seconds; this.spawnMillis = spawnMillis;
    }
    @Override public void startGame() {
        ConsoleInput input = ConsoleInput.getInstance();
        System.out.println("\nZOMBIE SURVIVAL");
        String name = input.readName("Player name: ");
        System.out.println("Survive " + seconds + " seconds! Zombies spawn and attack every " + spawnMillis / 1000.0 + " seconds.");
        System.out.println("Bat: 25 damage, unlimited. Pistol: 50 damage, 6 bullets. Each zombie has 50 health.");
        System.out.println("Each wave deals 5 damage per zombie (maximum 25). You have 3 medkits (+30 health).");
        SurvivalState state = new SurvivalState();
        ScheduledExecutorService spawner = Executors.newSingleThreadScheduledExecutor(task -> {
            Thread thread = new Thread(task, "zombie-spawner"); thread.setDaemon(true); return thread;
        });
        try (GameTimer timer = new GameTimer(seconds)) {
            spawner.scheduleAtFixedRate(() -> {
                if (timer.isActive() && state.isAlive()) {
                    state.spawnAndAttack();
                    System.out.println("\n[Zombie wave] " + state.status());
                }
            }, spawnMillis, spawnMillis, TimeUnit.MILLISECONDS);
            while (timer.isActive() && state.isAlive()) {
                System.out.println(state.status());
                String command = input.readWhile("1 Bat | 2 Pistol | 3 Heal | 4 Status | Q Quit: ",
                    () -> timer.isActive() && state.isAlive());
                if (command == null) break;
                if (command.equalsIgnoreCase("q")) { System.out.println("Survival game cancelled. Score: " + state.getScore()); return; }
                try {
                    switch (Validation.number(command, 1, 4)) {
                        case 1: System.out.println(state.attack(false)); break;
                        case 2: System.out.println(state.attack(true)); break;
                        case 3: System.out.println(state.heal()); break;
                        default: System.out.println(state.status());
                    }
                } catch (InvalidChoiceException e) { System.out.println(e.getMessage()); }
            }
        } finally {
            spawner.shutdownNow();
            try { spawner.awaitTermination(1, TimeUnit.SECONDS); }
            catch (InterruptedException e) { Thread.currentThread().interrupt(); }
        }
        System.out.println(state.isAlive() ? name + " survived!" : name + " was overwhelmed. Game over.");
        System.out.println("Final score: " + state.getScore());
    }
}
