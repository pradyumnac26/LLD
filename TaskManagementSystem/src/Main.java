import models.Task;
import models.TaskPriority;
import models.TaskStatus;
import models.User;

import java.time.LocalDate;

public class Main {
    public static void main(String[] args) {

        TaskManager tm = TaskManager.getTaskManagerInstance();

        // -------------------------------------------------------
        // CREATE USERS
        // -------------------------------------------------------
        System.out.println("========== USERS ==========\n");
        User prerana = tm.createUser("Prerana", "prerana@dev.com");
        User arjun = tm.createUser("Arjun", "arjun@dev.com");

        System.out.println("Created: " + prerana);
        System.out.println("Created: " + arjun);

        // -------------------------------------------------------
        // CREATE TASKS
        // -------------------------------------------------------
        System.out.println("\n========== CREATE TASKS ==========\n");
        Task t1 = tm.createTask(
                "Fix login bug",
                "NPE thrown on empty password",
                LocalDate.now().plusDays(2),
                TaskPriority.HIGH,
                prerana.getId()
        );

        Task t2 = tm.createTask(
                "Write unit tests",
                "Cover auth module",
                LocalDate.now().plusDays(5),
                TaskPriority.MID,
                prerana.getId()
        );

        Task t3 = tm.createTask(
                "Update README",
                "Add setup instructions",
                LocalDate.now().plusDays(7),
                TaskPriority.LOW,
                arjun.getId()
        );

        System.out.println(t1);
        System.out.println(t2);
        System.out.println(t3);

        // -------------------------------------------------------
        // UPDATE — observer fires on status / priority / assignee
        // -------------------------------------------------------
        System.out.println("\n========== UPDATES (observer logs below) ==========\n");
        tm.updateTaskAssignee(t1.getId(), arjun.getId());
        tm.updateTaskStatus(t1.getId(), TaskStatus.IN_PROGRESS);

        tm.updateTaskPriority(t2.getId(), TaskPriority.HIGH);
        tm.updateTaskStatus(t2.getId(), TaskStatus.IN_PROGRESS);

        tm.updateTaskStatus(t3.getId(), TaskStatus.DONE);

        // -------------------------------------------------------
        // UPDATE DESCRIPTION
        // -------------------------------------------------------
        System.out.println("\n========== UPDATE DESCRIPTION ==========\n");
        tm.updateTaskDescription(t1.getId(), "NPE in AuthService - fix null check");
        System.out.println("New description: " + t1.getDescription());

        // -------------------------------------------------------
        // MODIFY TASK — multiple fields at once
        // NOTE: your current TaskManager ignores newDueDate
        // -------------------------------------------------------
        System.out.println("\n========== MODIFY TASK ==========\n");
        System.out.println("Before: " + t2);

        tm.modifyTask(
                t2.getId(),
                "Cover auth + payment modules",   // new description
                LocalDate.now().plusDays(10),     // currently ignored in your TaskManager
                TaskPriority.HIGH,                // new priority
                arjun.getId()                     // new assignee
        );

        System.out.println("After:  " + t2);

        // -------------------------------------------------------
        // LIST / FILTER
        // -------------------------------------------------------
        System.out.println("\n========== ALL TASKS ==========\n");
        tm.getAllTasks().forEach(System.out::println);

        System.out.println("\n========== FILTER BY STATUS: IN_PROGRESS ==========\n");
        tm.listTasksByStatus(TaskStatus.IN_PROGRESS).forEach(System.out::println);

        System.out.println("\n========== FILTER BY PRIORITY: HIGH ==========\n");
        tm.listTasksByPriority(TaskPriority.HIGH).forEach(System.out::println);

        System.out.println("\n========== FILTER BY USER: Arjun ==========\n");
        tm.listTasksByUser(arjun.getId()).forEach(System.out::println);

        // -------------------------------------------------------
        // SEARCH
        // -------------------------------------------------------
        System.out.println("\n========== SEARCH 'unit' ==========\n");
        tm.searchTasks("unit").forEach(System.out::println);

        System.out.println("\n========== SEARCH 'auth' ==========\n");
        tm.searchTasks("auth").forEach(System.out::println);

        // -------------------------------------------------------
        // DELETE
        // -------------------------------------------------------
        System.out.println("\n========== DELETE ==========\n");
        tm.deleteTask(t3.getId());
        System.out.println("Deleted: " + t3.getTitle());
        System.out.println("Remaining tasks count: " + tm.getAllTasks().size());

        // -------------------------------------------------------
        // ERROR CASES
        // -------------------------------------------------------
        System.out.println("\n========== ERROR CASES ==========\n");
        try {
            tm.updateTaskStatus("fake-id", TaskStatus.DONE);
        } catch (IllegalArgumentException e) {
            System.out.println("CAUGHT: " + e.getMessage());
        }

        try {
            tm.deleteTask(t3.getId());
        } catch (IllegalArgumentException e) {
            System.out.println("CAUGHT: " + e.getMessage());
        }

        try {
            tm.updateTaskAssignee(t1.getId(), "fake-user-id");
        } catch (IllegalArgumentException e) {
            System.out.println("CAUGHT: " + e.getMessage());
        }
    }
}