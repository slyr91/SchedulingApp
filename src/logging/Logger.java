package logging;

import java.io.BufferedWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.time.ZonedDateTime;

public class Logger {

    private final Path logFile;

    private static Logger instance;

    private Logger() {
        logFile = Paths.get("./log.txt");
        if(!Files.exists(logFile)){
            try {
                Files.createFile(logFile);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    public static Logger getInstance() {

        if(instance == null) {
            instance = new Logger();
        }

        return instance;
    }

    public void information(String message) {
        String infoMessage = ZonedDateTime.now() + " [information]: " + message;

        try (BufferedWriter writer = Files.newBufferedWriter(logFile, StandardOpenOption.APPEND)) {
            writer.write(infoMessage);
            writer.newLine();
            writer.flush();
        } catch (IOException e) {
            error("Failed to write an information level message to the log file.");
            e.printStackTrace();
        }
    }

    public void warning(String message) {
        String warningMessage = ZonedDateTime.now() + " [warning]: " + message;

        try (BufferedWriter writer = Files.newBufferedWriter(logFile, StandardOpenOption.APPEND)) {
            writer.write(warningMessage);
            writer.newLine();
            writer.flush();
        } catch (IOException e) {
            error("Failed to write a warning level message to the log file.");
            e.printStackTrace();
        }
    }

    public void error(String message) {
        String errorMessage = ZonedDateTime.now() + " [error]: " + message;

        try (BufferedWriter writer = Files.newBufferedWriter(logFile, StandardOpenOption.APPEND)) {
            writer.write(errorMessage);
            writer.newLine();
            writer.flush();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
