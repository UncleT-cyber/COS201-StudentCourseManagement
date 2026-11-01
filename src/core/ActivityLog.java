/*
 * Student Name : ABAH ANTHONY
 * Student ID   : 30166082
 * Level        : 200 LEVEL
 * Department   : DEPARTMENT OF CYBER SECURITY
 * Institution  : MIVA OPEN UNIVERSITY
 * Course Code  : COS 201 - COMPUTER PROGRAMMING I
 * Project      : MID-SEMESTER LAB ASSESSMENT PROJECT
 *                (Student Course Management System)
 */

package core;

import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.function.Consumer;

/**
 * Keeps the activity entries shown in the Live System
 * Logs panel. Every enrolment, drop, warning and file
 * operation reports itself here.
 */
public class ActivityLog {

    /** One line in the log. */
    public static class Entry {
        public final String time;
        public final String level;
        public final String message;

        public Entry(String time, String level, String message) {
            this.time = time;
            this.level = level;
            this.message = message;
        }
    }

    private static final DateTimeFormatter TIME_FORMAT =
            DateTimeFormatter.ofPattern("HH:mm:ss");

    private final List<Entry> entries = new ArrayList<>();
    private final List<Consumer<Entry>> listeners = new ArrayList<>();

    /** Registers a panel that wants to show new lines. */
    public void addListener(Consumer<Entry> listener) {
        listeners.add(listener);
    }

    public void info(String message) {
        append("INFO", message);
    }

    public void warn(String message) {
        append("WARN", message);
    }

    private void append(String level, String message) {
        Entry entry = new Entry(
                LocalTime.now().format(TIME_FORMAT), level, message);
        entries.add(entry);
        for (Consumer<Entry> listener : listeners) {
            listener.accept(entry);
        }
    }

    public List<Entry> getEntries() {
        return Collections.unmodifiableList(entries);
    }

    /** Fills the panel with the starting lines so the log
     *  is never empty when the program opens. */
    public void seedStartup(int courseCount, String studentName) {
        info("Student Course Manager started");
        info("Course catalog loaded: " + courseCount + " courses");
        info("Real-time validation enabled");
        info("Session opened - " + studentName);
    }
}
