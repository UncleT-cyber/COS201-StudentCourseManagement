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
import java.awt.FlowLayout;
import java.util.ArrayList;
import java.util.List;

import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;

import core.CourseManager;
import model.Course;
import model.CourseMaterial;
import model.CourseTemplate;

/**
 * Materials screen: sample materials for the courses the
 * student is enrolled in. Materials are linked to the
 * course template, so a 300 Level student sees them under
 * COS 301 while a 200 Level student sees COS 201.
 */
public class MaterialsPanel extends JPanel {

    private final DashboardFrame owner;
    private final CourseManager manager;
    private final List<CourseMaterial> allMaterials;
    private final JPanel listBox = new JPanel();

    public MaterialsPanel(DashboardFrame owner,
                          List<CourseMaterial> allMaterials) {
        this.owner = owner;
        this.manager = owner.getManager();
        this.allMaterials = allMaterials;

        setLayout(new BorderLayout());
        setOpaque(false);
        setBorder(new javax.swing.border.EmptyBorder(18, 18, 18, 18));

        JPanel top = new JPanel(new BorderLayout(0, 2));
        top.setOpaque(false);
        top.add(Theme.label("Course Materials", 18, true, Theme.NAVY),
                BorderLayout.NORTH);
        top.add(Theme.mutedLabel(
                "Sample learning materials for your enrolled courses",
                12), BorderLayout.SOUTH);
        JPanel noteBox = new JPanel(new BorderLayout());
        noteBox.setOpaque(false);
        noteBox.setBorder(new javax.swing.border.EmptyBorder(10, 0, 10,
                0));
        JLabel note = Theme.messageLabel(
                "Sample content for demonstration - these are not real "
                + "lecture files.", Theme.ORANGE);
        JPanel noteCard = new Theme.RoundedPanel(10);
        noteCard.setBackground(Theme.ORANGE_BG);
        noteCard.setLayout(new BorderLayout());
        noteCard.setBorder(new javax.swing.border.EmptyBorder(9, 12, 9,
                12));
        noteCard.add(note, BorderLayout.CENTER);
        noteBox.add(noteCard, BorderLayout.CENTER);

        JPanel north = new JPanel(new BorderLayout(0, 0));
        north.setOpaque(false);
        north.add(top, BorderLayout.NORTH);
        north.add(noteBox, BorderLayout.SOUTH);
        add(north, BorderLayout.NORTH);

        listBox.setOpaque(false);
        listBox.setLayout(new BoxLayout(listBox, BoxLayout.Y_AXIS));
        JScrollPane scroll = new JScrollPane(listBox);
        scroll.setBorder(null);
        scroll.setHorizontalScrollBarPolicy(
                javax.swing.ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER);
        scroll.getViewport().setBackground(Theme.BG);
        add(scroll, BorderLayout.CENTER);

        manager.addChangeListener(this::refresh);
        refresh();
    }

    /** Rebuilds the list for the current enrolments. */
    public void refresh() {
        listBox.removeAll();

        if (manager.getEnrollments().isEmpty()) {
            listBox.add(Theme.mutedLabel(
                    "You are not enrolled in any courses yet.", 13));
            listBox.add(Theme.gap(8));
            listBox.revalidate();
            listBox.repaint();
            return;
        }

        for (Course course : manager.getEnrollments()) {
            listBox.add(courseSection(course));
            listBox.add(Theme.gap(10));
        }
        listBox.revalidate();
        listBox.repaint();
    }

    /** One course with its materials under it. */
    private JPanel courseSection(Course course) {
        JPanel section = Theme.card(new BorderLayout(0, 8));
        section.setMaximumSize(new Dimension(Short.MAX_VALUE, 200));
        section.setAlignmentX(LEFT_ALIGNMENT);

        JPanel head = new JPanel(new BorderLayout(0, 2));
        head.setOpaque(false);
        head.add(Theme.label(course.getCode(), 13, true, Theme.NAVY),
                BorderLayout.WEST);
        JLabel title = Theme.mutedLabel(course.getTitle(), 11);
        head.add(title, BorderLayout.CENTER);
        section.add(head, BorderLayout.NORTH);

        CourseTemplate template = manager.getTemplate(course);
        List<CourseMaterial> materials = (template == null)
                ? new ArrayList<>()
                : materialsOf(template.getTemplateId());

        JPanel body = new JPanel();
        body.setOpaque(false);
        body.setLayout(new BoxLayout(body, BoxLayout.Y_AXIS));

        if (materials.isEmpty()) {
            body.add(Theme.mutedLabel(
                    "No sample materials attached to this course.", 11));
        } else {
            for (CourseMaterial material : materials) {
                body.add(materialRow(material));
                body.add(Theme.gap(4));
            }
        }
        section.add(body, BorderLayout.CENTER);
        return section;
    }

    /** One material line: title, type pill, sample note. */
    private JPanel materialRow(CourseMaterial material) {
        JPanel row = new JPanel(new FlowLayout(FlowLayout.LEFT, 8, 2));
        row.setOpaque(false);
        row.setAlignmentX(LEFT_ALIGNMENT);

        JLabel title = Theme.label(material.getTitle(), 12, false,
                Theme.TEXT);
        row.add(title);

        String type = material.getType();
        Theme.Chip typeChip;
        if ("Slides".equals(type)) {
            typeChip = Theme.chip(type, Theme.TEAL, Theme.TEAL_LIGHT);
        } else if ("Reading".equals(type)) {
            typeChip = Theme.chip(type, Theme.ORANGE, Theme.ORANGE_BG);
        } else {
            typeChip = Theme.chip(type, Theme.BLUE, Theme.BLUE_LIGHT);
        }
        row.add(typeChip);
        row.add(Theme.mutedLabel("sample material", 10));
        row.add(Box.createHorizontalGlue());
        return row;
    }

    private List<CourseMaterial> materialsOf(String templateId) {
        List<CourseMaterial> result = new ArrayList<>();
        for (CourseMaterial material : allMaterials) {
            if (material.getTemplateId().equals(templateId)) {
                result.add(material);
            }
        }
        return result;
    }
}
