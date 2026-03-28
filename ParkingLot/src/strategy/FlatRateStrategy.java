package strategy;

import model.Ticket;
import model.VehicleSize;

import java.util.EnumMap;
import java.util.Map;

public class FlatRateStrategy implements FeeStrategy {

    // rate per hour per vehicle size
    private final Map<VehicleSize, Double> ratePerHour;

    public FlatRateStrategy() {
        ratePerHour = new EnumMap<>(VehicleSize.class);
        ratePerHour.put(VehicleSize.SMALL,  10.0);   // Bike
        ratePerHour.put(VehicleSize.MEDIUM, 20.0);   // Car
        ratePerHour.put(VehicleSize.BIG,  40.0);   // Bus
    }

    @Override
    public double calculateFee(Ticket ticket) {
        long durationMs = ticket.getExitTime() - ticket.getEntryTime();

        // round up to next hour — partial hours charged as full
        long hoursParked = (long) Math.ceil(durationMs / 3_600_000.0);

        // minimum 1 hour
        hoursParked = Math.max(hoursParked, 1);

        double rate = ratePerHour.get(ticket.getVehicle().getSize());
        return hoursParked * rate;
    }
}