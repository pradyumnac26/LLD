package models;

public enum TaskStatus {
    TODO(0),
    IN_PROGRESS(1),
    DONE(2);

    TaskStatus(int value) {
        this.value = value;
    }

    private int value;
    public int getValue() {
        return value;
    }


}
