# Student Course Management System

Mid-semester lab assessment project for **COS 201 - Computer Programming I**,
MIVA Open University.

A Java Swing portal where a student signs in, enrols in courses, searches
the catalogue, tracks credit units and saves or loads the course list from
a text file. The seeded 200 Level course list adapts to whichever level a
student registers with (COS 201 becomes COS 301 for a 300 Level student).

## Requirements

- JDK 8 or newer (tested on JDK 26)
- No external libraries, no Maven, no Gradle

## Compile

Open a terminal in the project folder and run:

```sh
javac -d out $(find src -name "*.java")
```

On Windows (Command Prompt):

```cmd
dir /s /b src\*.java > sources.txt & javac -d out @sources.txt
```

## Run

```sh
java -cp out ui.Main
```

Sign in with the seeded student ID **30166082** (no password), or create an
account from the Register tab.

To open the portal straight away with the seeded account:

```sh
java -cp out ui.Main --demo
```

## Run the self test

Checks the recursive search, the recursive total, the enrolment rules and
the level adaptor. Every line should print `PASS`.

```sh
java -cp out test.ManagerSelfTest
```

## Data files

| File | Purpose |
|------|---------|
| `courses.txt` | Master course catalogue, shipped with the project |
| `data/enrollments.txt` | Your saved enrolments, written on exit |
| `data/students.txt` | Registered student accounts |

Delete `data/` to start fresh - the program recreates it on the next run.

## Project layout

```
src/
  model/    Course, CourseTemplate, Student, CourseMaterial
  core/     CourseManager, CourseAdaptor, CourseFileIO, SeedData,
            ActivityLog, InvalidCourseException
  ui/       Main, LoginFrame, DashboardFrame and the screen panels
  test/     ManagerSelfTest
docs/       Overview, design, recursion notes, challenges
screenshots/  Screenshots of the running program
```
