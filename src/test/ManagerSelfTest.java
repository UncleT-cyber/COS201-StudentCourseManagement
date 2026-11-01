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

package test;

import java.util.Collections;
import java.util.List;

import core.CourseAdaptor;
import core.CourseManager;
import core.InvalidCourseException;
import core.SeedData;
import model.Course;
import model.CourseTemplate;
import model.Student;

/**
 * Small command line check for the parts that do not
 * need the interface: the recursive search, the
 * recursive total units, the level adaptor and the
 * enrolment rules.
 *
 * Run:  java -cp out test.ManagerSelfTest
 * Every line should print PASS.
 */
public class ManagerSelfTest {

    private static int passed = 0;
    private static int failed = 0;

    public static void main(String[] args) {
        Student student = new Student("30166082", "ABAH ANTHONY",
                "DEPARTMENT OF CYBER SECURITY", 200);
        CourseManager manager = new CourseManager(student,
                SeedData.templates());
        List<Course> catalogue = manager.getCatalogue();

        // ---- catalogue was built from the seed data ----
        check("catalogue holds 10 courses", catalogue.size() == 10);

        // ---- recursive search ----
        Course found = manager.recursiveSearch(catalogue, 0, "COS 201");
        check("recursive search finds COS 201", found != null);
        check("found the right course",
                found != null && found.getTitle()
                .equals("Computer Programming I"));
        Course missing = manager.recursiveSearch(catalogue, 0, "CSC 999");
        check("recursive search returns null when missing",
                missing == null);
        Course looseCase = manager.recursiveSearch(catalogue, 0,
                "  cos 201 ");
        check("search ignores case and spaces", looseCase != null);

        // ---- recursive total on an empty list ----
        check("total of empty list is 0",
                manager.recursiveTotalUnits(Collections.<Course>emptyList(),
                        0) == 0);

        // ---- adding courses ----
        try {
            manager.addCourse("COS 201", "Computer Programming I", 3);
            manager.addCourse("MTH 201", "Mathematical Methods I", 2);
            check("two courses added", manager.enrolledCount() == 2);
        } catch (InvalidCourseException e) {
            check("two courses added", false);
        }
        check("recursive total is 5", manager.totalUnits() == 5);

        // ---- rejected additions ----
        check("duplicate rejected", throwsInvalid(manager, "COS 201",
                "Computer Programming I", 3));
        check("empty title rejected", throwsInvalid(manager, "CSC 201",
                "   ", 3));
        check("bad code rejected", throwsInvalid(manager, "12",
                "Some Course", 3));
        check("zero units rejected", throwsInvalid(manager, "GST 101",
                "Studies", 0));
        check("too many units rejected", throwsInvalid(manager, "GST 101",
                "Studies", 9));

        // ---- prerequisite rule ----
        check("CYB 203 blocked before CYB 201", throwsInvalid(manager,
                "CYB 203", "Cybercrime, Law and Countermeasures", 2));
        try {
            manager.addCourse("CYB 201",
                    "Introduction to Cybersecurity and Strategy", 2);
            check("CYB 203 allowed after CYB 201", true);
        } catch (InvalidCourseException e) {
            check("CYB 203 allowed after CYB 201", false);
        }

        // ---- credit limit of 24 ----
        CourseManager full = new CourseManager(student,
                SeedData.templates());
        try {
            for (CourseTemplate template : SeedData.templates()) {
                CourseAdaptor.checkLevel(200);
                int units = template.getUnits();
                try {
                    full.addCourse(template.getBaseCode(),
                            template.getTitle(), units);
                } catch (InvalidCourseException e) {
                    // prerequisites and the limit decide this
                }
            }
            check("credit load never passes 24",
                    full.totalUnits() <= CourseManager.MAX_UNITS);
        } catch (Exception e) {
            check("credit load never passes 24", false);
        }

        // ---- level adaptor ----
        check("code stays at own level",
                CourseAdaptor.adaptCode("COS 201", 200, 200)
                .equals("COS 201"));
        check("code moves to 300 level",
                CourseAdaptor.adaptCode("COS 201", 200, 300)
                .equals("COS 301"));
        check("code moves to 100 level",
                CourseAdaptor.adaptCode("COS 201", 200, 100)
                .equals("COS 101"));
        check("hyphenated code adapts",
                CourseAdaptor.adaptCode("MIVA-CYB 207", 200, 300)
                .equals("MIVA-CYB 307"));
        check("SIWES code is pinned",
                CourseAdaptor.adaptCode("CYB 299", 200, 400)
                .equals("CYB 299"));
        check("title numeral moves",
                CourseAdaptor.adaptTitle("Mathematical Methods I",
                        200, 300).equals("Mathematical Methods II"));
        check("SIWES title is pinned",
                CourseAdaptor.adaptTitle("SIWES I", 200, 300)
                .equals("SIWES I"));
        check("plain title does not change",
                CourseAdaptor.adaptTitle("Discrete Structures",
                        200, 300).equals("Discrete Structures"));
        check("bad level rejected", throwsBadLevel());

        // ---- results ----
        System.out.println();
        System.out.println("Passed: " + passed + "   Failed: " + failed);
        if (failed > 0) {
            System.exit(1);
        }
    }

    private static boolean throwsInvalid(CourseManager manager,
            String code, String title, int units) {
        try {
            manager.addCourse(code, title, units);
            return false;
        } catch (InvalidCourseException e) {
            return true;
        }
    }

    private static boolean throwsBadLevel() {
        try {
            CourseAdaptor.checkLevel(250);
            return false;
        } catch (IllegalArgumentException e) {
            return true;
        }
    }

    private static void check(String name, boolean condition) {
        if (condition) {
            passed++;
            System.out.println("PASS  " + name);
        } else {
            failed++;
            System.out.println("FAIL  " + name);
        }
    }
}
