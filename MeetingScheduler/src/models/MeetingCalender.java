package models;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

public class MeetingCalender {
    private List<Meeting> meetings;

    public MeetingCalender() {
        this.meetings = new ArrayList<>();
    }

    public List<Meeting> getMeetings() {
        return meetings;
    }
    public void addMeetings(Meeting meeting) {
        meetings.add(meeting);
    }
    public void cancelMeetings(Meeting meeting) {
        meetings.remove(meeting);
    }
    public List<Meeting> getTodaysMeetings() {
        LocalDate today = LocalDate.now();
        List<Meeting> filtered = meetings.stream().filter(m -> m.getStartTime().toLocalDate().equals(today)).collect(Collectors.toList());
        return filtered;
    }

    public boolean hasConflict(LocalDateTime startTime, LocalDateTime endTime) {
        for (Meeting meeting : meetings) {
            if (meeting.getStartTime().isBefore(endTime) && startTime.isBefore(meeting.getEndTime())) {
                return true;
            }


        }
        return false;

    }
}
