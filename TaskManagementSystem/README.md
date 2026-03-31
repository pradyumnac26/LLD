# Task Management System / Ticket System 

## Requirements 

- The goal is to design something like Jira/Trello/Asana. 
- The user should be able to create a task , say title, description, priority and assignee.. 
- Should be able to modify the task or delete a created task. 
- Task can have priorities LOW, MEDIUM, HIGH 
- We can know the task status like - TODO, IN_PROGRESS, DONE 
- Should be able to list all the tasks, and search or filter through the tasks.
- extensibility - should be easily able to add new task statuses, or priorities etc. 

## Entities 
- Task 
- models.TaskStatus
- models.TaskPriority 
- User (Assignee) 
- TaskManager (orchestrator to create tasks , update, deleteetc.. , to assign , to add priotrities, to update etc..)

## Relationship
Task has-a task status, user, task priority 
Task Manager -> Task, 

## class Design 

```java 
enum models.TaskStatus {
    TODO, 
    IN_PROGRESS, 
    DONE
}
```

```java 
enum models.TaskPriority {
    LOW, 
    MEDIUM,
    HIGH
}
```

```java 
import models.TaskPriority;
import models.TaskStatus;

class Task:
        -id          :

String(final)
-title       :

String(final)
-description :String
-taskStatus  :TaskStatus
-taskPriority:TaskPriority
-assignee    :User

+

getId() +

getTitle() +

getDescription()
+

getTaskStatus()  +

setTaskStatus(status)
+

getTaskPriority()+

setTaskPriority(priority)
+

getAssignee()    +

setAssignee(user)

class User:
        -id    :

String(final)
-name  :String
-email :String

class TaskManager:  // Singleton
        -instance :

TaskManager(static)
-tasks    :Map<String, Task>    // taskId → Task

+

getInstance()                          ->TaskManager
+

addTask(title, desc, priority, assignee) ->Task
+

updateTaskStatus(taskId, status)
+

updateTaskPriority(taskId, priority)
+

updateTaskAssignee(taskId, user)
+

removeTask(taskId)
+

getAllTasks()                           ->List<Task>
+

searchByAssignee(user)                 ->List<Task>
+

searchByStatus(status)                 ->List<Task>
+

searchByPriority(priority)             ->List<Task>

```

