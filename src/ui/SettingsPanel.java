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

import java.awt.BorderLayout;
import java.awt.GridLayout;

import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;

import core.CourseFileIO;
import core.CourseManager;

/**
 * Settings screen: student details and the Save / Load
 * buttons for the text file operations required by the
 * brief. The record is also saved automatically when the
 * program is closed.
 */
public class SettingsPanel extends JPanel {

    private final DashboardFrame owner;
    private final CourseManager manager;

    public SettingsPanel(DashboardFrame owner) {
        this.owner = owner;
        this.manager = owner.getManager();

        setLayout(new BorderLayout(0, 14));
        setOpaque(false);
        setBorder(new javax.swing.border.EmptyBorder(18, 18, 18, 18));

        JPanel top = new JPanel(new BorderLayout(0, 2));
        top.setOpaque(false);
        top.add(Theme.label("Settings", 18, true, Theme.NAVY),
                BorderLayout.NORTH);
        top.add(Theme.mutedLabel("Student details and data files", 12),
                BorderLayout.SOUTH);
        add(top, BorderLayout.NORTH);

        JPanel centre = new JPanel();
        centre.setOpaque(false);
        centre.setLayout(new javax.swing.BoxLayout(centre,
                javax.swing.BoxLayout.Y_AXIS));
        centre.add(profileCard());
        centre.add(Theme.gap(14));
        centre.add(filesCard());
        centre.add(Theme.gap(14));

        JScrollPane scroll = new JScrollPane(centre);
        scroll.setBorder(null);
        scroll.setHorizontalScrollBarPolicy(
                javax.swing.ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER);
        scroll.getViewport().setBackground(Theme.BG);
        add(scroll, BorderLayout.CENTER);
    }

    private JPanel profileCard() {
        JPanel card = Theme.card(new GridLayout(0, 2, 10, 8));
        card.setAlignmentX(LEFT_ALIGNMENT);
        card.setMaximumSize(new java.awt.Dimension(
                Short.MAX_VALUE, 220));
        card.add(Theme.label("Student Details", 14, true, Theme.NAVY));
        card.add(Theme.mutedLabel("From your registration record", 11));
        card.add(Theme.label("Full name", 12, false, Theme.MUTED));
        card.add(Theme.label(owner.getStudent().getName(), 12, true,
                Theme.TEXT));
        card.add(Theme.label("Student ID", 12, false, Theme.MUTED));
        card.add(Theme.label(owner.getStudent().getId(), 12, true,
                Theme.TEXT));
        card.add(Theme.label("Programme", 12, false, Theme.MUTED));
        card.add(Theme.label(owner.getStudent().getProgram(), 12, true,
                Theme.TEXT));
        card.add(Theme.label("Level", 12, false, Theme.MUTED));
        card.add(Theme.label(owner.getStudent().getLevelText(), 12,
                true, Theme.TEXT));
        return card;
    }

    private JPanel filesCard() {
        JPanel card = Theme.card(new BorderLayout(0, 12));
        card.setAlignmentX(LEFT_ALIGNMENT);
        card.setMaximumSize(new java.awt.Dimension(
                Short.MAX_VALUE, 300));

        JPanel head = new JPanel(new BorderLayout(0, 2));
        head.setOpaque(false);
        head.add(Theme.label("Data Files", 14, true, Theme.NAVY),
                BorderLayout.NORTH);
        head.add(Theme.mutedLabel(
                "Save or load your course list from a text file", 11),
                BorderLayout.SOUTH);
        card.add(head, BorderLayout.NORTH);

        JPanel lines = new JPanel(new GridLayout(0, 1, 0, 6));
        lines.setOpaque(false);
        lines.add(fileLine("Course catalogue",
                CourseFileIO.CATALOG_FILE.toString()));
        lines.add(fileLine("My enrolments",
                CourseFileIO.ENROLLMENTS_FILE.toString()));
        lines.add(fileLine("Registered students",
                CourseFileIO.STUDENTS_FILE.toString()));
        card.add(lines, BorderLayout.CENTER);

        JPanel buttons = new JPanel(new java.awt.FlowLayout(
                java.awt.FlowLayout.LEFT, 10, 0));
        buttons.setOpaque(false);
        JButton saveButton = Theme.primaryButton("Save to File");
        JButton loadButton = Theme.outlineButton("Load from File");
        buttons.add(saveButton);
        buttons.add(loadButton);

        JPanel buttonBox = new JPanel(new BorderLayout(0, 6));
        buttonBox.setOpaque(false);
        buttonBox.add(buttons, BorderLayout.NORTH);
        buttonBox.add(Theme.mutedLabel(
                "Your record is also saved automatically when you "
                + "close the program.", 11), BorderLayout.SOUTH);
        card.add(buttonBox, BorderLayout.SOUTH);

        saveButton.addActionListener(e -> saveNow());
        loadButton.addActionListener(e -> loadNow());
        return card;
    }

    private JPanel fileLine(String name, String path) {
        JPanel line = new JPanel(new BorderLayout(8, 0));
        line.setOpaque(false);
        line.add(Theme.label(name, 12, true, Theme.TEXT),
                BorderLayout.WEST);
        line.add(Theme.mutedLabel(path, 11), BorderLayout.CENTER);
        return line;
    }

    /** The Save operation from the brief, on demand. */
    private void saveNow() {
        boolean ok = CourseFileIO.saveEnrollments(
                owner.getStudent(), manager.getEnrollments());
        if (ok) {
            owner.getLog().info("Saved "
                    + manager.enrolledCount() + " course(s) to "
                    + CourseFileIO.ENROLLMENTS_FILE.getFileName());
            javax.swing.JOptionPane.showMessageDialog(this,
                    "Course list saved to "
                    + CourseFileIO.ENROLLMENTS_FILE + ".",
                    "Save", javax.swing.JOptionPane.INFORMATION_MESSAGE);
        } else {
            owner.getLog().warn("Could not save the course list");
            javax.swing.JOptionPane.showMessageDialog(this,
                    "Could not write the file. Check the folder "
                    + "permissions and try again.",
                    "Save", javax.swing.JOptionPane.ERROR_MESSAGE);
        }
    }

    /** The Load operation from the brief, on demand. */
    private void loadNow() {
        int loaded = CourseFileIO.loadEnrollments(manager,
                owner.getLog());
        owner.refreshAll();
        javax.swing.JOptionPane.showMessageDialog(this,
                "Loaded " + loaded + " course(s) from "
                + CourseFileIO.ENROLLMENTS_FILE + ".",
                "Load", javax.swing.JOptionPane.INFORMATION_MESSAGE);
    }
}
