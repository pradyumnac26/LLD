package service;

import Mode.AppendMode;
import model.Log;
import model.LogLevel;

import java.security.AlgorithmConstraints;

public class LoggingService {
private LogLevel logLevel;
private AppendMode appendMode;

public LoggingService(AppendMode appendMode, LogLevel logLevel) {
    this.appendMode = appendMode;
    this.logLevel= logLevel;

}

public LogLevel getLogLevel() {
    return logLevel;
}

    public AppendMode getAppendMode() {
        return appendMode;
    }

    public void setAppendMode(AppendMode appendMode) {
        this.appendMode = appendMode;
    }

    public void setLogLevel(LogLevel logLevel) {
        this.logLevel = logLevel;
    }

    public void Log(LogLevel incomingLevel, String message) {
    if (incomingLevel.getValue() >= this.logLevel.getValue()) {
        Log log = new Log(message, incomingLevel);
        appendMode.append(log);
    }
    }
}
