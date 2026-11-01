#!/usr/bin/env python3
"""
Builds the COS 201 lab report PDF to match the submission guidelines:

  - page 0: cover page (NOT counted in the page limit)
  - pages 1-2: a SHORT report covering:
      * what the system does
      * how each major feature works
      * challenges encountered / how errors were handled
      * where recursion was applied
  - ONE screenshot showing the program running.

The source code itself is submitted as files (and published in the
repository); only the recursion excerpt is printed in the report because
"where recursion was applied" is a required section.

Usage:  python3 report/generate_report.py
Output: COS201_LAB_REPORT_ABAH_ANTHONY_30166082.pdf (project root)

Screenshots: place exactly ONE .png in screenshots/ (program running).
The first file in alphabetical order is embedded as Figure 1.
"""

import base64
import glob
import html
import os
import re
import subprocess
import sys

ROOT = os.path.dirname(os.path.dirname(os.path.abspath(__file__)))
PDF_NAME = "COS201_LAB_REPORT_ABAH_ANTHONY_30166082.pdf"
REPO_URL = "https://github.com/UncleT-cyber/COS201-StudentCourseManagement"

JAVA_KEYWORDS = [
    "abstract", "boolean", "break", "byte", "case", "catch", "char",
    "class", "const", "continue", "default", "do", "double", "else",
    "enum", "extends", "final", "finally", "float", "for", "goto",
    "if", "implements", "import", "instanceof", "int", "interface",
    "long", "native", "new", "package", "private", "protected",
    "public", "return", "short", "static", "strictfp", "super",
    "switch", "synchronized", "this", "throw", "throws", "transient",
    "try", "void", "volatile", "while", "true", "false", "null",
]


def read_lines(rel_path, start, end):
    """Reads lines start..end (1-based, inclusive) from a source file."""
    path = os.path.join(ROOT, "src", rel_path)
    with open(path, "r", encoding="utf-8") as handle:
        lines = handle.read().splitlines()
    excerpt = lines[start - 1:end]
    if not excerpt:
        raise SystemExit("empty excerpt: %s %d-%d" % (rel_path, start, end))
    return "\n".join(excerpt)


def highlight_line(line):
    """Colours one line of Java. Comment lines are grey, keywords blue."""
    stripped = line.strip()
    if stripped.startswith(("//", "/*", "*", "*/")):
        return '<span class="cmt">%s</span>' % html.escape(line)

    code, comment = line, ""
    if "//" in line and line.count('"') % 2 == 0:
        at = line.index("//")
        code, comment = line[:at], line[at:]

    kw_re = re.compile(r"\b(%s)\b" % "|".join(JAVA_KEYWORDS))
    pieces = []
    buf = ""
    i = 0
    while i < len(code):
        ch = code[i]
        if ch == '"':
            if buf:
                pieces.append(("code", buf))
                buf = ""
            j = i + 1
            while j < len(code):
                if code[j] == "\\":
                    j += 2
                    continue
                if code[j] == '"':
                    break
                j += 1
            pieces.append(("str", code[i:j + 1]))
            i = j + 1
            continue
        buf += ch
        i += 1
    if buf:
        pieces.append(("code", buf))

    out = []
    for kind, text in pieces:
        escaped = html.escape(text)
        if kind == "str":
            out.append(escaped)
        else:
            out.append(kw_re.sub(
                lambda m: '<span class="kw">%s</span>' % m.group(1),
                escaped))
    result = "".join(out)
    if comment:
        result += '<span class="cmt">%s</span>' % html.escape(comment)
    return result


def code_block(rel_path, start, end, caption):
    """Builds a numbered, highlighted code listing from a source file."""
    raw = read_lines(rel_path, start, end)
    rows = []
    for offset, line in enumerate(raw.splitlines()):
        rows.append('<span class="ln">%d</span>%s'
                    % (start + offset, highlight_line(line)))
    return (
        '<figure class="code">'
        '<figcaption><b>%s</b>, <code>src/%s</code>, '
        'lines %d to %d</figcaption>'
        '<pre>%s</pre></figure>'
        % (html.escape(caption), html.escape(rel_path), start, end,
           "\n".join(rows))
    )


