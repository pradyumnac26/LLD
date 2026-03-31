package models;

public enum TaskPriority {
    LOW (0),
    MID (1),
    HIGH(2);

    private int value;
     TaskPriority(int value) {
        this.value = value;
    }

    public int getValue() {
        return value;
    }
}
