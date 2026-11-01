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
 * A sample course material. It is linked to a course
 * template, not to a course code, so the same material
 * follows the course to whatever level the student
 * registered with (COS 201 -> COS 301).
 */
public class CourseMaterial {

    private final String materialId;
    private final String templateId;
    private final String title;
    private final String type;

    public CourseMaterial(String materialId, String templateId,
                          String title, String type) {
        this.materialId = materialId;
        this.templateId = templateId;
        this.title = title;
        this.type = type;
    }

    public String getMaterialId() {
        return materialId;
    }

    public String getTemplateId() {
        return templateId;
    }

    public String getTitle() {
        return title;
    }

    public String getType() {
        return type;
    }

    @Override
    public String toString() {
        return title + " [" + type + "]";
    }
}
