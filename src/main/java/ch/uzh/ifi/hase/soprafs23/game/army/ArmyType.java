package ch.uzh.ifi.hase.soprafs23.game.army;

public enum ArmyType {
    RED("Red"),
    BLUE("Blue");
    private final String name;
    ArmyType(String name) { this.name = name; }
    public String getName() { return this.name; }
}