def screenshot_block():
    """Embeds ONE screenshot (first .png in screenshots/) as Figure 1.
    The guidelines ask for a single screenshot of the program running."""
    shot_dir = os.path.join(ROOT, "screenshots")
    files = sorted(f for f in os.listdir(shot_dir)
                   if f.lower().endswith(".png"))
    if not files:
        return (
            '<div class="shot-missing">Place one screenshot of the program '
            'running in <code>screenshots/</code>, then run '
            '<code>python3 report/generate_report.py</code> again.</div>')
    if len(files) > 1:
        print("note: %d PNGs in screenshots/ - embedding only the first "
              "(%s); the guidelines ask for one screenshot"
              % (len(files), files[0]))
    with open(os.path.join(shot_dir, files[0]), "rb") as handle:
        data = base64.b64encode(handle.read()).decode("ascii")
    return (
        '<figure class="shot">'
        '<img src="data:image/png;base64,%s" alt="Program running">'
        '<figcaption>Figure 1: The program running, signed in to the '
        'Student Course Management System dashboard.</figcaption>'
        '</figure>'
        % data
    )


CSS = """
@page { size: A4; margin: 16mm; }
* { box-sizing: border-box; }
body { font-family: Georgia, 'Times New Roman', serif; font-size: 9.4pt;
       color: #1a1a1a; line-height: 1.42; margin: 0; }
/* no position:fixed elements - they repeat on every A4 page and
   overlap content. Identity lives in the masthead instead. */
h1, h2, h3 { font-family: Helvetica, Arial, sans-serif; }
h2 { font-size: 10.5pt; margin: 9px 0 4px; padding-bottom: 2px;
     border-bottom: 1pt solid #2456a6; color: #2456a6;
     page-break-after: avoid; break-after: avoid; }
p { margin: 4px 0; text-align: justify; }
li { page-break-inside: avoid; break-inside: avoid; }
a { color: #2456a6; text-decoration: none; word-break: break-all; }
ul { margin: 5px 0 5px 18px; padding: 0; }
li { margin: 3px 0; text-align: justify; }

/* ---- cover (page 0, not counted in the 1-2 page limit) ---- */
.cover { text-align: center; padding-top: 48px; page-break-after: always; }
.cover .rule { width: 130px; height: 3px; background: #2456a6;
     margin: 16px auto; }
.cover .uni { font-family: Helvetica, Arial, sans-serif; font-size:
     13pt; letter-spacing: 2px; font-weight: bold; }
.cover .course { font-family: Helvetica, Arial, sans-serif; font-size:
     10.5pt; margin-top: 6px; color: #444; }
.cover .title { font-family: Helvetica, Arial, sans-serif; font-size:
     20pt; font-weight: bold; margin: 42px 10px 6px; }
.cover .subtitle { font-size: 11.5pt; color: #444; font-style: italic; }
.cover table { margin: 44px auto 0; border-collapse: collapse;
     font-size: 10.5pt; }
.cover td { padding: 5px 14px; text-align: left; }
.cover td.k { font-family: Helvetica, Arial, sans-serif; font-size:
     9pt; text-transform: uppercase; letter-spacing: 1px; color: #666;
     text-align: right; }
.cover td.v { font-weight: bold; }
.cover .date { margin-top: 44px; color: #555; font-size: 10pt; }
.cover .copy { position: relative; margin-top: 90px; font-size: 8pt;
     color: #888; font-family: Helvetica, Arial, sans-serif; }

/* ---- compact header ---- */
.masthead { border-bottom: 2pt solid #2456a6; padding-bottom: 6px;
       margin-bottom: 8px; }
.masthead .uni { font-family: Helvetica, Arial, sans-serif; font-size:
       11.5pt; letter-spacing: 1.5px; font-weight: bold; }
.masthead .dept { font-family: Helvetica, Arial, sans-serif; font-size:
       8.8pt; color: #444; margin-top: 2px; }
.masthead .title { font-family: Helvetica, Arial, sans-serif; font-size:
       13.5pt; font-weight: bold; margin-top: 7px; }
.meta { width: 100%; border-collapse: collapse; font-size: 8.8pt;
       margin: 6px 0 2px; }
.meta td { padding: 1px 10px 1px 0; }
.meta td.k { font-family: Helvetica, Arial, sans-serif; color: #666;
       text-transform: uppercase; font-size: 7.6pt; letter-spacing: .5px; }
.meta td.v { font-weight: bold; padding-right: 18px; }

/* ---- feature table ---- */
table.feat { width: 100%; border-collapse: collapse; margin: 5px 0;
     font-size: 8.3pt; }
table.feat th { background: #2456a6; color: #fff; font-family:
     Helvetica, Arial, sans-serif; font-size: 7.9pt; text-align: left;
     padding: 3px 5px; }
table.feat td { border-bottom: 0.5pt solid #ddd; padding: 2.5px 5px;
     vertical-align: top; }
table.feat tr:nth-child(even) td { background: #f4f7fc; }
table.feat td.f { font-weight: bold; white-space: nowrap; }
table.feat code { font-size: 8pt; }

/* ---- code excerpt ---- */
figure.code { margin: 6px 0 8px; }
figure.code figcaption { font-family: Helvetica, Arial, sans-serif;
     font-size: 7.8pt; color: #555; margin-bottom: 3px; }
figure.code figcaption code { background: #eef2f8; padding: 1px 4px;
     border-radius: 3px; }
figure.code pre { background: #f7f8fa; border: 0.6pt solid #d8dde6;
     border-left: 2.5pt solid #2456a6; border-radius: 4px;
     font-family: Menlo, Consolas, monospace; font-size: 7.2pt;
     line-height: 1.32; padding: 6px 8px; overflow: hidden;
     white-space: pre-wrap; word-wrap: break-word; margin: 0; }
.ln { display: inline-block; width: 24px; color: #9aa3b0;
     user-select: none; }
.kw { color: #2456a6; font-weight: bold; }
.cmt { color: #5a8f5a; font-style: italic; }

/* ---- screenshot ---- */
figure.shot { margin: 7px 0; text-align: center; page-break-inside:
     avoid; }
figure.shot img { max-width: 100%; max-height: 80mm; border: 0.6pt
     solid #ccc; border-radius: 3px; }
figure.shot figcaption { font-family: Helvetica, Arial, sans-serif;
     font-size: 7.8pt; color: #555; margin-top:3px; }
.shot-missing { border: 1pt dashed #b9c2cf; border-radius: 5px;
     padding: 10px; color: #667; font-size: 8.5pt; font-family:
     Helvetica, Arial, sans-serif; text-align: center; }

.note { background: #f4f7fc; border-left: 2.5pt solid #2456a6;
     padding: 5px 9px; margin: 6px 0; font-size: 8.8pt; }
.note .steps { font-size: 8.3pt; line-height: 1.5; margin: 5px 0 0; }
.footer-note { font-size: 8pt; color: #666; margin-top: 12px;
     border-top: 0.5pt solid #ccc; padding-top: 10px;
     line-height: 1.55; }
.footer-note p { margin: 0 0 5px; }
.footer-note .signoff { font-style: italic; color: #888;
     margin-bottom: 0; }
"""


