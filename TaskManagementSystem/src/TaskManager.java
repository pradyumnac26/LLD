import models.Task;
import models.TaskPriority;
import models.TaskStatus;
import models.User;
import observer.ActivityLogger;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

public class TaskManager {
    private static volatile TaskManager taskManagerInstance;

    private final ConcurrentHashMap<String, Task> tasks;
    private final ConcurrentHashMap<String, User> users;

    private TaskManager() {
        users = new ConcurrentHashMap<>();
        tasks = new ConcurrentHashMap<>();
    }

    public static TaskManager getTaskManagerInstance() {
        if (taskManagerInstance == null) {
            synchronized (TaskManager.class) {
                if (taskManagerInstance == null) {
                    taskManagerInstance = new TaskManager();
                }
            }
        }
        return taskManagerInstance;
    }

    // -------------------------------------------------------
    // USER OPERATIONS
    // -------------------------------------------------------
    public User createUser(String name, String email) {
        User user = new User(name, email);
        users.put(user.getId(), user);
        return user;
    }

    public User getUserById(String userId) {
        return users.get(userId);
    }

    // -------------------------------------------------------
    // CREATE TASK
    // -------------------------------------------------------
    public Task createTask(String title,
                           String description,
                           LocalDate dueDate,
                           TaskPriority priority,
                           String createdByUserId) {

        User createdBy = users.get(createdByUserId);
        if (createdBy == null) {
            throw new IllegalArgumentException("User not found: " + createdByUserId);
        }

        Task task = new Task.TaskBuilder(title)
                .description(description)
                .priority(priority)
                .createdBy(createdBy)
                .build();

        task.addObserver(new ActivityLogger());
        tasks.put(task.getId(), task);
        return task;
    }

    public void addTask(Task task) {
        if (task == null) {
            throw new IllegalArgumentException("Task cannot be null");
        }
        task.addObserver(new ActivityLogger());
        tasks.put(task.getId(), task);
    }

    // -------------------------------------------------------
    // UPDATE SINGLE FIELD
    // -------------------------------------------------------
    public void updateTaskStatus(String taskId, TaskStatus status) {
        Task task = tasks.get(taskId);
        if (task == null) {
            throw new IllegalArgumentException("Task not found: " + taskId);
        }
        task.setStatus(status);
    }

    public void updateTaskPriority(String taskId, TaskPriority priority) {
        Task task = tasks.get(taskId);
        if (task == null) {
            throw new IllegalArgumentException("Task not found: " + taskId);
        }
        task.setPriority(priority);
    }

    public void updateTaskAssignee(String taskId, String userId) {
        Task task = tasks.get(taskId);
        if (task == null) {
            throw new IllegalArgumentException("Task not found: " + taskId);
        }

        User user = users.get(userId);
        if (user == null) {
            throw new IllegalArgumentException("User not found: " + userId);
        }

        task.setAssignee(user);
    }

    public void updateTaskDescription(String taskId, String description) {
        Task task = tasks.get(taskId);
        if (task == null) {
            throw new IllegalArgumentException("Task not found: " + taskId);
        }
        task.setDescription(description);
    }


    // -------------------------------------------------------
    // MODIFY MULTIPLE FIELDS
    // pass null for any field you don't want to change
    // -------------------------------------------------------
    public void modifyTask(String taskId,
                           String newDescription,
                           LocalDate newDueDate,
                           TaskPriority newPriority,
                           String newAssigneeId) {

        Task task = tasks.get(taskId);
        if (task == null) {
            throw new IllegalArgumentException("Task not found: " + taskId);
        }

        if (newDescription != null) {
            task.setDescription(newDescription);
        }


        if (newPriority != null) {
            task.setPriority(newPriority);
        }

        if (newAssigneeId != null) {
            User user = users.get(newAssigneeId);
            if (user == null) {
                throw new IllegalArgumentException("User not found: " + newAssigneeId);
            }
            task.setAssignee(user);
        }
    }

    // -------------------------------------------------------
    // DELETE TASK
    // -------------------------------------------------------
    public void removeTask(Task task) {
        if (task == null) {
            return;
        }
        tasks.remove(task.getId());
    }

    public void deleteTask(String taskId) {
        Task removedTask = tasks.remove(taskId);
        if (removedTask == null) {
            throw new IllegalArgumentException("Task not found: " + taskId);
        }
    }

    // -------------------------------------------------------
    // READ TASKS
    // -------------------------------------------------------
    public Task getTaskById(String taskId) {
        return tasks.get(taskId);
    }

    public List<Task> getAllTasks() {
        return new ArrayList<>(tasks.values());
    }

    public List<Task> listTasksByUser(String userId) {
        User user = users.get(userId);
        if (user == null) {
            throw new IllegalArgumentException("User not found: " + userId);
        }

        return tasks.values()
                .stream()
                .filter(task -> user.equals(task.getAssignee()))
                .collect(Collectors.toList());
    }

    public List<Task> listTasksByStatus(TaskStatus status) {
        return tasks.values()
                .stream()
                .filter(task -> task.getStatus() == status)
                .collect(Collectors.toList());
    }

    public List<Task> listTasksByPriority(TaskPriority priority) {
        return tasks.values()
                .stream()
                .filter(task -> task.getPriority() == priority)
                .collect(Collectors.toList());
    }

    // -------------------------------------------------------
    // SEARCH TASKS
    // -------------------------------------------------------
    public List<Task> searchTasks(String keyword) {
        String lowerKeyword = keyword.toLowerCase();

        return tasks.values()
                .stream()
                .filter(task ->
                        task.getTitle().toLowerCase().contains(lowerKeyword) ||
                                (task.getDescription() != null &&
                                        task.getDescription().toLowerCase().contains(lowerKeyword))
                )
                .collect(Collectors.toList());
    }
}