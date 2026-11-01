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

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;

import model.Course;
import model.CourseTemplate;
import model.Student;

/**
 * Reads and writes the three text files of the system.
 *
 *   courses.txt         master course catalogue (shipped)
 *   data/enrollments.txt  each student's saved enrolments
 *   data/students.txt     registered students
 *
 * Every method guards its file operations with
 * try/catch. A missing file is treated as a first run,
 * a bad line is skipped and reported instead of
 * crashing the program.
 */
public class CourseFileIO {

    /** Project folder, found from the working directory. */
    public static final Path BASE = findBase();
    public static final Path CATALOG_FILE = BASE.resolve("courses.txt");
    public static final Path DATA_DIR = BASE.resolve("data");
    public static final Path ENROLLMENTS_FILE =
            DATA_DIR.resolve("enrollments.txt");
    public static final Path STUDENTS_FILE =
            DATA_DIR.resolve("students.txt");

    private CourseFileIO() {
    }

    /** Works whether the program is started from the
     *  project root or from inside a sub folder. */
    private static Path findBase() {
        Path current = Paths.get("").toAbsolutePath();
        if (Files.exists(current.resolve("src"))
                || Files.exists(current.resolve("courses.txt"))) {
            return current;
        }
        Path parent = current.getParent();
        if (parent != null
                && (Files.exists(parent.resolve("src"))
                || Files.exists(parent.resolve("courses.txt")))) {
            return parent;
        }
        return current;
    }

    /** Creates the folders and default files the first
     *  time the program is run. */
    public static void ensureFiles() {
        try {
            Files.createDirectories(DATA_DIR);
        } catch (IOException e) {
            System.out.println("Could not create data folder: " + e);
        }
        if (!Files.exists(CATALOG_FILE)) {
            writeTemplates(SeedData.templates());
        }
        if (!Files.exists(STUDENTS_FILE)) {
            List<Student> first = new ArrayList<>();
            first.add(SeedData.demoStudent());
            saveStudents(first);
        }
        if (!Files.exists(ENROLLMENTS_FILE)) {
            saveEnrollments(SeedData.demoStudent(), new ArrayList<Course>());
        }
    }

    /** Reads courses.txt. Blank lines and lines starting
     *  with # are ignored, bad lines are reported. */
    public static List<CourseTemplate> loadTemplates(ActivityLog log) {
        List<CourseTemplate> list = new ArrayList<>();
        if (!Files.exists(CATALOG_FILE)) {
            if (log != null) {
                log.warn("courses.txt not found - using built-in seed data");
            }
            return SeedData.templates();
        }
        int lineNumber = 0;
        int bad = 0;
        try (BufferedReader reader = Files.newBufferedReader(
                CATALOG_FILE, StandardCharsets.UTF_8)) {
            String line;
            while ((line = reader.readLine()) != null) {
                lineNumber++;
                line = line.trim();
                if (line.isEmpty() || line.startsWith("#")) {
                    continue;
                }
                String[] parts = line.split("\\|", -1);
                if (parts.length < 6) {
                    bad++;
                    continue;
                }
                try {
                    int baseLevel = Integer.parseInt(parts[2].trim());
                    int units = Integer.parseInt(parts[4].trim());
                    String prerequisite = (parts.length > 6)
                            ? parts[6].trim() : "";
                    CourseAdaptor.checkLevel(baseLevel);
                    if (units < 1 || units > 6) {
                        bad++;
                        continue;
                    }
                    list.add(new CourseTemplate(parts[0].trim(),
                            parts[1].trim(), baseLevel,
                            parts[3].trim(), units,
                            parts[5].trim(), prerequisite));
                } catch (IllegalArgumentException ex) {
                    // bad number or invalid level in this line
                    bad++;
                }
            }
        } catch (IOException e) {
            if (log != null) {
                log.warn("Could not read courses.txt: " + e.getMessage());
            }
            return SeedData.templates();
        }
        if (log != null && bad > 0) {
            log.warn("Skipped " + bad + " malformed course line(s) in courses.txt");
        }
        if (list.isEmpty()) {
            return SeedData.templates();
        }
        return list;
    }

    public static boolean writeTemplates(List<CourseTemplate> list) {
        try (BufferedWriter writer = Files.newBufferedWriter(
                CATALOG_FILE, StandardCharsets.UTF_8)) {
            writer.write("# Student Course Manager - master course catalogue");
            writer.newLine();
            writer.write("# format: templateId|baseCode|baseLevel|title|units|category|prerequisite");
            writer.newLine();
            for (CourseTemplate template : list) {
                writer.write(template.getTemplateId() + "|"
                        + template.getBaseCode() + "|"
                        + template.getBaseLevel() + "|"
                        + template.getTitle() + "|"
                        + template.getUnits() + "|"
                        + template.getCategory() + "|"
                        + template.getPrerequisiteTemplateId());
                writer.newLine();
            }
            return true;
        } catch (IOException e) {
            System.out.println("Could not write courses.txt: " + e);
            return false;
        }
    }

