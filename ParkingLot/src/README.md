# Parking Lot — LLD

---

## Requirements

- 3 types of vehicles: **Bike**, **Car**, **Bus**
- Vehicles can enter the parking lot and get a **Ticket**
- Vehicle is parked at the **nearest open spot** that can fit its size
- Parking lot supports **multiple levels**, each with multiple spots
- On exit — the spot is freed and a **fee is charged** at the gate
- Fee strategy: **flat rate** based on vehicle size and hours parked
- Fee strategy is **swappable** at runtime without changing core logic

**Out of scope:**
- UI / rendering
- Payment gateway
- Reservations
- Real-time tracking

---

## Entities

| Entity | Role |
|---|---|
| `Vehicle` | Base class — holds `licensePlate` and `size` |
| `Bike`, `Car`, `Bus` | Subtypes of Vehicle — set size to SMALL / MEDIUM / LARGE |
| `VehicleSize` | Enum — SMALL, MEDIUM, LARGE |
| `ParkingSpot` | Holds spot id, size, occupied flag, parked vehicle |
| `ParkingLevel` | Holds a list of spots. Finds first available spot for a vehicle |
| `Ticket` | Issued on entry — holds vehicle, spot, levelNumber, entryTime |
| `FeeStrategy` | Interface — `calculateFee(ticket)` |
| `FlatRateStrategy` | Implements FeeStrategy — charges per hour by vehicle size |
| `ParkingLot` | **Orchestrator (Singleton)** — manages entry, exit, fee collection |

---

## Relationships

```
ParkingLot
  └── has a List<ParkingLevel>
  └── has a FeeStrategy
  └── has a ConcurrentHashMap<licensePlate, Ticket>  (activeTickets)

ParkingLevel
  └── has a List<ParkingSpot>

ParkingSpot
  └── has a VehicleSize  (what size vehicle fits here)
  └── has a Vehicle?     (null when free)

Ticket
  └── has a Vehicle
  └── has a ParkingSpot
  └── has levelNumber (int)
  └── has entryTime (long)
  └── has exitTime  (long, stamped on exit)
```

---

## Class Design

### `VehicleSize`
```java
enum VehicleSize {
    SMALL,    // Bike
    MEDIUM,   // Car
    LARGE     // Bus
}
```

### `Vehicle`
```java
abstract class Vehicle {
    - licensePlate : String
    - size         : VehicleSize

    + getLicensePlate() -> String
    + getSize()         -> VehicleSize
}

class Bike extends Vehicle   // size = SMALL
class Car  extends Vehicle   // size = MEDIUM
class Bus  extends Vehicle   // size = LARGE
```

### `ParkingSpot`
```java
class ParkingSpot {
    - spotId        : String        (final)
    - spotSize      : VehicleSize   (final)
    - occupied      : boolean
    - parkedVehicle : Vehicle?

    + getSpotId()         -> String
    + getSpotSize()       -> VehicleSize
    + isAvailable()       -> boolean    (synchronized)
    + isOccupied()        -> boolean
    + getParkedVehicle()  -> Vehicle?
    + park(vehicle)                     (synchronized)
    + unpark()                          (synchronized)
    + canFitVehicle(vehicle) -> boolean
}
```

**Flexible spot matching in `canFitVehicle`:**
```
SMALL  vehicle → only fits SMALL spot
MEDIUM vehicle → fits MEDIUM or LARGE spot
LARGE  vehicle → only fits LARGE spot
```
If spot is already occupied → always returns false.

### `ParkingLevel`
```java
class ParkingLevel {
    - levelNumber : int
    - spots       : List<ParkingSpot>

    + getLevelNumber()              -> int
    + getSpots()                    -> List<ParkingSpot>
    + findAvailableSpot(vehicle)    -> ParkingSpot?   // linear scan
    + hasAvailableSpot(vehicle)     -> boolean
}
```

### `Ticket`
```java
class Ticket {
    - id          : String     (final, UUID)
    - vehicle     : Vehicle    (final)
    - spot        : ParkingSpot (final)
    - levelNumber : int        (final)
    - entryTime   : long       (final, set at construction)
    - exitTime    : long       (set on exit via stampExitTime())

    + getId()           -> String
    + getVehicle()      -> Vehicle
    + getSpot()         -> ParkingSpot
    + getLevelNumber()  -> int
    + getEntryTime()    -> long
    + getExitTime()     -> long
    + stampExitTime()           // called before fee calculation
}
```

### `FeeStrategy`
```java
interface FeeStrategy {
    + calculateFee(ticket: Ticket) -> double
}
```

