package service;

import model.ParkingLevel;
import model.ParkingSpot;
import model.Ticket;
import model.Vehicle;
import strategy.FeeStrategy;
import strategy.FlatRateStrategy;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;

import model.ParkingLevel;

public class ParkingLot {

    private static ParkingLot instance;
    private  List<ParkingLevel> levels;

    private FeeStrategy feeStrategy;

    private final ConcurrentHashMap<String, Ticket> activeTickets;
    private ParkingLot() {
        this.levels = new ArrayList<>();
        this.feeStrategy = new FlatRateStrategy();
        this.activeTickets = new ConcurrentHashMap<>();
    }
    public static synchronized ParkingLot getInstance() {
        if (instance == null) {
            instance = new ParkingLot();
        }
        return instance;
    }
    public void addLevel(ParkingLevel level) {
        levels.add(level);
    }

    public void setFeeStrategy(FeeStrategy feeStrategy) {
        this.feeStrategy = feeStrategy;
    }

    public Ticket enter(Vehicle vehicle) {
        for (ParkingLevel level : levels) {
            ParkingSpot availableSpot = level.findAvailableSpot(vehicle);

            if (availableSpot != null) {
                availableSpot.park(vehicle);

                Ticket t = new Ticket(vehicle, availableSpot, level.getLevelNumber());
                activeTickets.put(vehicle.getLicensePlate(), t);

                System.out.println("ENTRY | " + t);

                return t;
            }
        }

        System.out.println("No available spot for vehicle: " + vehicle.getLicensePlate());
        return null;
    }

    public double exit(Vehicle vehicle) {
        Ticket ticket = activeTickets.get(vehicle.getLicensePlate());

        if (ticket == null) {
            throw new IllegalStateException("No active ticket found for vehicle");
        }

        ParkingSpot spot = ticket.getSpot();
        spot.unpark();

        double fee = feeStrategy.calculateFee(ticket);

        activeTickets.remove(vehicle.getLicensePlate());

        System.out.println("EXIT | " + ticket + " | Fee: " + fee);

        return fee;
    }





}
