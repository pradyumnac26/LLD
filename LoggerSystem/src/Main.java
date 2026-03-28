import Mode.ConsoleMode;
import model.LogLevel;
import service.LoggingService;

public class Main {
    public static void main(String[] args) {
        LoggingService lg = new LoggingService(new ConsoleMode(), LogLevel.INFO);
        lg.Log(LogLevel.INFO, "Hii");
        lg.Log(LogLevel.DEBUG, "ywwwb");
    }
}