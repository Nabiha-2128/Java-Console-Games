package model;

import java.util.Map;

public final class SnakeBoard {
    private final Map<Integer, Integer> jumps = Map.ofEntries(
        Map.entry(4, 14), Map.entry(9, 31), Map.entry(20, 38), Map.entry(28, 84),
        Map.entry(40, 59), Map.entry(63, 81), Map.entry(71, 91),
        Map.entry(17, 7), Map.entry(54, 34), Map.entry(62, 19),
        Map.entry(64, 60), Map.entry(87, 24), Map.entry(93, 73), Map.entry(99, 78));
    public int move(int position, int roll) {
        if (position < 0 || position > 100 || roll < 1 || roll > 6) throw new IllegalArgumentException("Invalid move");
        int landing = position + roll;
        return landing > 100 ? position : jumps.getOrDefault(landing, landing);
    }
}
