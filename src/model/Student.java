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

package model;

/**
 * A registered student. The level chosen during
 * registration decides how the seeded courses are
 * labelled on that student's portal.
 */
public class Student {

    private final String id;
    private final String name;
    private final String program;
    private final int level;

    public Student(String id, String name, String program, int level) {
        this.id = id;
        this.name = name;
        this.program = program;
        this.level = level;
    }

    public String getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public String getProgram() {
        return program;
    }

    public int getLevel() {
        return level;
    }

    public String getLevelText() {
        return level + " LEVEL";
    }

    /** Returns the last word of the name so that
     *  "ABAH ANTHONY" greets the student as ANTHONY. */
    public String getFirstName() {
        String trimmed = name.trim();
        int lastSpace = trimmed.lastIndexOf(' ');
        if (lastSpace < 0) {
            return trimmed;
        }
        return trimmed.substring(lastSpace + 1);
    }

    /** Two students are the same when the IDs match. */
    public boolean sameIdAs(String otherId) {
        return id != null && id.equals(otherId);
    }

    @Override
    public String toString() {
        return name + " (" + id + ") - " + level + " Level";
    }
}
