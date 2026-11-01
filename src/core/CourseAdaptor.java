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

import java.util.Arrays;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import model.Course;
import model.CourseTemplate;

/**
 * Adapts a master course template to the level a student
 * registered with.
 *
 * Examples (base level 200):
 *   COS 201 for a 200 Level student stays COS 201
 *   COS 201 for a 300 Level student becomes COS 301
 *   COS 201 for a 100 Level student becomes COS 101
 *
 * The numeric part of the code is shifted by the
 * difference between the student's level and the base
 * level. Titles that end in a Roman numeral move with it
 * (Mathematical Methods I becomes Mathematical Methods II
 * at 300 Level). Irregular courses are pinned so they do
 * not change.
 */
public class CourseAdaptor {

    public static final int[] VALID_LEVELS = {100, 200, 300, 400};

    /** Courses that never change code at other levels. */
    private static final String[] PINNED_CODES = {"CYB299"};

    /** Index + 1 is the value of the numeral. */
    private static final String[] ROMAN = {"I", "II", "III", "IV"};
    private static final Pattern TRAILING_NUMBER =
            Pattern.compile("^(.*\\D)(\\d+)(\\D*)$");
    private static final Pattern TRAILING_ROMAN =
            Pattern.compile("^(.*\\s)(IV|III|II|I)$");

    /** Only 100, 200, 300 and 400 are accepted. */
    public static void checkLevel(int level) {
        boolean valid = false;
        for (int i = 0; i < VALID_LEVELS.length; i++) {
            if (VALID_LEVELS[i] == level) {
                valid = true;
                break;
            }
        }
        if (!valid) {
            throw new IllegalArgumentException(
                    "Invalid level " + level
                    + ". Level must be 100, 200, 300 or 400.");
        }
    }

    /** Shifts the number in a course code for the
     *  student's level, for example COS 201 -> COS 301. */
    public static String adaptCode(String baseCode, int baseLevel,
                                   int studentLevel) {
        checkLevel(baseLevel);
        checkLevel(studentLevel);

        String compact = Course.normalizeCode(baseCode);
        for (int i = 0; i < PINNED_CODES.length; i++) {
            if (PINNED_CODES[i].equals(compact)) {
                return baseCode;
            }
        }
        if (studentLevel == baseLevel) {
            return baseCode;
        }

        Matcher match = TRAILING_NUMBER.matcher(baseCode.trim());
        if (!match.matches()) {
            return baseCode;
        }
        int number = Integer.parseInt(match.group(2));
        int adapted = number + (studentLevel - baseLevel);
        if (adapted < 100 || adapted > 999) {
            return baseCode;
        }
        return match.group(1) + adapted + match.group(3);
    }

    /** Moves the Roman numeral in a title with the level,
     *  for example Linear Algebra I -> Linear Algebra II.
     *  Titles without a numeral stay as they are. */
    public static String adaptTitle(String title, int baseLevel,
                                    int studentLevel) {
        checkLevel(baseLevel);
        checkLevel(studentLevel);

        int shift = (studentLevel - baseLevel) / 100;
        if (shift == 0) {
            return title;
        }
        if (title.toUpperCase().contains("SIWES")) {
            return title;
        }

        Matcher match = TRAILING_ROMAN.matcher(title.trim());
        if (!match.matches()) {
            return title;
        }
        String numeral = match.group(2);
        int value = Arrays.asList(ROMAN).indexOf(numeral) + 1;
        int moved = value + shift;
        if (moved < 1 || moved > 4) {
            return title;
        }
        return match.group(1) + ROMAN[moved - 1];
    }

    /** Builds the course a student actually sees. */
    public static Course adapt(CourseTemplate template, int studentLevel) {
        String code = adaptCode(template.getBaseCode(),
                template.getBaseLevel(), studentLevel);
        String title = adaptTitle(template.getTitle(),
                template.getBaseLevel(), studentLevel);
        return new Course(code, title, template.getUnits(),
                template.getCategory(), template.getTemplateId());
    }
}
