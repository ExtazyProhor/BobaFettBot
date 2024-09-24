package com.prohor.personal.bobaFettBot.system;

import java.io.*;
import java.util.Arrays;

public class LogWriter implements ExceptionWriter {
    private final File exceptionsLog;

    public LogWriter(File dir) {
        exceptionsLog = new File(dir, "exceptions.log");
    }

    @Override
    public void writeException(Exception e) {
        log(exceptionsLog,
                e + "\n" + Arrays.stream(e.getStackTrace()).map(x -> "\tat " + x + "\n").reduce("", String::concat));
    }

    private void log(File file, String s) {
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(file, true))) {
            writer.write(LocalFormatter.getCurrentDateTime());
            writer.newLine();
            writer.write(s);
            writer.newLine();
            writer.newLine();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