def build_html():
    body = []

    # ---------------- cover: page 0, not part of the 1-2 page limit ----
    body.append("""
<div class="cover">
  <div class="uni">MIVA OPEN UNIVERSITY</div>
  <div class="course">DEPARTMENT OF CYBER SECURITY<br>
       COS 201: COMPUTER PROGRAMMING I</div>
  <div class="rule"></div>
  <div class="title">Student Course Management System</div>
  <div class="subtitle">Mid-Semester Laboratory Assessment<br>
       Lab Report</div>
  <table>
    <tr><td class="k">Student Name</td><td class="v">ABAH ANTHONY</td></tr>
    <tr><td class="k">Matric Number</td><td class="v">30166082</td></tr>
    <tr><td class="k">Level</td><td class="v">200 LEVEL</td></tr>
    <tr><td class="k">Department</td><td class="v">CYBER SECURITY</td></tr>
    <tr><td class="k">Course</td><td class="v">COS 201 (Semester 1)</td></tr>    <tr><td class="k">Source Code</td>
        <td class="v" style="font-size:9pt;">
          <a href="https://github.com/UncleT-cyber/COS201-StudentCourseManagement"
             style="color:#2456a6; text-decoration:underline;">
            https://github.com/UncleT-cyber/COS201-StudentCourseManagement
          </a></td></tr>
  </table>
  <div class="date">October 2026</div>
  <div class="copy">&copy; Copyright 2025 MIVA Open University.
       All Rights Reserved</div>
</div>
""")

    # ---------------- content page 1: header (no fixed elements) ----
    body.append("""
<div class="masthead">
  <div class="uni">MIVA OPEN UNIVERSITY</div>
  <div class="dept">DEPARTMENT OF CYBER SECURITY &middot; COS 201
       COMPUTER PROGRAMMING I &middot; MID-SEMESTER LABORATORY
       ASSESSMENT</div>
  <div class="title">Student Course Management System Lab Report</div>
</div>
<table class="meta">
  <tr>
    <td class="k">Name</td><td class="v">ABAH ANTHONY</td>
    <td class="k">Matric No</td><td class="v">30166082</td>
    <td class="k">Level</td><td class="v">200 LEVEL</td>
    <td class="k">Date</td><td class="v">October 2026</td>
  </tr>
</table>
""")

    # ---------------- 1. what the system does ----------------
    body.append("""
<h2>1. What the system does</h2>
<p>The Student Course Management System is a desktop program that lets a
student manage the courses carried in a semester. After signing in with a
matric number, the student enrols in courses from the catalogue, sees all
enrolled courses in one table, searches by course code, and watches the
total credit units build up against the 24-unit limit. The course list is
saved to a plain text file (<code>courses.txt</code>) and loaded back the
next time the program starts, so nothing is lost between sessions. The
program is written in Java using only the standard library (Swing), so it
compiles with plain <code>javac</code> and needs no external libraries.
The code is organised into four packages, so each part has one job:
<code>model</code> holds the data, <code>core</code> holds the rules,
recursion and file handling, <code>ui</code> is the interface, and
<code>test</code> is the self-test.</p>
""")

    # ---------------- 2. how each major feature works ----------------
    body.append("""
<h2>2. How each major feature works</h2>
<table class="feat">
<tr><th style="width:20%">Feature</th><th>How it works</th></tr>
<tr><td class="f">1. Add a course</td>
    <td>The Enrolment form passes the typed code, title and units to
    <code>CourseManager.addCourse()</code>, which checks the code is a
    known course, the title is not empty, the units are a number, the
    24-unit limit is not exceeded and prerequisites are met; only
    then is the <code>Course</code> object added to the list.</td></tr>
<tr><td class="f">2. View all courses</td>
    <td>The Home screen table is filled by looping over the enrolment list
    and printing each course's code, title, units and status. It refreshes
    automatically after every add or drop through the manager's change
    listener.</td></tr>
<tr><td class="f">3. Search by code</td>
    <td>The search box calls <code>searchCatalogue(code)</code>. The typed
    text is trimmed, upper-cased and stripped of spaces
    (<code>Course.normalizeCode()</code>), so <code>cos 201</code> and
    <code>COS 201</code> find the same course; the comparison itself is
    the recursive search in section 4.</td></tr>
<tr><td class="f">4. Compute total units</td>
    <td><code>totalUnits()</code> runs the recursive total over the
    enrolment list and drives the credit progress bar (e.g. 13 of 24
    units, 54%). It is recalculated after every enrolment and drop.</td></tr>
<tr><td class="f">5. Save to file</td>
    <td><code>CourseFileIO.saveEnrollments()</code> writes one course per
    line to <code>courses.txt</code> with <code>|</code> between fields,
    using a <code>BufferedWriter</code> in try-with-resources so the file
    is always closed. Saving also happens automatically on exit.</td></tr>
<tr><td class="f">6. Load from file</td>
    <td><code>loadEnrollments()</code> reads the file back at start-up and
    from the Load button. A line that cannot be parsed is counted and
    skipped instead of stopping the load; a missing file falls back to the
    seeded catalogue.</td></tr>
<tr><td class="f">7. Exit</td>
    <td>Logout / Exit saves the course list, stops the clock and autosave
    timers, then closes the window, so the file on disk is always up to
    date.</td></tr>
</table>
""")
    body.append(screenshot_block())

    # ---------------- 3. challenges / error handling ----------------
    body.append("""
<h2>3. Challenges encountered and how errors were handled</h2>
<ul>
<li><b>Invalid student input.</b> Every field can be typed wrongly, so
validation runs before anything touches the data. A non-numeric credit
value raises <code>NumberFormatException</code>, caught in the form and
shown as a message. Empty titles, unknown codes and breaking the 24-unit
limit raise the custom <i>checked</i> exception
<code>InvalidCourseException</code>, thrown by
<code>addCourse()</code> <i>before</i> the list is changed, so a rejected
enrolment never leaves a half-updated record. The interface only catches
and displays, so the program never crashes on bad input. The login
screen applies the same idea: regular expressions check the matric number
and name before sign-in proceeds.</li>
<li><b>File errors.</b> Save and load use try-with-resources so handles
are closed even if an operation fails. Malformed lines are skipped and
counted rather than aborting the load, and a missing or unreadable file
falls back to the seeded catalogue, so a damaged file cannot stop the
program from starting.</li>
<li><b>Presenting the catalogue at any level.</b> The seed catalogue is
written for 200 Level; supporting other levels without duplicating the
data was solved with a regular-expression adaptor that shifts the level
number in each code (SIWES courses are pinned and never shifted).</li>
</ul>
""")

    # ---------------- 4. where recursion was applied ----------------
    body.append("""
<h2>4. Where recursion was applied</h2>
<p>Recursion is applied in two required places, both in
<code>core/CourseManager.java</code>. <b>recursiveSearch</b> finds a
course code: the <i>base case</i> is the index running past the end of
the list (return <code>null</code>); the <i>recursive step</i> checks the
current course and, if it is not a match, calls itself with
<code>index + 1</code>. <b>recursiveTotalUnits</b> adds up the credit
units: the base case returns 0 past the end, and the recursive step
returns the current course's units plus the total of the rest. The public
methods <code>searchCatalogue()</code> and <code>totalUnits()</code>
start the recursion at index 0.</p>
""")
    body.append("""
<div class="note"><b>Worked example, <code>recursiveTotalUnits</code>
on [COS 201 (3 units), MTH 201 (2 units)]</b>
<pre class="steps">total(0) = 3 + total(1)   course 0 is COS 201
total(1) = 2 + total(2)   course 1 is MTH 201
total(2) = 0              base case, the index is past the end

total(1) = 2 + 0 = 2      the calls unwind
total(0) = 3 + 2 = <b>5 units</b></pre></div>
""")

    # ---------------- footer ----------------
    body.append("""
<div class="footer-note">
<p>All source code files are submitted through the repository:
<a href="%s">%s</a>. The compiled program <code>SCM.jar</code> is
attached to the repository's <i>Releases</i> page and runs on any
computer with Java 8 or newer (<code>java -jar SCM.jar</code>).
The project was built with plain <code>javac</code> and
<code>java</code>, and its self-test passes <b>25 of 25 checks</b>.</p>
<p class="signoff">ABAH ANTHONY, 30166082<br>
Department of Cyber Security, MIVA Open University</p>
</div>
""" % (REPO_URL, REPO_URL))

    html_doc = (
        "<!DOCTYPE html><html><head><meta charset=\"utf-8\">"
        "<title>COS 201 Lab Report - ABAH ANTHONY 30166082</title>"
        "<style>%s</style></head><body>"
        "%s</body></html>"
        % (CSS, "\n".join(body)))
    return html_doc


