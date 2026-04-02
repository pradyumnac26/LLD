import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;
import models.*;

public class Main {
    public static void main(String[] args) {
        NotificationService notificationService = new EmailService();
        MeetingScheduler scheduler = new MeetingScheduler(notificationService);

        MeetingRoom room1 = new MeetingRoom("R1", "Alpha", 4);
        MeetingRoom room2 = new MeetingRoom("R2", "Beta", 8);
        MeetingRoom room3 = new MeetingRoom("R3", "Gamma", 12);

        scheduler.addRooms(room1);
        scheduler.addRooms(room2);
        scheduler.addRooms(room3);

        User organizer = new User("U1", "Pradyumna", "pradyumna@gmail.com");
        User u2 = new User("U2", "Rahul", "rahul@gmail.com");
        User u3 = new User("U3", "Sneha", "sneha@gmail.com");
        User u4 = new User("U4", "Amit", "amit@gmail.com");

        System.out.println("===== AVAILABLE ROOMS BEFORE BOOKING =====");
        List<MeetingRoom> availableRooms = scheduler.getAvailableRooms(
                LocalDateTime.of(2026, 4, 2, 10, 0),
                LocalDateTime.of(2026, 4, 2, 11, 0),
                3
        );
        for (MeetingRoom room : availableRooms) {
            System.out.println(room.getName() + " | capacity = " + room.getCapacity());
        }

        System.out.println("\n===== BOOKING MEETING 1 =====");
        Meeting meeting1 = scheduler.bookMeetings(
                "Design Discussion",
                organizer,
                Arrays.asList(u2, u3),
                LocalDateTime.of(2026, 4, 2, 10, 0),
                LocalDateTime.of(2026, 4, 2, 11, 0),
                3
        );

        System.out.println("Booked meeting: " + meeting1.getTitle());
        System.out.println("Room: " + meeting1.getRoom().getName());
        System.out.println("Start: " + meeting1.getStartTime());
        System.out.println("End: " + meeting1.getEndTime());

        System.out.println("\n===== TRY BOOKING OVERLAPPING MEETING =====");
        try {
            Meeting meeting2 = scheduler.bookMeetings(
                    "Backend Sync",
                    organizer,
                    Arrays.asList(u2, u3, u4),
                    LocalDateTime.of(2026, 4, 2, 10, 30),
                    LocalDateTime.of(2026, 4, 2, 11, 30),
                    4
            );

            System.out.println("Booked meeting: " + meeting2.getTitle());
            System.out.println("Room: " + meeting2.getRoom().getName());
        } catch (RuntimeException e) {
            System.out.println("Booking failed: " + e.getMessage());
        }

        System.out.println("\n===== BOOKING NON-OVERLAPPING MEETING =====");
        Meeting meeting3 = scheduler.bookMeetings(
                "Sprint Planning",
                organizer,
                Arrays.asList(u2, u3, u4),
                LocalDateTime.of(2026, 4, 2, 11, 0),
                LocalDateTime.of(2026, 4, 2, 12, 0),
                4
        );

        System.out.println("Booked meeting: " + meeting3.getTitle());
        System.out.println("Room: " + meeting3.getRoom().getName());

        System.out.println("\n===== MEETINGS FOR ROOM R1 =====");
        try {
            List<Meeting> meetingsInRoom = scheduler.getMeetingsForRoom("R1");
            for (Meeting meeting : meetingsInRoom) {
                System.out.println(
                        meeting.getTitle() + " | " +
                                meeting.getStartTime() + " -> " +
                                meeting.getEndTime()
                );
            }
        } catch (RuntimeException e) {
            System.out.println(e.getMessage());
        }

        System.out.println("\n===== AVAILABLE ROOMS AFTER BOOKINGS =====");
        List<MeetingRoom> roomsAfterBooking = scheduler.getAvailableRooms(
                LocalDateTime.of(2026, 4, 2, 10, 30),
                LocalDateTime.of(2026, 4, 2, 11, 30),
                3
        );
        for (MeetingRoom room : roomsAfterBooking) {
            System.out.println(room.getName() + " | capacity = " + room.getCapacity());
        }
    }
}