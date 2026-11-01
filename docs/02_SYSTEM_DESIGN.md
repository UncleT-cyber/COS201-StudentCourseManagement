# 02 - System Design

**Student Name:** ABAH ANTHONY  |  **Student ID:** 30166082  |  **COS 201, MIVA OPEN UNIVERSITY**

## Class structure

The code is split into three packages so that the data, the logic and the
screens stay separate.

```
model/            the data objects
  Course              one course as the student sees it (code, title, units, category)
  CourseTemplate      one master record (base code, base level, prerequisite)
  Student             id, name, programme, level
  CourseMaterial      sample material linked to a course template

core/             the logic
  CourseManager       holds the catalogue and the enrolments, add / drop /
                      recursive search / recursive total / credit limit
  CourseAdaptor       moves a template to the student's level (COS 201 -> COS 301)
  CourseFileIO        reads and writes courses.txt, data/enrollments.txt,
                      data/students.txt
  SeedData            the seeded courses, materials, demo student, programmes
  ActivityLog         timestamped INFO / WARN lines for the log panel
  InvalidCourseException   checked exception for rejected enrolments

ui/               the screens
  Main                entry point, look and feel, prepares the files
  LoginFrame          split screen login and registration
  DashboardFrame      header, sidebar, CardLayout host, save on exit
  HomePanel           enrolment form, enrolments table, profile, chart, logs
  EnrollmentPanel     search box (recursive), catalogue table, enrolment form
  CoursesPanel        full catalogue with status
  MaterialsPanel      sample materials for enrolled courses
  AnalyticsPanel      donut chart, credit bar, summary
  SettingsPanel       student details, Save / Load buttons
  Theme               colours, rounded cards, buttons, pill labels
```

## How the classes work together

```
Main
 └─ LoginFrame ── reads students.txt (CourseFileIO)
      └─ DashboardFrame
           ├─ CourseFileIO.loadTemplates()  -> courses.txt -> SeedData fallback
           ├─ CourseManager(student, templates)   builds this student's catalogue
           ├─ CourseFileIO.loadEnrollments()      -> data/enrollments.txt
           ├─ ActivityLog                        feeds the log panels
           └─ panels (Home, Enrollment, Courses, Materials, Analytics, Settings)
                ├─ call manager.addCourse / dropCourse / searchCatalogue
                ├─ listen with manager.addChangeListener -> refresh()
                └─ report to ActivityLog -> log panels update live
```

Flow of one enrolment:

1. The student types `COS 201` in the form.
2. `EnrollFormPanel` asks `CourseManager.findCatalogueByCode()` and turns
   the status pill green, fills the title and units.
3. The Enroll button parses the units (a `NumberFormatException` here is
   caught and reported) and calls `manager.addCourse()`.
4. `addCourse()` checks format, duplicates, the 24 unit limit and the
   prerequisite, then throws `InvalidCourseException` with a readable
   message if anything is wrong.
5. On success the manager fires its change listeners, every panel
   refreshes, and the activity log gets a new INFO line.

## Layout hierarchy (Java Swing)

```
LoginFrame (JFrame, BorderLayout)
├── WEST  form side (GridBagLayout)
│     ├── logo, title, subtitle
│     ├── Login | Register tab buttons
│     └── CardLayout formBox -> loginForm() / registerForm()
└── CENTER  ImagePanel (classroom photo with a white fade)

DashboardFrame (JFrame, BorderLayout)
├── NORTH   header: logo, name, welcome line, clock, level badge
├── WEST    sidebar: six nav buttons + MIVA Open University + Logout
└── CENTER  screenBox (CardLayout) -> home / enrollment / courses /
                                      materials / analytics / settings
      └── HomePanel
            └── JScrollPane
                  ├── Row 1 (GridLayout 1x3): form card, table card, profile card
                  ├── Row 2 (GridLayout 1x2): analytics card, logs card
                  └── materials strip
```

## File format schema

All three files are plain text, one record per line, fields separated by
`|`. Lines starting with `#` are comments.

**courses.txt** (master catalogue)

```
templateId|baseCode|baseLevel|title|units|category|prerequisite
PROGRAMMING_1|COS 201|200|Computer Programming I|3|Core|
CYB_203|CYB 203|200|Cybercrime, Law and Countermeasures|2|Core|CYB_201
```

**data/enrollments.txt** (one student's enrolments)

```
studentId|templateId|code|title|units|category
30166082|PROGRAMMING_1|COS 201|Computer Programming I|3|Core
30166082||GST 101|Community Service|2|Other
```

An empty `templateId` means the student typed the course in himself. When
the file is loaded, a line with a templateId is rebuilt from the adapted
catalogue, so the record always matches the student's level.

**data/students.txt** (registered students)

```
id|name|program|level
30166082|ABAH ANTHONY|DEPARTMENT OF CYBER SECURITY|200
```

## Level adaptation rules

- The number in the code is shifted by `studentLevel - baseLevel`
  (`COS 201` at base 200 becomes `COS 101`, `COS 301`, `COS 401`).
- Titles ending in a Roman numeral move with the level
  (`Mathematical Methods I` -> `Mathematical Methods II`).
- Exceptions live in small fixed lists inside `CourseAdaptor`:
  `CYB 299` (SIWES) and any title containing SIWES never change.
- A level that is not 100 / 200 / 300 / 400 throws
  `IllegalArgumentException`.