def pdf_page_count(pdf_path):
    """Best-effort page count from the PDF page-tree /Count entries."""
    try:
        with open(pdf_path, "rb") as handle:
            data = handle.read()
        counts = [int(c) for c in re.findall(rb"/Count\s+(\d+)", data)]
        return max(counts) if counts else None
    except OSError:
        return None


def main():
    html_path = os.path.join(ROOT, "report", "report.html")
    with open(html_path, "w", encoding="utf-8") as handle:
        handle.write(build_html())

    pdf_path = os.path.join(ROOT, PDF_NAME)
    chrome = ("/Applications/Google Chrome.app/Contents/MacOS/"
              "Google Chrome")
    subprocess.run([
        chrome, "--headless", "--disable-gpu",
        "--no-pdf-header-footer",
        "--virtual-time-budget=10000",
        "--print-to-pdf=" + pdf_path,
        "file://" + html_path,
    ], check=True, capture_output=True)
    size = os.path.getsize(pdf_path)
    pages = pdf_page_count(pdf_path)
    # page 0 is the cover (not counted); the guideline allows 2 pages
    content_pages = (pages - 1) if pages else None
    print("Report written: %s (%.0f KB) - cover + %s content page(s)"
          % (pdf_path, size / 1024,
             content_pages if content_pages is not None else "?"))
    if content_pages and content_pages > 2:
        print("WARNING: content is %d pages; the guidelines allow pages "
              "1-2 (cover excluded)." % content_pages)
        sys.exit(1)


if __name__ == "__main__":
    main()
