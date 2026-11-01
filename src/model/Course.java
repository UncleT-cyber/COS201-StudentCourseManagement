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
 * A course as the student sees it on the portal.
 * The code and title here are already adapted to the
 * student's level (for example COS 201 becomes COS 301
 * for a 300 Level student).
 */
public class Course {

    private final String code;
    private final String title;
    private final int units;
    private final String category;
    private final String templateId;

    public Course(String code, String title, int units, String category, String templateId) {
        this.code = code;
        this.title = title;
        this.units = units;
        this.category = category;
        this.templateId = (templateId == null) ? "" : templateId;
    }

    public String getCode() {
        return code;
    }

    public String getTitle() {
        return title;
    }

    public int getUnits() {
        return units;
    }

    public String getCategory() {
        return category;
    }

    public String getTemplateId() {
        return templateId;
    }

    /** A custom course is one the student typed in by hand,
     *  it does not come from the seeded catalogue. */
    public boolean isCustom() {
        return templateId.isEmpty();
    }

    /** Puts a code into a standard form so COS 201, cos201 and
     *  " cos 201 " are all treated as the same course. */
    public static String normalizeCode(String code) {
        if (code == null) {
            return "";
        }
        return code.trim().toUpperCase().replace(" ", "");
    }

    @Override
    public String toString() {
        return code + " - " + title + " (" + units + " units)";
    }
}
