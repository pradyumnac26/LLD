package model;

import java.util.UUID;

public class Ticket {
    private final String id;
    private final Vehicle vehicle;
    private final ParkingSpot spot;
    private final int levelNumber;
    private final long entryTime;
    private long exitTime;     // stamped on exit

    public Ticket(Vehicle vehicle, ParkingSpot spot, int levelNumber) {
        this.id = UUID.randomUUID().toString();
        this.vehicle = vehicle;
        this.spot = spot;
        this.levelNumber = levelNumber;
        this.entryTime = System.currentTimeMillis();
    }

    public String getId() {
        return id;
    }

    public Vehicle getVehicle() {
        return vehicle;
    }

    public ParkingSpot getSpot() {
        return spot;
    }

    public int getLevelNumber() {
        return levelNumber;
    }

    public long getEntryTime() {
        return entryTime;
    }

    public long getExitTime() {
        return exitTime;
    }

    // called by ParkingLot.exit() before fee calculation
    public void stampExitTime() {
        this.exitTime = System.currentTimeMillis();
    }
}