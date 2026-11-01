# 01 - Project Overview

**Student Name:** ABAH ANTHONY
**Student ID:** 30166082
**Level:** 200 LEVEL
**Department:** DEPARTMENT OF CYBER SECURITY
**Institution:** MIVA OPEN UNIVERSITY
**Course Code:** COS 201 - COMPUTER PROGRAMMING I
**Project:** MID-SEMESTER LAB ASSESSMENT PROJECT (Student Course Management System)

## What the system does

The Student Course Management System lets a student keep a simple record of
the courses they are taking for the semester. The student signs in through a
split screen login window, enrols in courses, sees the courses in a table,
searches for a course by its code, watches the total credit units add up on
a progress bar, and saves or loads the whole course list from a text file.

## Objectives of the assignment

1. Design a small program around classes and objects instead of one long
   method.
2. Perform the seven required operations:
   1. Add Course (code, title, units)
   2. Display / View all courses
   3. Search course by code
   4. Compute total credit units using a recursive method
   5. Save courses to a text file
   6. Load courses from a text file
   7. Exit
3. Handle bad input and file errors with exceptions.
4. Use loops, one recursive feature, string processing and a clean layout.

## User requirements

- Sign in with a student ID, or register a new account with a name,
  programme and level of study.
- Enrol in a course by typing its code, credit units and title. The form
  checks the input while the student types: available, already enrolled,
  bad code format, prerequisite status.
- Drop a course from the Active Enrollments table with one click.
- See the total credit units on a bar that fills up to the 24 unit limit.
- Search the catalogue by course code (recursive search).
- View sample materials for the courses they are enrolled in.
- See a chart of credit units grouped by category (Core, Elective).
- Read a live log of everything the program does.
- Save and load the course list from a text file, automatically and on
  demand from Settings.

## What was seeded

One master dataset of the 200 Level First Semester courses (10 courses,
22 units) and two or three sample materials per course. The programme does
not keep a separate copy for each level: the `CourseAdaptor` relabels the
same data for whichever level a student picks during registration, so a
300 Level student sees COS 301, CYB 301, MTH 301 and so on.

## Scope limits

- No real passwords or sessions - the login checks the ID format only, as
  allowed by the brief.
- No real files are uploaded; the materials are sample entries.
- The data lives in text files, not a database.
