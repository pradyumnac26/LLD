package models;

import java.time.LocalDateTime;
import java.util.List;

public class Meeting {
    private String id;
    private String title;
    private User organizer;
    private List<User> invitees;
    private LocalDateTime startTime;
    private LocalDateTime endTime;
    private MeetingRoom room;

    public Meeting(String id, String title, User organizer, List<User> invitees, LocalDateTime startTime, LocalDateTime endTime, MeetingRoom room) {
        this.id = id;
        this.title = title;
        this.organizer = organizer;
        this.invitees = invitees;
        this.startTime = startTime;
        this.endTime = endTime;
        this.room = room;
    }

    public String getId() {
        return id;
    }

    public String getTitle() {
        return title;
    }

    public User getOrganizer() {
        return organizer;
    }

    public List<User> getInvitees() {
        return invitees;
    }

    public LocalDateTime getStartTime() {
        return startTime;
    }

    public LocalDateTime getEndTime() {
        return endTime;
    }

    public MeetingRoom getRoom() {
        return room;
    }
}
