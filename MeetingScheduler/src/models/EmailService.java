package models;

import java.sql.SQLOutput;
import java.util.ArrayList;
import java.util.List;

public class EmailService implements NotificationService {
    public void send(Meeting meeting) {
        List<User> allAttendees = new ArrayList<>();
        allAttendees.add(meeting.getOrganizer());
        allAttendees.addAll(meeting.getInvitees());
        for (User u : allAttendees) {
            System.out.println(
                    "EMAIL to " + u.getEmail()
                            + ": You are invited to \"" + meeting.getTitle() + "\""
                            + " in " + meeting.getRoom().getName()
                            + " from " + meeting.getStartTime()
                            + " to "   + meeting.getEndTime()
            );
        }

    }

}
