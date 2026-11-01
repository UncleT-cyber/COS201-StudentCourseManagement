# 03 - Recursion and Algorithms

**Student Name:** ABAH ANTHONY  |  **Student ID:** 30166082  |  **COS 201, MIVA OPEN UNIVERSITY**

The brief asks for at least one recursive feature. Two were written, both
inside `core/CourseManager.java`. They work on a plain `ArrayList` and do
not touch the interface, so they can be tested on their own with
`test/ManagerSelfTest`.

## 1. Recursive search for a course code

```java
public Course recursiveSearch(List<Course> list, int index,
                              String targetCode) {
    if (list == null || index >= list.size()) {
        return null;                                   // base case
    }
    Course current = list.get(index);
    if (Course.normalizeCode(current.getCode())
            .equals(Course.normalizeCode(targetCode))) {
        return current;                                // found it
    }
    return recursiveSearch(list, index + 1, targetCode);  // recursive step
}
```

**Base case:** the index has moved past the last element. The list is
exhausted, nothing was found, return `null`.

**Recursive step:** the current course is not the one we want, so call the
same method on the rest of the list, one position further along. Because
the index grows by one every call, the list gets smaller each time and the
base case is always reached - the recursion terminates.

**Worked trace** - searching for `COS 201` in a 3 course list:

```
recursiveSearch([A, B, C], 0, "COS 201")
  A is MTH 201 -> not a match
  recursiveSearch([A, B, C], 1, "COS 201")
      B is COS 201 -> match, return B
  <- B
<- B
```

Both sides are normalised with `Course.normalizeCode()` (trim, uppercase,
remove spaces) before comparing, so `cos 201`, `COS201` and ` COS 201 `
all find the same course. The search is used by the Search button on the
Enrollment screen and by the self test.

## 2. Recursive total credit units

```java
public int recursiveTotalUnits(List<Course> list, int index) {
    if (list == null || index >= list.size()) {
        return 0;                                       // base case
    }
    return list.get(index).getUnits()
         + recursiveTotalUnits(list, index + 1);        // recursive step
}
```

**Base case:** the index is past the end of the list, the total of "no
courses" is 0.

**Recursive step:** units of the current course plus the total of the
remaining courses.

**Worked trace** - enrolments of 3, 2 and 2 units:

```
recursiveTotalUnits([3, 2, 2], 0)
  3 + recursiveTotalUnits([3, 2, 2], 1)
        2 + recursiveTotalUnits([3, 2, 2], 2)
              2 + recursiveTotalUnits([3, 2, 2], 3)
                    -> 0   (index 3 is past the end)
              -> 2
        -> 4
  -> 7
```

`totalUnits()` simply calls `recursiveTotalUnits(enrollments, 0)` and the
result feeds the credit load bar (`total / 24`), the banner under the
table, the analytics chart and the credit limit check inside `addCourse`.

## Loops (iteration)

Loops are used where repetition is simple and clarity matters:

- building the catalogue from the seed data
- scanning the catalogue to find a code (`findCatalogueByCode`)
- filling the JTable rows in `refresh()`
- reading a file line by line with `readLine()` in a `while` loop
- grouping credit units by category for the chart
- the clock in the header, a Swing `Timer` firing every second

## Checking the two methods

```sh
javac -d out $(find src -name "*.java")
java -cp out test.ManagerSelfTest
```

The test builds a manager from the seed data and prints `PASS` for the
search on a found course, a missing course, a badly formatted code, the
total of an empty list, and the total after adding courses.
