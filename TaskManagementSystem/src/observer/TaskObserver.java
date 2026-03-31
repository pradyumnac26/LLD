package observer;


import models.Task;
import models.TaskPriority;
import models.TaskStatus;
import models.User;

public interface TaskObserver {
    void onStatusChanged(Task task, TaskStatus oldStatus, TaskStatus newStatus);
    void onPriorityChanged(Task task, TaskPriority oldPriority, TaskPriority newPriority);
    void onAssigneeChanged(Task task, User oldAssignee, User newAssignee);
}