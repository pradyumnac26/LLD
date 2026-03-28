import model.*;
import service.ParkingLot;

import java.util.Arrays;

public class Main {
    public static void main(String[] args) throws InterruptedException {

        // -------------------------------------------------------
        // SETUP — get singleton, add levels
        // Level 1: 2 SMALL, 2 MEDIUM, 1 LARGE
        // Level 2: 1 SMALL, 2 MEDIUM, 1 LARGE
        // -------------------------------------------------------
        ParkingLevel level1 = new ParkingLevel(1, Arrays.asList(
                new ParkingSpot("L1-S1", VehicleSize.SMALL),
                new ParkingSpot("L1-S2", VehicleSize.SMALL),
                new ParkingSpot("L1-M1", VehicleSize.MEDIUM),
                new ParkingSpot("L1-M2", VehicleSize.MEDIUM),
                new ParkingSpot("L1-L1", VehicleSize.BIG)
        ));

        ParkingLevel level2 = new ParkingLevel(2, Arrays.asList(
                new ParkingSpot("L2-S1", VehicleSize.SMALL),
                new ParkingSpot("L2-M1", VehicleSize.MEDIUM),
                new ParkingSpot("L2-M2", VehicleSize.MEDIUM),
                new ParkingSpot("L2-L1", VehicleSize.BIG)
        ));

        ParkingLot lot = ParkingLot.getInstance();
        lot.addLevel(level1);
        lot.addLevel(level2);

        // -------------------------------------------------------
        // ENTRY
        // -------------------------------------------------------
        System.out.println("========== ENTRY ==========\n");

        Vehicle bike1 = new Bike("MH-01");
        Vehicle car1  = new Car("KA-02");
        Vehicle bus1  = new Bus("DL-03");
        Vehicle car2  = new Car("TN-04");

        lot.enter(bike1);   // SMALL  → L1-S1
        lot.enter(car1);    // MEDIUM → L1-M1
        lot.enter(bus1);    // LARGE  → L1-L1
        lot.enter(car2);    // MEDIUM → L1-M2

        // -------------------------------------------------------
        // FLEXIBLE MATCHING — no MEDIUM left on L1, car spills to L2
        // -------------------------------------------------------
        System.out.println("\n========== FLEXIBLE MATCHING ==========\n");

        Vehicle car3 = new Car("GJ-05");
        lot.enter(car3);    // L1 MEDIUM full → tries L2 → L2-M1

        // -------------------------------------------------------
        // simulate time parked
        // -------------------------------------------------------
        Thread.sleep(2000);

        // -------------------------------------------------------
        // EXIT — pass the vehicle object
        // -------------------------------------------------------
        System.out.println("\n========== EXIT ==========\n");

        lot.exit(car1);    // KA-02 exits — fee = 20.0 (min 1 hr)
        lot.exit(bike1);   // MH-01 exits — fee = 10.0
        lot.exit(bus1);    // DL-03 exits — fee = 40.0

        // -------------------------------------------------------
        // RE-ENTRY — L1-M1 and L1-L1 now free
        // -------------------------------------------------------
        System.out.println("\n========== RE-ENTRY ==========\n");

        lot.enter(new Car("KA-02"));   // should get L1-M1 again

        // -------------------------------------------------------
        // NO SPOT TEST
        // -------------------------------------------------------
        System.out.println("\n========== NO SPOT TEST ==========\n");

        lot.enter(new Car("AA-01"));   // L1-M2 taken, tries L2 → L2-M2
        lot.enter(new Car("AA-02"));   // all MEDIUM full, spills to LARGE L2-L1
        lot.enter(new Car("AA-03"));   // all MEDIUM and LARGE full → no spot

        // -------------------------------------------------------
        // INVALID EXIT TEST
        // -------------------------------------------------------
        System.out.println("\n========== INVALID EXIT TEST ==========\n");

        try {
            lot.exit(new Car("FAKE-99"));   // no ticket → exception
        } catch (IllegalStateException e) {
            System.out.println("CAUGHT: " + e.getMessage());
        }
    }
}