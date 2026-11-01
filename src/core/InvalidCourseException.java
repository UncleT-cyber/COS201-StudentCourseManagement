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

/**
 * Thrown when the student tries to add a course with bad
 * details: empty title, wrong code format, duplicate
 * enrolment, too many credit units or an unsatisfied
 * prerequisite. The interface catches it and shows the
 * message to the student.
 */
public class InvalidCourseException extends Exception {

    public InvalidCourseException(String message) {
        super(message);
    }
}
