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
import java.awt.Font;

import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;

import core.CourseManager;
import model.Course;

/**
 * Courses screen: the full catalogue for the student's
 * level with the enrolment status of each course. This is
 * the "View All Courses" operation of the brief.
 */
public class CoursesPanel extends JPanel {

    private final DashboardFrame owner;
    private final CourseManager manager;
    private final DefaultTableModel tableModel;

    public CoursesPanel(DashboardFrame owner) {
        this.owner = owner;
        this.manager = owner.getManager();

        tableModel = new DefaultTableModel(
                new Object[]{"Code", "Title", "Units", "Category",
                    "Status", "Prerequisite"},
                0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        JTable table = new JTable(tableModel);
        HomePanel.styleTable(table);
        table.getColumnModel().getColumn(3)
                .setCellRenderer(new CategoryRenderer());
        table.getColumnModel().getColumn(4)
                .setCellRenderer(new StatusRenderer());

        setLayout(new BorderLayout(0, 12));
        setOpaque(false);
        setBorder(new javax.swing.border.EmptyBorder(18, 18, 18, 18));

        JPanel top = new JPanel(new BorderLayout(0, 2));
        top.setOpaque(false);
        top.add(Theme.label("Courses", 18, true, Theme.NAVY),
                BorderLayout.NORTH);
        top.add(Theme.mutedLabel(
                owner.getStudent().getLevelText()
                + " course list - codes are adapted to your "
                + "registration level", 12), BorderLayout.SOUTH);
        add(top, BorderLayout.NORTH);

        JScrollPane scroll = new JScrollPane(table);
        scroll.setBorder(javax.swing.BorderFactory.createLineBorder(
                Theme.BORDER));
        add(scroll, BorderLayout.CENTER);

        manager.addChangeListener(this::refresh);
        refresh();
    }

    /** Rebuilds the rows from the catalogue. */
    public void refresh() {
        tableModel.setRowCount(0);
        for (Course course : manager.getCatalogue()) {
            String prerequisite = manager.getPrerequisiteCode(course);
            tableModel.addRow(new Object[]{course.getCode(),
                course.getTitle(), course.getUnits(),
                course.getCategory(),
                manager.isEnrolled(course.getCode())
                        ? "Enrolled" : "Available",
                (prerequisite == null) ? "-" : prerequisite});
        }
    }

    /** Blue for Core, teal for Elective, orange for Other. */
    private static class CategoryRenderer
            extends DefaultTableCellRenderer {
        @Override
        public java.awt.Component getTableCellRendererComponent(
                JTable table, Object value, boolean isSelected,
                boolean hasFocus, int row, int column) {
            JLabel label = (JLabel) super.getTableCellRendererComponent(
                    table, value, isSelected, hasFocus, row, column);
            label.setFont(Theme.font(11, Font.BOLD));
            if ("Elective".equals(value)) {
                label.setForeground(Theme.TEAL);
            } else if ("Other".equals(value)) {
                label.setForeground(Theme.ORANGE);
            } else {
                label.setForeground(Theme.BLUE);
            }
            return label;
        }
    }

    /** Green when enrolled, grey when still available. */
    private static class StatusRenderer extends DefaultTableCellRenderer {
        @Override
        public java.awt.Component getTableCellRendererComponent(
                JTable table, Object value, boolean isSelected,
                boolean hasFocus, int row, int column) {
            JLabel label = (JLabel) super.getTableCellRendererComponent(
                    table, value, isSelected, hasFocus, row, column);
            label.setFont(Theme.font(11, Font.BOLD));
            if ("Enrolled".equals(value)) {
                label.setForeground(Theme.GREEN);
            } else {
                label.setForeground(Theme.MUTED);
            }
            return label;
        }
    }
}
