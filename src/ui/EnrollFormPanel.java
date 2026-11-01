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

package ui;

import java.awt.Color;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;

import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;

import core.ActivityLog;
import core.CourseManager;
import core.InvalidCourseException;
import model.Course;

/**
 * The enrolment form used on the Home and Enrollment
 * screens: course code, credit units and course title
 * with real-time validation while the student types.
 *
 * Invalid input never crashes the program:
 *   - a bad code format turns the status pill red
 *   - a non numeric credit value is caught with
 *     NumberFormatException and reported
 *   - every rejected enrolment writes a WARN line to
 *     the activity log
 */
public class EnrollFormPanel extends JPanel {

    private static final String CODE_PATTERN =
            "^[A-Z]{2,6}(?:-[A-Z]{3})?-?\\d{3}$";

    private final CourseManager manager;
    private final ActivityLog log;
    private final Runnable afterChange;

    private final JTextField codeField = new JTextField();
    private final JTextField unitsField = new JTextField();
    private final JTextField titleField = new JTextField();

    private final Theme.Chip statusChip =
            Theme.chip("Waiting for course code", Theme.MUTED,
                    Theme.BLUE_LIGHT);
    private final JLabel prerequisiteLabel = Theme.mutedLabel("", 11);
    private final JLabel messageLabel = Theme.messageLabel("", Theme.GREEN);

    /** True while the form fills the fields itself, so
     *  its own writes are not treated as user input. */
    private boolean filling = false;
    private boolean refreshing = false;
    private boolean titleEditedByUser = false;
    private boolean unitsEditedByUser = false;

    public EnrollFormPanel(CourseManager manager, ActivityLog log,
                           Runnable afterChange) {
        this.manager = manager;
        this.log = log;
        this.afterChange = afterChange;

        setLayout(new GridBagLayout());
        setOpaque(false);

        GridBagConstraints c = new GridBagConstraints();
        c.gridx = 0;
        c.gridy = 0;
        c.anchor = GridBagConstraints.WEST;
        c.insets = new Insets(0, 0, 5, 0);
        c.fill = GridBagConstraints.HORIZONTAL;
        c.weightx = 1;

        add(Theme.label("Course Code", 12, true, Theme.TEXT), c);
        c.gridy++;
        Theme.styleField(codeField, "e.g. COS 201");
        add(codeField, c);

        c.gridy++;
        c.insets = new Insets(10, 0, 5, 0);
        add(Theme.label("Credit Units", 12, true, Theme.TEXT), c);
        c.gridy++;
        c.insets = new Insets(0, 0, 5, 0);
        Theme.styleField(unitsField, "1 - 6");
        add(unitsField, c);

        c.gridy++;
        c.insets = new Insets(10, 0, 5, 0);
        add(Theme.label("Course Title", 12, true, Theme.TEXT), c);
        c.gridy++;
        c.insets = new Insets(0, 0, 10, 0);
        Theme.styleField(titleField, "e.g. Computer Programming I");
        add(titleField, c);

        c.gridy++;
        c.insets = new Insets(0, 0, 8, 0);
        add(statusChip, c);

        c.gridy++;
        c.insets = new Insets(0, 0, 0, 0);
        add(prerequisiteLabel, c);

        c.gridy++;
        c.insets = new Insets(12, 0, 0, 0);
        JButton enrollButton = Theme.primaryButton("+  Enroll");
        add(enrollButton, c);

        c.gridy++;
        c.insets = new Insets(10, 0, 0, 0);
        add(messageLabel, c);

        c.gridy++;
        c.insets = new Insets(8, 0, 0, 0);
        add(Theme.label("\u2713  Real-time validation enabled", 11,
                false, Theme.GREEN), c);

        enrollButton.addActionListener(e -> enroll());

        // the code field drives the status pill
        DocumentListener codeWatcher = new DocumentListener() {
            @Override
            public void insertUpdate(DocumentEvent e) {
                refresh();
            }

            @Override
            public void removeUpdate(DocumentEvent e) {
                refresh();
            }

            @Override
            public void changedUpdate(DocumentEvent e) {
                refresh();
            }
        };
        codeField.getDocument().addDocumentListener(codeWatcher);

        // remember when the student overwrites an auto-filled value
        DocumentListener titleWatcher = new DocumentListener() {
            @Override
            public void insertUpdate(DocumentEvent e) {
                markTitleEdited();
            }

            @Override
            public void removeUpdate(DocumentEvent e) {
                markTitleEdited();
            }

            @Override
            public void changedUpdate(DocumentEvent e) {
                markTitleEdited();
            }
        };
        titleField.getDocument().addDocumentListener(titleWatcher);

        DocumentListener unitsWatcher = new DocumentListener() {
            @Override
            public void insertUpdate(DocumentEvent e) {
                markUnitsEdited();
            }

            @Override
            public void removeUpdate(DocumentEvent e) {
                markUnitsEdited();
            }

            @Override
            public void changedUpdate(DocumentEvent e) {
                markUnitsEdited();
            }
        };
        unitsField.getDocument().addDocumentListener(unitsWatcher);
    }