### `FlatRateStrategy`
```java
class FlatRateStrategy implements FeeStrategy {
    - ratePerHour : Map<VehicleSize, Double>
        // SMALL  → 10.0
        // MEDIUM → 20.0
        // LARGE  → 40.0

    + calculateFee(ticket) -> double
}
```

### `ParkingLot` (Singleton orchestrator)
```java
class ParkingLot {
    - instance      : ParkingLot           (static)
    - levels        : List<ParkingLevel>
    - feeStrategy   : FeeStrategy
    - activeTickets : ConcurrentHashMap<String, Ticket>  // licensePlate → Ticket

    + getInstance()                -> ParkingLot   (static, synchronized)
    + addLevel(level)
    + setFeeStrategy(strategy)
    + enter(vehicle)               -> Ticket        // null if full
    + exit(vehicle)                -> double        // fee charged
    + hasAvailableSpot(vehicle)    -> boolean
}
```

---

## Implementation

### `ParkingLevel.findAvailableSpot`
```
findAvailableSpot(vehicle) -> ParkingSpot?
    for each spot in spots:
        if spot.canFitVehicle(vehicle):
            return spot
    return null
```
Linear scan — spots are stored in order, so the first match is always the nearest.

---

### `ParkingLot.enter`
```
enter(vehicle) -> Ticket
    for each level in levels:
        spot = level.findAvailableSpot(vehicle)

        if spot != null:
            spot.park(vehicle)
            ticket = new Ticket(vehicle, spot, level.getLevelNumber())
            activeTickets.put(vehicle.getLicensePlate(), ticket)
            return ticket

    print "No available spot"
    return null
```
Levels are scanned in order — level 1 first. First level with a fitting spot wins.

---

### `ParkingLot.exit`
```
exit(vehicle) -> double
    ticket = activeTickets.get(vehicle.getLicensePlate())
    if ticket == null:
        throw IllegalStateException

    spot = ticket.getSpot()
    spot.unpark()                       // free the spot

    fee = feeStrategy.calculateFee(ticket)  // stamps exitTime internally

    activeTickets.remove(vehicle.getLicensePlate())
    return fee
```

---

### `FlatRateStrategy.calculateFee`
```
calculateFee(ticket) -> double
    stamp exitTime on ticket if not already stamped

    durationMs  = exitTime - entryTime
    hoursParked = ceil(durationMs / 3_600_000)   // round up to next hour
    hoursParked = max(hoursParked, 1)             // minimum 1 hour

    rate = ratePerHour.get(ticket.vehicle.size)
    return hoursParked * rate

    // e.g. CAR parked 2.5 hrs → ceil(2.5) = 3 hrs × 20 = 60.0
```

---

## Key Design Decisions

**Flexible spot matching** — A `MEDIUM` vehicle can park in a `LARGE` spot if no `MEDIUM` spot is free. This logic lives in `ParkingSpot.canFitVehicle()` — not in `ParkingLot` or `ParkingLevel`. The rule belongs to the spot.

**Singleton** — `ParkingLot.getInstance()` is `synchronized`. Only one instance across the whole app.

**`ConcurrentHashMap` for `activeTickets`** — Safe for concurrent entry/exit without locking the whole map.

**`synchronized` on spot methods** — `park()`, `unpark()`, `isAvailable()` are all `synchronized`. Two threads cannot claim or free the same spot simultaneously.

**`licensePlate` as key** — `activeTickets` is keyed by `licensePlate`, not `ticketId`. Exit only needs the vehicle — no ticket ID to remember.

**Exit time stamped on ticket** — `Ticket.stampExitTime()` is called inside `FlatRateStrategy.calculateFee()` if not already set. The ticket becomes a complete record of the full parking session.

**`FeeStrategy` is an interface** — Swap to surge pricing or weekend rates at runtime via `setFeeStrategy()` without touching any other class.

---

## Package Structure

```
src/
  Main.java
  model/
    VehicleSize.java
    Vehicle.java
    Bike.java
    Car.java
    Bus.java
    ParkingSpot.java
    ParkingLevel.java
    Ticket.java
  strategy/
    FeeStrategy.java
    FlatRateStrategy.java
  service/
    ParkingLot.java
```

---


## Extensibility

**Add a new vehicle type (e.g. Truck)**
- Add `EXTRA_LARGE` to `VehicleSize`
- Create `Truck extends Vehicle` with size `EXTRA_LARGE`
- Add `EXTRA_LARGE → rate` to `FlatRateStrategy`
- Add `EXTRA_LARGE` spots to levels
- Zero changes to `ParkingLot`, `ParkingLevel`, or `enter/exit` logic


**Add more levels**
- Call `lot.addLevel(new ParkingLevel(3, spots))`
- `enter()` already iterates all levels — picks up the new one automatically