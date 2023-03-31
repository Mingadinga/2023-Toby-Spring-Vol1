package springbook.user.dao;

public enum Level {

    BASIC(1), SILVER(2), GOLD(3);
    private final int value;
    private Level next;

    static {
        BASIC.next = SILVER;
        SILVER.next = GOLD;
        GOLD.next = null;
    }

    Level(int value) {
        this.value = value;
    }

    public int intValue() {
        return this.value;
    }

    public Level nextLevel() {
        return this.next;
    }

    public static Level valueOf(int value) {
        switch (value) {
            case 1: return BASIC;
            case 2: return SILVER;
            case 3: return GOLD;
            default: throw new AssertionError("Unknown value: " + value);
        }
    }

}