    public static List<Student> loadStudents() {
        List<Student> list = new ArrayList<>();
        if (!Files.exists(STUDENTS_FILE)) {
            list.add(SeedData.demoStudent());
            return list;
        }
        try (BufferedReader reader = Files.newBufferedReader(
                STUDENTS_FILE, StandardCharsets.UTF_8)) {
            String line;
            while ((line = reader.readLine()) != null) {
                line = line.trim();
                if (line.isEmpty() || line.startsWith("#")) {
                    continue;
                }
                String[] parts = line.split("\\|", -1);
                if (parts.length < 4) {
                    continue;
                }
                try {
                    list.add(new Student(parts[0].trim(), parts[1].trim(),
                            parts[2].trim(),
                            Integer.parseInt(parts[3].trim())));
                } catch (NumberFormatException ex) {
                    // bad level value, the line is skipped
                }
            }
        } catch (IOException e) {
            System.out.println("Could not read students.txt: " + e);
        }
        if (list.isEmpty()) {
            list.add(SeedData.demoStudent());
        }
        return list;
    }

    public static boolean saveStudents(List<Student> students) {
        try {
            Files.createDirectories(DATA_DIR);
        } catch (IOException e) {
            return false;
        }
        try (BufferedWriter writer = Files.newBufferedWriter(
                STUDENTS_FILE, StandardCharsets.UTF_8)) {
            writer.write("# registered students: id|name|program|level");
            writer.newLine();
            for (Student student : students) {
                writer.write(student.getId() + "|" + student.getName()
                        + "|" + student.getProgram() + "|"
                        + student.getLevel());
                writer.newLine();
            }
            return true;
        } catch (IOException e) {
            System.out.println("Could not write students.txt: " + e);
            return false;
        }
    }

    /** Saves one student's enrolments. The templateId is
     *  stored so the course can be rebuilt at the right
     *  level when the file is loaded again. */
    public static boolean saveEnrollments(Student student,
                                          List<Course> courses) {
        try {
            Files.createDirectories(DATA_DIR);
        } catch (IOException e) {
            return false;
        }
        try (BufferedWriter writer = Files.newBufferedWriter(
                ENROLLMENTS_FILE, StandardCharsets.UTF_8)) {
            writer.write("# studentId|templateId|code|title|units|category");
            writer.newLine();
            for (Course course : courses) {
                writer.write(student.getId() + "|"
                        + course.getTemplateId() + "|"
                        + course.getCode() + "|"
                        + course.getTitle() + "|"
                        + course.getUnits() + "|"
                        + course.getCategory());
                writer.newLine();
            }
            return true;
        } catch (IOException e) {
            System.out.println("Could not write enrollments.txt: " + e);
            return false;
        }
    }

    /**
     * Loads the student's saved enrolments into the
     * manager. The manager is cleared first so loading
     * twice does not duplicate anything.
     * Returns the number of courses loaded.
     */
    public static int loadEnrollments(CourseManager manager,
                                      ActivityLog log) {
        Student student = manager.getStudent();
        manager.clearEnrollments();
        if (!Files.exists(ENROLLMENTS_FILE)) {
            if (log != null) {
                log.warn("No saved record yet - starting with an empty course list");
            }
            return 0;
        }
        int loaded = 0;
        int bad = 0;
        try (BufferedReader reader = Files.newBufferedReader(
                ENROLLMENTS_FILE, StandardCharsets.UTF_8)) {
            String line;
            while ((line = reader.readLine()) != null) {
                line = line.trim();
                if (line.isEmpty() || line.startsWith("#")) {
                    continue;
                }
                String[] parts = line.split("\\|", -1);
                if (parts.length < 6 || !parts[0].trim()
                        .equals(student.getId())) {
                    continue;
                }
                try {
                    int units = Integer.parseInt(parts[4].trim());
                    Course course = rebuildCourse(manager, parts, units);
                    if (course == null) {
                        bad++;
                        continue;
                    }
                    manager.addLoadedCourse(course);
                    loaded++;
                } catch (NumberFormatException ex) {
                    bad++;
                }
            }
        } catch (IOException e) {
            if (log != null) {
                log.warn("Could not read enrollments.txt: " + e.getMessage());
            }
            return 0;
        }
        if (log != null && bad > 0) {
            log.warn("Skipped " + bad + " unreadable enrolment line(s)");
        }
        manager.fireChanged();
        return loaded;
    }

    /** Rebuilds one saved line. When the line has a
     *  templateId the adapted catalogue version is used,
     *  so the record follows the student's level. */
    private static Course rebuildCourse(CourseManager manager,
                                        String[] parts, int units) {
        String templateId = parts[1].trim();
        if (!templateId.isEmpty()) {
            for (Course course : manager.getCatalogue()) {
                if (templateId.equals(course.getTemplateId())) {
                    return new Course(course.getCode(), course.getTitle(),
                            units, course.getCategory(), templateId);
                }
            }
            // template no longer exists in courses.txt
            return new Course(parts[2].trim(), parts[3].trim(), units,
                    parts[5].trim(), templateId);
        }
        return new Course(parts[2].trim(), parts[3].trim(), units,
                parts[5].trim(), "");
    }
}
