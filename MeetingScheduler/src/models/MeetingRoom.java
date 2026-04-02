package models;

import java.time.LocalDateTime;

public class MeetingRoom {
    private String roomId ;
    private String name;
    private int capacity;
    private MeetingCalender calender;
    public MeetingRoom(String roomId,String name, int capacity) {
        this.roomId = roomId;
        this.name = name;
        this.capacity = capacity;
        this.calender = new MeetingCalender();
    }

    public String getName() {
        return name;
    }

    public String getRoomId() {
        return roomId;
    }

    public int getCapacity() {
        return capacity;
    }

    public MeetingCalender getCalender() {
        return calender;
    }

    public boolean isAvailable(LocalDateTime startTime, LocalDateTime endTime) {
        if (calender.hasConflict(startTime, endTime)) {
            return false;
        }
        else {
            return true;
        }
    }
}
