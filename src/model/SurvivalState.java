package model;

import java.util.ArrayDeque;

/** All mutations are synchronized: the spawn worker and player share this state. */
public final class SurvivalState {
    private final ArrayDeque<Integer> zombies = new ArrayDeque<>();
    private int health = 100;
    private int ammunition = 6;
    private int medkits = 3;
    private int score;
    public SurvivalState() { zombies.add(50); }
    public synchronized boolean isAlive() { return health > 0; }
    public synchronized int getHealth() { return health; }
    public synchronized int getScore() { return score; }
    public synchronized int getZombieCount() { return zombies.size(); }
    public synchronized int getAmmunition() { return ammunition; }
    public synchronized int getMedkits() { return medkits; }
    public synchronized void spawnAndAttack() {
        if (!isAlive()) return;
        zombies.addLast(50);
        health = Math.max(0, health - Math.min(zombies.size(), 5) * 5);
    }
    public synchronized String attack(boolean pistol) {
        if (!isAlive()) return "You have no health left.";
        if (zombies.isEmpty()) return "No zombies to attack.";
        if (pistol && ammunition == 0) return "No ammunition. Use the bat.";
        if (pistol) ammunition--;
        int remaining = zombies.removeFirst() - (pistol ? 50 : 25);
        if (remaining <= 0) { score += 10; return "Zombie defeated! +10 points."; }
        zombies.addFirst(remaining);
        return "Zombie hit. Remaining zombie health: " + remaining;
    }
    public synchronized String heal() {
        if (!isAlive()) return "You have no health left.";
        if (medkits == 0) return "No medkits left.";
        if (health == 100) return "Health is already full; medkit saved.";
        medkits--;
        health = Math.min(100, health + 30);
        return "Used a medkit. Health: " + health;
    }
    public synchronized String status() {
        return "Health: " + health + " | Zombies: " + zombies.size() + " | Ammo: " + ammunition
            + " | Medkits: " + medkits + " | Score: " + score;
    }
}
