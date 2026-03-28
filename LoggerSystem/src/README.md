## Requirements 
- Support Log Levels - INFO, DEBUG, WARNING, ERROR 
- Configure a Log level, and if show only logs above the configured log level. 
- The destination sink of the logs, should be extensible, for now support Console and File. 
- Single Shared logger across the application. (meaning Singelton..) 

## Entities 
- LogLevel (INFO, DEBUG, WARNING, ERROR) 
- Log
- AppendMode - ConsoleMode, FileMode etc.. 
- LoggingService (configure a log level, and create log, and then delegate to the correct AppendMode) - This acts as the orchestrator. 

## Relationships 
LoggingService -> has-a -> LogLevel, AppendMode, Log
Log -> has-a -> Loglevel + message + timestamp
AppendMode -> consumes Log 

## Class Design 
```java
enum LogLevel {
    INFO, 
    DEBUG, 
    WARNING, 
    ERROR
}
```

```java
class Log {
    - message : string 
    - level : LogLevel 
    - timestamp : long
    
    + getMessage() 
    + getLevel() 
    + getTimestamp()    
}

```

```java
interface AppendMode { 
    + append(log : Log)
}

```

```java 
class ConsoleMode { 
    + append(log : Log)
    
}

```

```java 
class FileMode { 
    - filePath : string
    - fileWriter : FileWriter  
    
    + append()
}

```

```java 
class LoggingService { 
    - instance : LoggingService
    - appendMode : AppendMode 
    - logLevel : LogLevel
    
    + getInstance()
    + setAppendMode(appendMode)
    + getLogLevel()
    + getAppendMode()
    + setLogLevel(logLevel)
    + log(level : LogLevel, message : string)   
}

```

## Implementation 

### `LoggingService.GetInstance` 
```java 
getInstance(appendMode, logLevel) -> LoggingService   
  if instance == null    
      synchronized(LoggingService.class)
          if instance == null    
              instance = new LoggingService(appendMode, logLevel)
  return instance

```

### 

### `LoggingService.Log`
```java 
log(level, message)
  if level.getValue() >= this.logLevel.getValue()
      log = new Log(message, level)
      synchronized(this)
          appendMode.append(log)

```

