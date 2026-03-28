package model;

public class ParkingSpot {
    private final String spotId;
    private final VehicleSize spotSize;
    private boolean occupied;
    private Vehicle parkedVehicle;

    public ParkingSpot(String spotId, VehicleSize spotSize) {
        this.spotId = spotId;
        this.spotSize = spotSize;
        this.occupied = false;
        this.parkedVehicle = null;
    }
public String getSpotId() {
        return spotId;
}

public VehicleSize getSpotSize() {
        return spotSize;
}

public synchronized boolean isAvailable() {
        return !occupied;
    }

    public boolean isOccupied() {
        return occupied;
    }

    public Vehicle getParkedVehicle() {
        return parkedVehicle;
    }

    public synchronized void park(Vehicle vehicle){
        occupied = true;
        parkedVehicle = vehicle;
    }

    public synchronized  void unpark(){
        occupied = false;
        parkedVehicle = null;
    }

    public boolean canFitVehicle(Vehicle vehicle) {
        if (occupied) return false;

        switch (vehicle.getSize()) {
            case SMALL:
                return spotSize == VehicleSize.SMALL;
            case MEDIUM:
                return spotSize == VehicleSize.MEDIUM;
            case BIG:
                return spotSize == VehicleSize.BIG;
            default:
                return false;
        }
    }
    }