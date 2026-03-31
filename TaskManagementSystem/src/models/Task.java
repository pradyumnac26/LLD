package models;

import observer.TaskObserver;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class Task {
    private String id;
    private String title ;
    private String description;
    private TaskPriority priority;
    private TaskStatus status;
    private User assignee ;
    private User createdBy;
    private final List<TaskObserver> observers = new ArrayList<>();
    private Task(TaskBuilder builder) {
        this.id          = UUID.randomUUID().toString();
        this.title       = builder.title;
        this.description = builder.description;
        this.priority    = builder.priority != null ? builder.priority : TaskPriority.MID;
        this.status      = builder.status;
        this.createdBy   = builder.createdBy;
        this.assignee    = builder.assignee;
    }

    // -------------------------------------------------------
    // Observer management
    // -------------------------------------------------------
    public void addObserver(TaskObserver observer) {
        observers.add(observer);
    }

    public void removeObserver(TaskObserver observer) {
        observers.remove(observer);
    }


    private void notifyStatusChanged(TaskStatus oldStatus, TaskStatus newStatus) {
        for (TaskObserver o : observers) {
            o.onStatusChanged(this, oldStatus, newStatus);
        }
    }

    private void notifyPriorityChanged(TaskPriority oldPriority, TaskPriority newPriority) {
        for (TaskObserver o : observers) {
            o.onPriorityChanged(this, oldPriority, newPriority);
        }
    }

    private void notifyAssigneeChanged(User oldAssignee, User newAssignee ) {
        for (TaskObserver o: observers) {
            o.onAssigneeChanged(this, oldAssignee, newAssignee);
        }
    }

    @Override
    public String toString() {
        return "Task[" + id.substring(0, 8) + "] \""
                + title + "\" | " + status + " | " + priority
                + " | assignee=" + (assignee != null ? assignee.getName() : "none");
    }

    public String getId() {
        return id;
    }

    public String getDescription() {
        return description;
    }

    public String getTitle() {
        return title;
    }

    public TaskPriority getPriority() {
        return priority;
    }

    public TaskStatus getStatus() {
        return status;
    }

    public User getAssignee() {
        return assignee;
    }
    public User getCreatedBy() {
        return createdBy;
    }

    public void setAssignee(User assignee) {
        User oldAssignee = this.assignee;
        this.assignee = assignee;
        notifyAssigneeChanged(oldAssignee, assignee);
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public void setPriority(TaskPriority priority) {
        TaskPriority oldPriority = this.priority;
        this.priority = priority;
        notifyPriorityChanged(oldPriority, priority);
    }

    public void setStatus(TaskStatus status) {
        TaskStatus oldStatus = this.status;
        this.status = status;
        notifyStatusChanged(oldStatus, status);
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public void setCreatedBy(User createdBy) {
        this.createdBy = createdBy;
    }

    public static class TaskBuilder {
        private  String title;
        private String description;

        private TaskPriority priority;
        private TaskStatus status;
        private User assignee;
        private User createdBy;

        public TaskBuilder(String title) {
            this.title = title;
        }

        public TaskBuilder description(String description){
            this.description = description;
            return this;
        }

        public TaskBuilder priority(TaskPriority priority) {
            this.priority = priority;
            return this;
        }

        public TaskBuilder status(TaskStatus status) {
            this.status = status;
            return this;
        }

        public TaskBuilder assignee(User assignee) {
            this.assignee = assignee;
            return this;
        }

        public TaskBuilder createdBy(User createdBy) {
            this.createdBy = createdBy;
            return this;
        }

        public Task build() {
            return new Task(this);
        }
    }
}
