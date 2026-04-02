# Meeting Scheduler — LLD

---

## Requirements

- N meeting rooms — each has a name and capacity.
- Book a meeting in any available room for a given time interval (start time, end time) and required capacity.
- A room cannot be double-booked — overlapping bookings for the same room are rejected.
- Send a notification to all invited persons when a meeting is booked.
- A Meeting Room Calendar tracks all meetings for a room on the current day.
- Query a room's calendar — list all meetings scheduled for today.
- Thread-safe — concurrent bookings for the same room handled safely.

**Out of scope:**
- Recurring meetings
- Cancellation
- Multi-day bookings
- Authentication
- Equipment booking

---

## Entities

| Entity | Role |
|---|---|
| `MeetingScheduler` | Orchestrator — books meetings, finds available rooms, sends notifications |
| `MeetingRoom` | Has name, capacity, and its own Calendar. Checks for conflicts. |
| `Meeting` | Holds title, organiser, invitees, start/end time, assigned room |
| `Calendar` | Belongs to a MeetingRoom. Stores today's meetings. Checks for time conflicts. |
| `Person` | Has name and email. Can be organiser or invitee. |
| `NotificationService` | Interface — notifies invitees on booking. EmailNotificationService implements it. |

---

## Relationships

```
MeetingScheduler
  └── List<MeetingRoom>
  └── NotificationService

MeetingRoom
  └── Calendar (one per room)

Calendar
  └── List<Meeting>  (today's meetings)

Meeting
  └── Person          (organiser)
  └── List<Person>    (invitees)
  └── MeetingRoom     (assigned room)
  └── startTime, endTime
```

---

## Class Design

### Class: Person
```java
class Person:
  - id    : String  (final, UUID)
  - name  : String  (final)
  - email : String  (final)

  + getId()    -> String
  + getName()  -> String
  + getEmail() -> String
```

### Class: Meeting
```java
class Meeting:
  - id        : String          (final, UUID)
  - title     : String          (final)
  - organiser : Person          (final)
  - invitees  : List<Person>    (final)
  - room      : MeetingRoom     (final)
  - startTime : LocalDateTime   (final)
  - endTime   : LocalDateTime   (final)

  + getId()        -> String
  + getTitle()     -> String
  + getOrganiser() -> Person
  + getInvitees()  -> List<Person>
  + getRoom()      -> MeetingRoom
  + getStartTime() -> LocalDateTime
  + getEndTime()   -> LocalDateTime
```

### Class: Calendar
```java
class Calendar:
  - meetings : List<Meeting>   // today's meetings

  + addMeeting(meeting)
  + getTodaysMeetings()                   -> List<Meeting>
  + hasConflict(startTime, endTime)       -> boolean
```

### Class: MeetingRoom
```java
class MeetingRoom:
  - id       : String    (final)
  - name     : String    (final)
  - capacity : int       (final)
  - calendar : Calendar  (final)

  + getId()       -> String
  + getName()     -> String
  + getCapacity() -> int
  + getCalendar() -> Calendar
  + isAvailable(startTime, endTime) -> boolean
```

### Interface: NotificationService
```java
interface NotificationService:
  + notify(meeting: Meeting)
```

### Class: EmailNotificationService
```java
class EmailNotificationService implements NotificationService:
  + notify(meeting: Meeting)
      // sends email to organiser + all invitees
```

### Class: MeetingScheduler (orchestrator)
```java
class MeetingScheduler:
  - rooms               : List<MeetingRoom>
  - notificationService : NotificationService

  + addRoom(room)
  + bookMeeting(title, organiser, invitees,
                startTime, endTime,
                requiredCapacity)    -> Meeting
  + getMeetingsForRoom(roomId)       -> List<Meeting>
  + getAvailableRooms(startTime,
                      endTime,
                      capacity)      -> List<MeetingRoom>
```

---

## Implementation

### Calendar.hasConflict
```
hasConflict(startTime, endTime) -> boolean
    for each meeting in meetings:
        // two intervals overlap if start1 < end2 AND start2 < end1
        if meeting.getStartTime().isBefore(endTime)
            and startTime.isBefore(meeting.getEndTime()):
            return true
    return false
```
> Back-to-back meetings (one ends at 9:30, next starts at 9:30) do NOT conflict —
> `startTime.isBefore(endTime)` is false when equal.

---

### Calendar.getTodaysMeetings
```
getTodaysMeetings() -> List<Meeting>
    today = LocalDate.now()
    return meetings filtered by startTime.toLocalDate() == today
```

---

### MeetingRoom.isAvailable
```
isAvailable(startTime, endTime) -> boolean
    return !calendar.hasConflict(startTime, endTime)
```

---

