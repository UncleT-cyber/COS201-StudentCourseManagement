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
import java.awt.Dimension;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;

import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.table.DefaultTableModel;

import core.CourseManager;
import model.Course;

/**
 * Enrollment screen: the full course catalogue for the
 * student's level, a search box that uses the recursive
 * search from CourseManager, and the enrolment form.
 */
public class EnrollmentPanel extends JPanel {

    private final DashboardFrame owner;
    private final CourseManager manager;

    private final JTextField searchField = new JTextField();
    private final JLabel searchResult = Theme.messageLabel(
            "Type a course code and press Search.", Theme.MUTED);

    private final DefaultTableModel tableModel;
    private final JTable catalogueTable;
    private final EnrollFormPanel enrollForm;

    public EnrollmentPanel(DashboardFrame owner) {
        this.owner = owner;
        this.manager = owner.getManager();
        this.enrollForm = new EnrollFormPanel(manager, owner.getLog(),
                owner::refreshAll);

        tableModel = new DefaultTableModel(
                new Object[]{"Code", "Title", "Units", "Category",
                    "Status", "Prerequisite"},
                0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        catalogueTable = new JTable(tableModel);
        HomePanel.styleTable(catalogueTable);
        catalogueTable.getColumnModel().getColumn(5)
                .setCellRenderer(new PrerequisiteRenderer());

        setLayout(new BorderLayout(14, 14));
        setOpaque(false);
        setBorder(new javax.swing.border.EmptyBorder(18, 18, 18, 18));

        add(buildTop(), BorderLayout.NORTH);
        add(buildCentre(), BorderLayout.CENTER);
        add(buildSide(), BorderLayout.EAST);

        manager.addChangeListener(this::refresh);
        refresh();
    }

    private JPanel buildTop() {
        JPanel top = new JPanel(new BorderLayout(0, 8));
        top.setOpaque(false);

        JPanel titleBox = new JPanel(new BorderLayout(0, 2));
        titleBox.setOpaque(false);
        titleBox.add(Theme.label("Enrollment", 18, true, Theme.NAVY),
                BorderLayout.NORTH);
        titleBox.add(Theme.mutedLabel(
                "Course catalogue for " + owner.getStudent().getLevelText()
                + " - adapted to your registration level", 12),
                BorderLayout.SOUTH);
        top.add(titleBox, BorderLayout.NORTH);

        // search row uses the recursive search
        JPanel searchRow = new JPanel(new GridBagLayout());
        searchRow.setOpaque(false);
        GridBagConstraints c = new GridBagConstraints();
        c.gridy = 0;
        c.insets = new Insets(0, 0, 0, 8);
        c.gridx = 0;
        c.anchor = GridBagConstraints.WEST;
        searchRow.add(Theme.label("Search by course code:", 12, true,
                Theme.TEXT), c);
        c.gridx = 1;
        c.fill = GridBagConstraints.HORIZONTAL;
        c.weightx = 1;
        Theme.styleField(searchField, "e.g. COS 201");
        searchRow.add(searchField, c);
        c.gridx = 2;
        c.fill = GridBagConstraints.NONE;
        c.weightx = 0;
        JButton searchButton = Theme.primaryButton("Search");
        searchRow.add(searchButton, c);

        searchButton.addActionListener(e -> runSearch());
        searchField.addActionListener(e -> runSearch());

        JPanel searchBox = new JPanel(new BorderLayout(0, 6));
        searchBox.setOpaque(false);
        searchBox.add(searchRow, BorderLayout.NORTH);
        searchBox.add(searchResult, BorderLayout.SOUTH);
        top.add(searchBox, BorderLayout.CENTER);
        return top;
    }

    /** Calls the recursive search and reports the result
     *  on screen and in the activity log. */
    private void runSearch() {
        String target = searchField.getText().trim();
        if (target.isEmpty()) {
            searchResult.setForeground(Theme.ORANGE);
            searchResult.setText("Enter a course code to search for.");
            owner.getLog().warn("Search rejected: empty course code");
            return;
        }
        Course found = manager.searchCatalogue(target);
        if (found == null) {
            searchResult.setForeground(Theme.RED);
            searchResult.setText("No course found with code " + target
                    + ".");
            owner.getLog().warn("Search '" + target + "': not found");
        } else {
            searchResult.setForeground(Theme.GREEN);
            searchResult.setText("Found: " + found.getCode() + " - "
                    + found.getTitle() + " (" + found.getUnits()
                    + " units)");
            owner.getLog().info("Search '" + found.getCode()
                    + "': found - " + found.getTitle());
        }
    }

    private JPanel buildCentre() {
        JPanel box = new JPanel(new BorderLayout(0, 8));
        box.setOpaque(false);
        box.add(Theme.label("Available Courses", 13, true, Theme.TEXT),
                BorderLayout.NORTH);
        JScrollPane scroll = new JScrollPane(catalogueTable);
        scroll.setBorder(javax.swing.BorderFactory.createLineBorder(
                Theme.BORDER));
        box.add(scroll, BorderLayout.CENTER);
        return box;
    }

    private JPanel buildSide() {
        JPanel side = Theme.card(new BorderLayout(0, 10));
        side.setPreferredSize(new Dimension(300, 100));
        JPanel head = new JPanel(new BorderLayout(0, 2));
        head.setOpaque(false);
        head.add(Theme.label("Add a Course", 15, true, Theme.NAVY),
                BorderLayout.NORTH);
        head.add(Theme.mutedLabel(
                "Code, credit units and title", 11), BorderLayout.SOUTH);
        side.add(head, BorderLayout.NORTH);
        side.add(enrollForm, BorderLayout.CENTER);
        return side;
    }

    public void refresh() {
        tableModel.setRowCount(0);
        for (Course course : manager.getCatalogue()) {
            String prerequisite = manager.getPrerequisiteCode(course);
            if (prerequisite == null) {
                prerequisite = "-";
            }
            tableModel.addRow(new Object[]{course.getCode(),
                course.getTitle(), course.getUnits(),
                course.getCategory(),
                manager.isEnrolled(course.getCode())
                        ? "Enrolled" : "Available",
                prerequisite});
        }
        enrollForm.refresh();
    }

    /** Keeps the prerequisite column tidy: shows the
     *  adapted code or a dash. */
    private static class PrerequisiteRenderer
            extends javax.swing.table.DefaultTableCellRenderer {
        @Override
        public java.awt.Component getTableCellRendererComponent(
                JTable table, Object value, boolean isSelected,
                boolean hasFocus, int row, int column) {
            JLabel label = (JLabel) super.getTableCellRendererComponent(
                    table, value, isSelected, hasFocus, row, column);
            label.setFont(Theme.font(11));
            if ("-".equals(value)) {
                label.setForeground(Theme.MUTED);
            } else {
                label.setForeground(Theme.ORANGE);
            }
            return label;
        }
    }
}