    private void markTitleEdited() {
        if (!filling) {
            titleEditedByUser = true;
        }
    }

    private void markUnitsEdited() {
        if (!filling) {
            unitsEditedByUser = true;
        }
    }

    /**
     * Updates the status pill and fills the title and
     * units of a known course code. Called on every
     * change of the code field.
     */
    public void refresh() {
        if (refreshing) {
            return;
        }
        refreshing = true;
        try {
            String compact = Course.normalizeCode(codeField.getText());

            if (compact.isEmpty()) {
                showStatus("Waiting for course code", Theme.MUTED,
                        Theme.BLUE_LIGHT);
                prerequisiteLabel.setText("");
                return;
            }
            if (!compact.matches(CODE_PATTERN)) {
                showStatus("Invalid code format (example: COS 201)",
                        Theme.RED, Theme.RED_BG);
                prerequisiteLabel.setText("");
                return;
            }

            Course known = manager.findCatalogueByCode(compact);
            if (known == null) {
                showStatus("Not in catalogue - custom course",
                        Theme.ORANGE, Theme.ORANGE_BG);
                prerequisiteLabel.setText("");
                return;
            }

            // fill in the details of a known course
            if (!titleEditedByUser
                    && !known.getTitle().equals(titleField.getText())) {
                filling = true;
                titleField.setText(known.getTitle());
                filling = false;
            }
            String units = String.valueOf(known.getUnits());
            if (!unitsEditedByUser
                    && !units.equals(unitsField.getText())) {
                filling = true;
                unitsField.setText(units);
                filling = false;
            }

            if (manager.isEnrolled(compact)) {
                showStatus("Already enrolled in " + compact,
                        Theme.ORANGE, Theme.ORANGE_BG);
            } else {
                showStatus("Available", Theme.GREEN, Theme.GREEN_BG);
            }

            String prerequisite = manager.getPrerequisiteCode(known);
            if (prerequisite == null) {
                prerequisiteLabel.setText("");
            } else if (manager.isPrerequisiteMet(known)) {
                prerequisiteLabel.setText(
                        "Prerequisite satisfied: " + prerequisite);
                prerequisiteLabel.setForeground(Theme.GREEN);
            } else {
                prerequisiteLabel.setText("Prerequisite: " + prerequisite
                        + " - enrol in it first");
                prerequisiteLabel.setForeground(Theme.ORANGE);
            }
        } finally {
            refreshing = false;
        }
    }

    private void showStatus(String text, Color foreground,
                            Color background) {
        statusChip.setText(text);
        statusChip.setColors(foreground, background);
    }

    /** Validates everything and adds the course. */
    private void enroll() {
        String code = codeField.getText();
        String title = titleField.getText();

        int units;
        try {
            units = Integer.parseInt(unitsField.getText().trim());
        } catch (NumberFormatException ex) {
            // bad credit input, e.g. "three" or an empty box
            showMessage("Credit units must be a whole number from 1 to 6.",
                    Theme.RED);
            log.warn("Invalid credit input '" + unitsField.getText()
                    + "' for " + code);
            return;
        }

        try {
            Course added = manager.addCourse(code, title, units);
            log.info(added.getCode() + " - Enrolled (" + units + " units)");
            showMessage("Enrolled in " + added.getCode() + " successfully.",
                    Theme.GREEN);
            filling = true;
            codeField.setText("");
            titleField.setText("");
            unitsField.setText("");
            filling = false;
            titleEditedByUser = false;
            unitsEditedByUser = false;
            refresh();
            afterChange.run();
        } catch (InvalidCourseException ex) {
            showMessage(ex.getMessage(), Theme.RED);
            log.warn(ex.getMessage());
        }
    }

    private void showMessage(String text, Color colour) {
        messageLabel.setForeground(colour);
        messageLabel.setText("<html>" + text + "</html>");
    }
}