### MeetingScheduler.bookMeeting — core method
```
bookMeeting(title, organiser, invitees,
            startTime, endTime, requiredCapacity) -> Meeting

    for each room in rooms:
        if room.getCapacity() < requiredCapacity: continue

        synchronized(room)
            // re-check inside lock — prevents race condition
            if !room.isAvailable(startTime, endTime): continue

            meeting = new Meeting(
                id        = UUID.generate(),
                title     = title,
                organiser = organiser,
                invitees  = invitees,
                room      = room,
                startTime = startTime,
                endTime   = endTime
            )

            room.getCalendar().addMeeting(meeting)
            notificationService.notify(meeting)
            return meeting

    throw NoRoomAvailableException
```
> `synchronized(room)` — not globally. Two meetings in different rooms
> can be booked simultaneously. Only threads competing for the same room block each other.

---

### MeetingScheduler.getAvailableRooms
```
getAvailableRooms(startTime, endTime, capacity) -> List<MeetingRoom>
    return rooms filtered by:
        room.getCapacity() >= capacity
        AND room.isAvailable(startTime, endTime)
```

---

### MeetingScheduler.getMeetingsForRoom
```
getMeetingsForRoom(roomId) -> List<Meeting>
    room = find room by id, throw if not found
    return room.getCalendar().getTodaysMeetings()
```

---

### EmailNotificationService.notify
```
notify(meeting)
    allPersons = [organiser] + invitees
    for each person in allPersons:
        send email to person.getEmail() with meeting details
```

---

## Verification — trace a full booking flow

| Step | Operation | Result |
|---|---|---|
| 0 | `bookMeeting("Standup", alice, [bob,carol], 9am, 9:30am, 3)` | Room A (cap=5) available → booked. Email sent to alice, bob, carol. |
| 1 | `bookMeeting("Design Review", dave, [alice], 9am, 10am, 2)` | Room A conflicts (9-9:30 overlaps 9-10). Room B available → booked. |
| 2 | `bookMeeting("Sprint", eve, [], 9:30am, 10am, 2)` | Room A — Standup ends 9:30, new starts 9:30 → no conflict → booked. |
| 3 | Two threads book same room simultaneously | `synchronized(room)` → one wins, one retries next room or throws. |
| 4 | `bookMeeting` with requiredCapacity=10, all rooms cap≤5 | No room passes capacity filter → NoRoomAvailableException. |
| 5 | `getMeetingsForRoom(roomAId)` | Returns [Standup, Sprint] for today. |

---

## Key Design Decisions

**`Calendar` belongs to `MeetingRoom`, not `MeetingScheduler`.** The room knows its own schedule. If you put all bookings in one central map inside the scheduler, the room can't answer "am I available?" without asking the scheduler back. Keeping `Calendar` on the room keeps logic where the data lives.

**Overlap formula.** `[s1, e1)` and `[s2, e2)` overlap if and only if `s1 < e2 AND s2 < e1`. Back-to-back meetings do not trigger this — correct behavior, no gap needed.

**`synchronized(room)` not `synchronized(this)`.** Locking globally serializes all bookings. Locking per room means Room A and Room B can be booked simultaneously — only competing threads for the same room block each other.

**`NotificationService` as an interface.** The scheduler never knows if it's sending emails, SMS, or Slack. Swapping notification channels is zero changes to the scheduler.

---

## Package Structure

```
src/
  Main.java
  model/
    Person.java
    Meeting.java
    Calendar.java
    MeetingRoom.java
  notification/
    NotificationService.java
    EmailNotificationService.java
  service/
    MeetingScheduler.java
  exception/
    NoRoomAvailableException.java
    RoomNotFoundException.java
```

---

## How to Run

```bash
javac -d out -sourcepath src $(find src -name "*.java")
java -cp out Main
```

---

## Extensibility

**Add SMS notifications alongside email**
Implement `SmsNotificationService implements NotificationService`. Or introduce a `CompositeNotificationService` that holds a list and calls both. `MeetingScheduler` is unchanged.

**Support cancellation**
Add `cancelMeeting(meetingId)` to `MeetingScheduler`. It removes the meeting from `room.getCalendar()` and notifies invitees. Zero structural changes.

**Multi-day calendar view**
Change `getTodaysMeetings()` to `getMeetingsForDate(date: LocalDate)` — same filter, parameterized. Pass the date from the scheduler.

**Room selection preference (prefer smallest fitting room)**
Extract the room selection loop into a `RoomSelectionStrategy` interface with `selectRoom(rooms, startTime, endTime, capacity) → MeetingRoom?`. `FirstAvailableStrategy` is what we have now. `SmallestFitStrategy` picks the room with the least wasted capacity.