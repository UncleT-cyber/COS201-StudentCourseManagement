# 04 - Challenges and Error Handling

**Student Name:** ABAH ANTHONY  |  **Student ID:** 30166082  |  **COS 201, MIVA OPEN UNIVERSITY**

## 1. Bad credit unit input (NumberFormatException)

The credit units box accepts text, so a student can type `three` or leave
it empty. `Integer.parseInt()` on that value throws
`NumberFormatException`. The enrol method wraps the parse in a `try /
catch` and shows *"Credit units must be a whole number from 1 to 6"*
instead of crashing, and a WARN line is written to the activity log with
the exact text that was rejected:

```java
try {
    units = Integer.parseInt(unitsField.getText().trim());
} catch (NumberFormatException ex) {
    showMessage("Credit units must be a whole number from 1 to 6.", Theme.RED);
    log.warn("Invalid credit input '" + unitsField.getText() + "' for " + code);
    return;
}
```

Even when the number parses, `addCourse()` rejects anything outside 1-6
and anything that would push the total past 24 units.

## 2. Invalid menu choices

My interface is a GUI: every operation is a button, so the program cannot
receive a menu number that does not exist - the invalid choice is
impossible by design. The same job is done by validating every value that
*can* be typed: course codes are matched against a format pattern, credit
units are parsed and range checked, and the level of study is picked from
a fixed list of four options. A wrong level still raises
`IllegalArgumentException` inside `CourseAdaptor.checkLevel()`, which the
self test checks.

## 3. Missing text file on first run (FileNotFoundException)

The first time the program runs, `data/enrollments.txt` does not exist.
Instead of crashing with an exception trace, `CourseFileIO.ensureFiles()`
creates the `data/` folder and writes the default files, and
`loadEnrollments()` treats a missing file as "no saved record yet" and
logs a WARN line the student can see in the Live System Logs panel.
Reading is always done inside `try { ... } catch (IOException e)`, and a
read failure reports the message rather than ending the program.

## 4. Broken lines inside a saved file

While testing I appended a half-finished line to `enrollments.txt` and
the loader would have crashed on the missing fields. Every line is now
split with `split("|", -1)` and checked: blank lines and `#` comments are
skipped, lines with fewer than six fields are counted as bad, and a unit
value that will not parse is skipped too. The count is reported once as
*"Skipped N unreadable enrolment line(s)"* so the student knows the file
was damaged.

## 5. Duplicate enrolments and empty fields

`addCourse()` throws `InvalidCourseException` (a checked exception, so the
compiler forces me to handle it) for an empty code, a code that does not
match the pattern, an empty title, a duplicate course, an out-of-range
unit total and an unmet prerequisite. The catch block shows the message
from the exception under the form and writes it to the log, so the same
message appears in two places.

## 6. A real bug: the Roman numeral test failed

While running the self test, `adaptTitle("Mathematical Methods I", 200,
300)` returned `Mathematical Methods IV` instead of `... II`. The array of
Roman numerals was written in reverse order, so `indexOf("I") + 1`
returned 4 instead of 1. I fixed the order of the array to
`{"I", "II", "III", "IV"}` and re-ran the test until all 25 checks
printed `PASS`. This is the clearest example of why the test file exists:
the mistake was invisible on screen but the test caught it immediately.

## 7. A compile error with multi-catch

I first wrote `catch (NumberFormatException | IllegalArgumentException)`
around the catalogue reader. `javac` refused it with *"Alternatives in a
multi-catch statement cannot be related by subclassing"* because
`NumberFormatException` already extends `IllegalArgumentException`.
Catching the parent alone fixed it - the parent handles both cases.

## 8. Two panels listening to the same data

After dropping a course on the Home table, the Courses table and the
chart still showed the old data. `CourseManager` now keeps a list of
change listeners; every panel registers itself with
`addChangeListener(this::refresh)` and the manager calls them all after
any add or drop. One change refreshes every screen.

## 9. Picture missing on another machine

The login photo is a local file (`assets/login-bg.jpg`). If someone runs
the project without it, `ImagePanel` falls back to a plain light blue
background instead of throwing - the form on the left still works.

## Summary

| Where | What can go wrong | How it is handled |
|---|---|---|
| Credit units field | non numeric text | `NumberFormatException` caught, message + WARN log |
| Course code / title | empty or malformed | `InvalidCourseException` with readable message |
| Total units | over 24 | rejected by `addCourse()` before anything is saved |
| Level of study | not 100/200/300/400 | `IllegalArgumentException` in `CourseAdaptor` |
| First run | no files yet | files created, missing file = empty record |
| Damaged file | short or broken lines | skipped and counted, reported in the log |
| Write failure | folder not writable | `IOException` caught, error dialog shown |
| Menu choices | invalid choice | impossible by design, buttons only |
