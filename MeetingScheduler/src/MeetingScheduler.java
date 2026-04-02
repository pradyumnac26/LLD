import models.Meeting;
import models.MeetingRoom;
import models.NotificationService;
import models.User;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

public class MeetingScheduler {
    private List<MeetingRoom> rooms = new ArrayList<>();
    NotificationService notificationService;

    public MeetingScheduler(NotificationService notificationService) {
        this.notificationService = notificationService;
    }

    public void addRooms(MeetingRoom room){
        rooms.add(room);

    }

    public Meeting bookMeetings(String title, User organizer, List<User> invitees, LocalDateTime startTime, LocalDateTime endTime, int requiredCapacity) {
        for (MeetingRoom room : rooms) {
            if (room.getCapacity() < requiredCapacity) {
                continue;
            }
            synchronized (room) {
                if (!room.isAvailable(startTime, endTime)) {
                    continue;
                }
                Meeting bookMeeting = new Meeting(
                        UUID.randomUUID().toString(),
                        title,
                        organizer,
                        invitees,
                        startTime,
                        endTime,
                        room
                );
                room.getCalender().addMeetings(bookMeeting);
                notificationService.send(bookMeeting);
                return bookMeeting;

            }


        }
        throw new RuntimeException("No room available for the given slot");
    }
    public List<MeetingRoom> getAvailableRooms(LocalDateTime startTime,
                                               LocalDateTime endTime,
                                               int capacity) {
        return rooms.stream()
                .filter(r -> r.getCapacity() >= capacity)
                .filter(r -> r.isAvailable(startTime, endTime))
                .collect(Collectors.toList());
    }

    public List<Meeting> getMeetingsForRoom(String roomId) {
        for (MeetingRoom room : rooms) {
            if (room.getRoomId().equals(roomId)) {
                return room.getCalender().getMeetings();
            }
        }
        throw new RuntimeException("Room not found");
    }

}
