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
 * One master record for a seeded course.
 * The template stores the base code and base level
 * (COS 201 at 200 Level). The application adapts it to
 * whatever level a student registered with.
 */
public class CourseTemplate {

    private final String templateId;
    private final String baseCode;
    private final int baseLevel;
    private final String title;
    private final int units;
    private final String category;
    private final String prerequisiteTemplateId;

    public CourseTemplate(String templateId, String baseCode, int baseLevel,
                          String title, int units, String category,
                          String prerequisiteTemplateId) {
        this.templateId = templateId;
        this.baseCode = baseCode;
        this.baseLevel = baseLevel;
        this.title = title;
        this.units = units;
        this.category = category;
        this.prerequisiteTemplateId =
                (prerequisiteTemplateId == null) ? "" : prerequisiteTemplateId;
    }

    public String getTemplateId() {
        return templateId;
    }

    public String getBaseCode() {
        return baseCode;
    }

    public int getBaseLevel() {
        return baseLevel;
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

    public String getPrerequisiteTemplateId() {
        return prerequisiteTemplateId;
    }

    public boolean hasPrerequisite() {
        return !prerequisiteTemplateId.isEmpty();
    }

    @Override
    public String toString() {
        return baseCode + " - " + title;
    }
}
