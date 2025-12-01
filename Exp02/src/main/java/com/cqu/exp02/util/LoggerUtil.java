package com.cqu.exp02.util;

import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class LoggerUtil {
    private static final String LOG_FILE = "rbac-app.log";
    private static final DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
    
    public enum LogLevel {
        INFO, WARN, ERROR
    }
    
    public static void log(LogLevel level, String message) {
        try (PrintWriter writer = new PrintWriter(new FileWriter(LOG_FILE, true))) {
            String timestamp = LocalDateTime.now().format(formatter);
            writer.println(String.format("[%s] %s: %s", timestamp, level, message));
        } catch (IOException e) {
            System.err.println("Failed to write to log file: " + e.getMessage());
        }
    }
    
    public static void info(String message) {
        log(LogLevel.INFO, message);
    }
    
    public static void warn(String message) {
        log(LogLevel.WARN, message);
    }
    
    public static void error(String message) {
        log(LogLevel.ERROR, message);
    }
}