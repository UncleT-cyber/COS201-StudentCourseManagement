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

import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import model.Course;
import model.CourseTemplate;
import model.Student;

/**
 * Holds the course catalogue and the student's enrolments
 * and performs the main operations of the system:
 * adding a course, dropping a course, searching by code
 * and computing the total credit units.
 *
 * The search and the total are written with recursion as
 * required by the lab brief. They work on a plain
 * ArrayList and do not touch the interface, so they can
 * be tested on their own (see test/ManagerSelfTest).
 */
public class CourseManager {

    /** Maximum credit load for a semester. */
    public static final int MAX_UNITS = 24;

    /** Course code format: COS 201, cos201, MIVA-CYB 207. */
    private static final String CODE_FORMAT =
            "^[A-Z]{2,6}(?:-[A-Z]{3})?-?\\d{3}$";

    private final Student student;
    private final List<CourseTemplate> templates;
    private final List<Course> catalogue = new ArrayList<>();
    private final List<Course> enrollments = new ArrayList<>();
    private final Map<String, CourseTemplate> templateByCode =
            new LinkedHashMap<>();
    private final Map<String, CourseTemplate> templateById =
            new LinkedHashMap<>();
    private final List<Runnable> changeListeners = new ArrayList<>();

    public CourseManager(Student student, List<CourseTemplate> templates) {
        this.student = student;
        this.templates = templates;

        // Build this student's catalogue from the master data.
        for (CourseTemplate template : templates) {
            templateById.put(template.getTemplateId(), template);
            Course course = CourseAdaptor.adapt(template, student.getLevel());
            catalogue.add(course);
            templateByCode.put(Course.normalizeCode(course.getCode()),
                    template);
        }
    }

    public Student getStudent() {
        return student;
    }

    public List<CourseTemplate> getTemplates() {
        return templates;
    }

    public List<Course> getCatalogue() {
        return Collections.unmodifiableList(catalogue);
    }

    /** Live view of the enrolments, used by the table
     *  models and by the file writer. */
    public List<Course> getEnrollments() {
        return enrollments;
    }

    public void addChangeListener(Runnable listener) {
        changeListeners.add(listener);
    }

    /** Tells every panel that the enrolments changed. */
    public void fireChanged() {
        for (Runnable listener : changeListeners) {
            listener.run();
        }
    }

    /** Linear scan of the catalogue (loop practice). */
    public Course findCatalogueByCode(String code) {
        String target = Course.normalizeCode(code);
        for (Course course : catalogue) {
            if (Course.normalizeCode(course.getCode()).equals(target)) {
                return course;
            }
        }
        return null;
    }

    public CourseTemplate getTemplate(Course course) {
        if (course == null || course.isCustom()) {
            return null;
        }
        return templateById.get(course.getTemplateId());
    }

    /** The prerequisite shown to the student, already
     *  adapted to the student's level. Null when the
     *  course has no prerequisite. */
    public String getPrerequisiteCode(Course course) {
        CourseTemplate template = getTemplate(course);
        if (template == null || !template.hasPrerequisite()) {
            return null;
        }
        CourseTemplate needed =
                templateById.get(template.getPrerequisiteTemplateId());
        if (needed == null) {
            return null;
        }
        return CourseAdaptor.adaptCode(needed.getBaseCode(),
                needed.getBaseLevel(), student.getLevel());
    }

    /** True when the student already took the course this
     *  one depends on. Courses without a prerequisite
     *  always pass. */
    public boolean isPrerequisiteMet(Course course) {
        CourseTemplate template = getTemplate(course);
        if (template == null || !template.hasPrerequisite()) {
            return true;
        }
        for (Course taken : enrollments) {
            if (template.getPrerequisiteTemplateId()
                    .equals(taken.getTemplateId())) {
                return true;
            }
        }
        return false;
    }

    public boolean isEnrolled(String code) {
        String target = Course.normalizeCode(code);
        for (Course course : enrollments) {
            if (Course.normalizeCode(course.getCode()).equals(target)) {
                return true;
            }
        }
        return false;
    }

