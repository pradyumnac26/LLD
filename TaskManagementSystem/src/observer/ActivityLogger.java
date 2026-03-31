package observer;

import models.Task;
import models.TaskPriority;
import models.TaskStatus;
import models.User;

import java.time.LocalDateTime;

public class ActivityLogger implements TaskObserver {

    @Override
    public void onStatusChanged(Task task, TaskStatus oldStatus, TaskStatus newStatus) {
        System.out.println("[LOG] " + LocalDateTime.now()
                + " | Task \"" + task.getTitle() + "\""
                + " status changed: " + oldStatus + " → " + newStatus);
    }

    @Override
    public void onPriorityChanged(Task task, TaskPriority oldPriority, TaskPriority newPriority) {
        System.out.println("[LOG] " + LocalDateTime.now()
                + " | Task \"" + task.getTitle() + "\""
                + " priority changed: " + oldPriority + " → " + newPriority);
    }

    @Override
    public void onAssigneeChanged(Task task, User oldAssignee, User newAssignee) {
        String oldName = oldAssignee != null ? oldAssignee.getName() : "unassigned";
        String newName = newAssignee != null ? newAssignee.getName() : "unassigned";
        System.out.println("[LOG] " + LocalDateTime.now()
                + " | Task \"" + task.getTitle() + "\""
                + " assignee changed: " + oldName + " → " + newName);
    }
}