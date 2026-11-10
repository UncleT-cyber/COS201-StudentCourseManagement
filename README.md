# Student Course Management System

A Java Swing application built for the **COS 201 – Computer Programming I** mid-semester lab assessment at MIVA Open University.

Students can sign in or register, search the course catalogue, enrol in courses, track credit units, and save their course list. The seeded 200 Level courses adapt to the student's level—for example, COS 201 becomes COS 301 for a 300 Level student.

## Screenshots

<table>
  <tr>
    <td align="center" colspan="2">
      <img src="screenshots/program_running.png" alt="Dashboard showing enrolled courses, credit load, analytics, and the activity log" width="800">
      <br><strong>Dashboard</strong><br>Review enrolled courses, credit units, analytics, and recent activity.
    </td>
  </tr>
  <tr>
    <td align="center" width="50%">
      <img src="screenshots/login_screen.png" alt="Sign-in screen with student ID entry and a registration option" width="380">
      <br><strong>Sign in or register</strong><br>Access the portal with a student account.
    </td>
    <td align="center" width="50%">
      <img src="screenshots/enrollment_search.png" alt="Course catalogue search showing a matching result for COS 201" width="380">
      <br><strong>Search the catalogue</strong><br>Find a course by its code.
    </td>
  </tr>
</table>

## Get started

### Install the app (no Java required)

Download the installer for your computer from the [latest release](https://github.com/UncleT-cyber/COS201-StudentCourseManagement/releases/latest). The installer includes the application and its Java runtime.

- [Windows installer](https://github.com/UncleT-cyber/COS201-StudentCourseManagement/releases/latest/download/StudentCourseManager-1.0-Windows.exe)
- [macOS disk image](https://github.com/UncleT-cyber/COS201-StudentCourseManagement/releases/latest/download/StudentCourseManager-1.0-macOS.dmg)
- [Plain Java build (`SCM.jar`)](https://github.com/UncleT-cyber/COS201-StudentCourseManagement/releases/latest/download/SCM.jar) — requires Java 8 or newer.

Sign in with the seeded student ID **30166082**; no password is required. You can also register an account from the Register tab.

## Build from source

### Requirements

- JDK 8 or newer (tested on JDK 26)
- No external libraries, Maven, or Gradle

### Compile

From the project directory, run:

```sh
javac -d out $(find src -name "*.java")
```

On Windows Command Prompt:

```cmd
dir /s /b src\*.java > sources.txt & javac -d out @sources.txt
```

### Run

```sh
java -cp out ui.Main
```

To open the portal with the seeded account:

```sh
java -cp out ui.Main --demo
```

## Run the self-test

The self-test checks recursive search and totals, enrolment rules, and level adaptation. Every line should print `PASS`.

```sh
java -cp out test.ManagerSelfTest
```

## Data files

When run from the project directory, the app keeps its data alongside the source. An installed copy stores data in a `Student Course Manager` folder inside your home directory.

| File | Purpose |
|------|---------|
| `courses.txt` | Master course catalogue shipped with the project |
| `data/enrollments.txt` | Saved enrolments, written on exit |
| `data/students.txt` | Registered student accounts |

Delete `data/` to start fresh; the app recreates it on the next run.

## Project layout

```text
src/
  model/       Course, CourseTemplate, Student, CourseMaterial
  core/        CourseManager, CourseAdaptor, CourseFileIO, SeedData,
               ActivityLog, InvalidCourseException
  ui/          Main, LoginFrame, DashboardFrame, and screen panels
  test/        ManagerSelfTest
docs/          Overview, design, recursion notes, challenges
screenshots/   Screenshots of the running program
```
