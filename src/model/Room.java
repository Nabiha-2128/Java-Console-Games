package model;

public final class Room {
    private final int number;
    private final boolean treasure;
    private boolean searched;
    public Room(int number, boolean treasure) { this.number = number; this.treasure = treasure; }
    public int getNumber() { return number; }
    public boolean hasTreasure() { return treasure; }
    public boolean isSearched() { return searched; }
    public void search() { searched = true; }
}