    /**
     * Adds a course after checking every rule.
     * Throws InvalidCourseException with a message the
     * student can read when something is wrong.
     */
    public Course addCourse(String code, String title, int units)
            throws InvalidCourseException {

        if (code == null || code.trim().isEmpty()) {
            throw new InvalidCourseException(
                    "Course code cannot be empty.");
        }
        String compact = Course.normalizeCode(code);
        if (!compact.matches(CODE_FORMAT)) {
            throw new InvalidCourseException(
                    "Invalid course code format. Example: COS 201.");
        }
        if (title == null || title.trim().isEmpty()) {
            throw new InvalidCourseException(
                    "Course title cannot be empty.");
        }
        if (units < 1 || units > 6) {
            throw new InvalidCourseException(
                    "Units must be between 1 and 6.");
        }
        if (isEnrolled(compact)) {
            throw new InvalidCourseException(
                    "You are already enrolled in " + compact + ".");
        }
        int newTotal = totalUnits() + units;
        if (newTotal > MAX_UNITS) {
            throw new InvalidCourseException(
                    "Credit limit exceeded. This enrolment would make "
                    + newTotal + " units, the maximum is "
                    + MAX_UNITS + " units.");
        }

        Course fromCatalogue = findCatalogueByCode(compact);
        Course course;
        if (fromCatalogue != null) {
            if (!isPrerequisiteMet(fromCatalogue)) {
                throw new InvalidCourseException(
                        "Prerequisite not met. Enrol in "
                        + getPrerequisiteCode(fromCatalogue)
                        + " first.");
            }
            // Use the catalogue wording so the table stays tidy.
            course = new Course(fromCatalogue.getCode(),
                    title.trim(), units,
                    fromCatalogue.getCategory(),
                    fromCatalogue.getTemplateId());
        } else {
            course = new Course(compact, title.trim(), units,
                    "Other", "");
        }

        enrollments.add(course);
        fireChanged();
        return course;
    }

    /** Removes a course by its code. Returns true when a
     *  course was actually found and removed. */
    public boolean dropCourse(String code) {
        String target = Course.normalizeCode(code);
        for (int i = 0; i < enrollments.size(); i++) {
            if (Course.normalizeCode(
                    enrollments.get(i).getCode()).equals(target)) {
                enrollments.remove(i);
                fireChanged();
                return true;
            }
        }
        return false;
    }

    /** Used by the file loader: adds a course exactly as
     *  it was saved, without re-running the checks. */
    public void addLoadedCourse(Course course) {
        enrollments.add(course);
    }

    public void clearEnrollments() {
        enrollments.clear();
    }

    /**
     * Recursive search for a course code, starting at
     * index.
     * Base case: the index has run past the end of the
     * list, there is nothing left to check, return null.
     * Recursive step: check the current course, if it is
     * not a match search the rest of the list from
     * index + 1.
     */
    public Course recursiveSearch(List<Course> list, int index,
                                  String targetCode) {
        if (list == null || index >= list.size()) {
            return null;
        }
        Course current = list.get(index);
        if (Course.normalizeCode(current.getCode())
                .equals(Course.normalizeCode(targetCode))) {
            return current;
        }
        return recursiveSearch(list, index + 1, targetCode);
    }

    /**
     * Recursive total of the credit units.
     * Base case: index is past the end, contribute 0.
     * Recursive step: units of the current course plus the
     * total of the rest of the list.
     */
    public int recursiveTotalUnits(List<Course> list, int index) {
        if (list == null || index >= list.size()) {
            return 0;
        }
        return list.get(index).getUnits()
                + recursiveTotalUnits(list, index + 1);
    }

    public Course searchCatalogue(String code) {
        return recursiveSearch(catalogue, 0, code);
    }

    public int totalUnits() {
        return recursiveTotalUnits(enrollments, 0);
    }

    public int enrolledCount() {
        int count = 0;
        for (Course course : enrollments) {
            count++;
        }
        return count;
    }

    public int remainingUnits() {
        return MAX_UNITS - totalUnits();
    }

    /** Credit units grouped by category, used by the
     *  analytics chart. Insertion order is kept. */
    public Map<String, Integer> creditsByCategory() {
        Map<String, Integer> totals = new LinkedHashMap<>();
        for (Course course : enrollments) {
            String category = course.getCategory();
            Integer old = totals.get(category);
            totals.put(category,
                    (old == null) ? course.getUnits() : old + course.getUnits());
        }
        return totals;
    }
}
