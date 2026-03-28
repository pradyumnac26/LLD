package model;
import java.util.List;
import java.util.Optional;

public class ParkingLevel {
    private final int levelNumber;

    private final List<ParkingSpot> spots;

    public ParkingLevel(int levelNumber, List<ParkingSpot> spots ) {
        this.levelNumber = levelNumber;
        this.spots = spots;
    }

    public int getLevelNumber() {
        return levelNumber;
    }

    public List<ParkingSpot> getSpots() {
        return spots;
    }

    public ParkingSpot findAvailableSpot(Vehicle vehicle) {
        for (ParkingSpot spot : spots) {
            if (spot.canFitVehicle(vehicle)) {
                return spot;
            }
        }
        return null;
    }

}
