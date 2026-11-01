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
import java.util.List;

import model.CourseMaterial;
import model.CourseTemplate;
import model.Student;

/**
 * The master sample data for the project: the 200 Level
 * First Semester course list, the sample materials that
 * go with each course, the demo student and the list of
 * programmes shown during registration.
 *
 * One dataset only. The CourseAdaptor relabels it for
 * whichever level a student registers with.
 */
public class SeedData {

    private SeedData() {
    }

    /** The 200 Level First Semester course list. */
    public static List<CourseTemplate> templates() {
        List<CourseTemplate> list = new ArrayList<>();
        list.add(new CourseTemplate("MTH_201", "MTH 201", 200,
                "Mathematical Methods I", 2, "Core", ""));
        list.add(new CourseTemplate("ENT_211", "ENT 211", 200,
                "Entrepreneurship and Innovation", 2, "Core", ""));
        list.add(new CourseTemplate("PROGRAMMING_1", "COS 201", 200,
                "Computer Programming I", 3, "Core", ""));
        list.add(new CourseTemplate("CYB_201", "CYB 201", 200,
                "Introduction to Cybersecurity and Strategy", 2, "Core", ""));
        list.add(new CourseTemplate("SEN_201", "SEN 201", 200,
                "Introduction to Software Engineering", 2, "Core", ""));
        list.add(new CourseTemplate("CYB_203", "CYB 203", 200,
                "Cybercrime, Law and Countermeasures", 2, "Core", "CYB_201"));
        list.add(new CourseTemplate("MTH_205", "MTH 205", 200,
                "Linear Algebra I", 2, "Core", "MTH_201"));
        list.add(new CourseTemplate("SIWES_1", "CYB 299", 200,
                "SIWES I", 3, "Core", ""));
        list.add(new CourseTemplate("ZERO_TRUST", "MIVA-CYB 207", 200,
                "Zero Trust Networking", 2, "Elective", "CYB_201"));
        list.add(new CourseTemplate("COS_203", "COS 203", 200,
                "Discrete Structures", 2, "Core", "MTH_201"));
        return list;
    }

    /** Sample materials, two or three per course. They are
     *  linked to the template so they follow the course to
     *  any level. */
    public static List<CourseMaterial> materials() {
        List<CourseMaterial> list = new ArrayList<>();
        list.add(new CourseMaterial("MAT_01", "MTH_201",
                "Lecture Notes - Functions and Graphs", "PDF"));
        list.add(new CourseMaterial("MAT_02", "MTH_201",
                "Week 2 Slides - Differentiation", "Slides"));
        list.add(new CourseMaterial("MAT_03", "ENT_211",
                "Reading - Starting a Small Business", "Reading"));
        list.add(new CourseMaterial("MAT_04", "ENT_211",
                "Case Study - Nigerian Startups", "PDF"));
        list.add(new CourseMaterial("MAT_05", "PROGRAMMING_1",
                "Lecture Notes - Variables and Data Types", "PDF"));
        list.add(new CourseMaterial("MAT_06", "PROGRAMMING_1",
                "Slides - Loops and Conditionals", "Slides"));
        list.add(new CourseMaterial("MAT_07", "PROGRAMMING_1",
                "Practice Questions - Java Basics", "PDF"));
        list.add(new CourseMaterial("MAT_08", "CYB_201",
                "Introduction to Cyber Threats", "PDF"));
        list.add(new CourseMaterial("MAT_09", "CYB_201",
                "Slides - Security Strategy Basics", "Slides"));
        list.add(new CourseMaterial("MAT_10", "SEN_201",
                "Lecture Notes - SDLC Models", "PDF"));
        list.add(new CourseMaterial("MAT_11", "SEN_201",
                "Reading - Agile Methodology", "Reading"));
        list.add(new CourseMaterial("MAT_12", "CYB_203",
                "Slides - Cybercrime Legislation in Nigeria", "Slides"));
        list.add(new CourseMaterial("MAT_13", "CYB_203",
                "Case Study - Digital Fraud", "PDF"));
        list.add(new CourseMaterial("MAT_14", "MTH_205",
                "Lecture Notes - Matrix Operations", "PDF"));
        list.add(new CourseMaterial("MAT_15", "MTH_205",
                "Slides - Vector Spaces", "Slides"));
        list.add(new CourseMaterial("MAT_16", "SIWES_1",
                "SIWES Placement Guidelines", "PDF"));
        list.add(new CourseMaterial("MAT_17", "SIWES_1",
                "Log Sheet Template", "PDF"));
        list.add(new CourseMaterial("MAT_18", "ZERO_TRUST",
                "Reading - Zero Trust Explained", "Reading"));
        list.add(new CourseMaterial("MAT_19", "ZERO_TRUST",
                "Slides - Microsegmentation", "Slides"));
        list.add(new CourseMaterial("MAT_20", "COS_203",
                "Lecture Notes - Sets and Logic", "PDF"));
        list.add(new CourseMaterial("MAT_21", "COS_203",
                "Practice Questions - Graph Theory", "PDF"));
        return list;
    }

    /** The demo account: ABAH ANTHONY, 200 Level. */
    public static Student demoStudent() {
        return new Student("30166082", "ABAH ANTHONY",
                "DEPARTMENT OF CYBER SECURITY", 200);
    }

    public static List<String> programs() {
        List<String> list = new ArrayList<>();
        list.add("DEPARTMENT OF CYBER SECURITY");
        list.add("DEPARTMENT OF COMPUTER SCIENCE");
        list.add("DEPARTMENT OF SOFTWARE ENGINEERING");
        list.add("DEPARTMENT OF INFORMATION TECHNOLOGY");
        return list;
    }
}
