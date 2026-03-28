package Mode;

import model.Log;

public class ConsoleMode implements AppendMode{
    @Override
    public void append(Log log) {
        System.out.println(log.toString());
    }
}
