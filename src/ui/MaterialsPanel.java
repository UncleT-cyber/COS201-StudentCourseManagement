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

import java.awt.BasicStroke;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.LayoutManager;
import java.awt.Rectangle;
import java.awt.RenderingHints;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.ArrayList;
import java.util.List;

import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.Scrollable;
import javax.swing.SwingUtilities;
import javax.swing.Timer;
import javax.swing.border.EmptyBorder;

import core.CourseManager;
import model.Course;
import model.CourseMaterial;
import model.CourseTemplate;

/**
 * Materials screen: sample materials for the courses the
 * student is enrolled in, shown as a grid of cards. Each
 * card lifts and glows while the pointer rests on it.
 */
public class MaterialsPanel extends JPanel {

    private static final int CARD_HEIGHT = 158;
    private static final int CARD_MIN_WIDTH = 300;
    private static final int GRID_GAP = 16;

    private final DashboardFrame owner;
    private final CourseManager manager;
    private final List<CourseMaterial> allMaterials;
    private final JPanel grid = new MaterialGrid();

    public MaterialsPanel(DashboardFrame owner,
                          List<CourseMaterial> allMaterials) {
        this.owner = owner;
        this.manager = owner.getManager();
        this.allMaterials = allMaterials;

        setLayout(new BorderLayout());
        setOpaque(false);
        setBorder(new EmptyBorder(20, 20, 20, 20));

        add(buildHeader(), BorderLayout.NORTH);

        grid.setOpaque(false);
        JScrollPane scroll = new JScrollPane(grid);
        Theme.styleScrollPane(scroll);
        scroll.setHorizontalScrollBarPolicy(
                JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
        scroll.setBorder(new EmptyBorder(4, 0, 0, 0));
        add(scroll, BorderLayout.CENTER);

        manager.addChangeListener(this::refresh);
        refresh();
    }

    private JPanel buildHeader() {
        JPanel top = new JPanel(new BorderLayout(0, 2));
        top.setOpaque(false);
        top.add(Theme.label("Course Materials", 18, true, Theme.NAVY),
                BorderLayout.NORTH);
        top.add(Theme.mutedLabel(
                "Sample learning materials for your enrolled courses",
                12), BorderLayout.SOUTH);

        JLabel note = Theme.messageLabel(
                "Sample content for demonstration - these are not real "
                + "lecture files.", Theme.ORANGE);
        JPanel noteCard = new Theme.RoundedPanel(10);
        noteCard.setBackground(Theme.ORANGE_BG);
        noteCard.setLayout(new BorderLayout());
        noteCard.setBorder(new EmptyBorder(9, 12, 9, 12));
        noteCard.add(note, BorderLayout.CENTER);

        JPanel noteBox = new JPanel(new BorderLayout());
        noteBox.setOpaque(false);
        noteBox.setBorder(new EmptyBorder(10, 0, 10, 0));
        noteBox.add(noteCard, BorderLayout.CENTER);

        JPanel north = new JPanel(new BorderLayout());
        north.setOpaque(false);
        north.add(top, BorderLayout.NORTH);
        north.add(noteBox, BorderLayout.SOUTH);
        return north;
    }

    /** Rebuilds the grid for the current enrolments. */
    public void refresh() {
        grid.removeAll();

        if (manager.getEnrollments().isEmpty()) {
            JPanel empty = Theme.card(new BorderLayout());
            empty.add(Theme.mutedLabel(
                    "You are not enrolled in any courses yet.", 13),
                    BorderLayout.CENTER);
            grid.add(empty);
            grid.revalidate();
            grid.repaint();
            return;
        }

        for (Course course : manager.getEnrollments()) {
            CourseTemplate template = manager.getTemplate(course);
            if (template == null) {
                continue;
            }
            for (CourseMaterial material
                    : materialsOf(template.getTemplateId())) {
                grid.add(new MaterialCard(course, material));
            }
        }
        grid.revalidate();
        grid.repaint();
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

    /** Scrollable wrapper that spreads the cards over the
     *  full width of the viewport. */
    private static class MaterialGrid extends JPanel implements Scrollable {

        MaterialGrid() {
            setLayout(new CardGridLayout(GRID_GAP, CARD_HEIGHT,
                    CARD_MIN_WIDTH));
        }

        @Override
        public Dimension getPreferredScrollableViewportSize() {
            return getPreferredSize();
        }

        @Override
        public int getScrollableUnitIncrement(Rectangle visible,
                                              int orientation,
                                              int direction) {
            return 28;
        }

        @Override
        public int getScrollableBlockIncrement(Rectangle visible,
                                               int orientation,
                                               int direction) {
            return 140;
        }

        @Override
        public boolean getScrollableTracksViewportWidth() {
            return true;
        }

        @Override
        public boolean getScrollableTracksViewportHeight() {
            return false;
        }
    }

    /** Even grid: as many equal columns as fit, fixed row height. */
    private static class CardGridLayout implements LayoutManager {

        private final int gap;
        private final int rowHeight;
        private final int minWidth;

        CardGridLayout(int gap, int rowHeight, int minWidth) {
            this.gap = gap;
            this.rowHeight = rowHeight;
            this.minWidth = minWidth;
        }

        @Override
        public void addLayoutComponent(String name, Component comp) {
        }

        @Override
        public void removeLayoutComponent(Component comp) {
        }

        private int columns(int width) {
            int fit = (width + gap) / (minWidth + gap);
            return Math.max(1, fit);
        }

        @Override
        public Dimension preferredLayoutSize(Container parent) {
            int width = parent.getWidth();
            if (width <= 0) {
                width = minWidth * 3 + gap * 2;
            }
            int cols = columns(width);
            int rows = (parent.getComponentCount() + cols - 1) / cols;
            int height = rows == 0 ? 0
                    : rows * rowHeight + (rows - 1) * gap;
            return new Dimension(width, height);
        }

        @Override
        public Dimension minimumLayoutSize(Container parent) {
            return new Dimension(minWidth, rowHeight);
        }

        @Override
        public void layoutContainer(Container parent) {
            int width = parent.getWidth();
            int cols = columns(width);
            int cell = (width - (cols - 1) * gap) / cols;
            int index = 0;
            for (Component child : parent.getComponents()) {
                int row = index / cols;
                int col = index % cols;
                child.setBounds(col * (cell + gap),
                        row * (rowHeight + gap), cell, rowHeight);
                index++;
            }
        }
    }

    /** One material as a card that lifts and glows on hover. */
    private static class MaterialCard extends JPanel {

        private static final int ARC = 18;
        private static final Color HOVER_TINT =
                new Color(247, 251, 255);

        private final Timer animator;
        private float hover;
        private float target;

        MaterialCard(Course course, CourseMaterial material) {
            setOpaque(false);
            setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));
            setBorder(new EmptyBorder(18, 20, 16, 20));

            JPanel top = new JPanel(new BorderLayout(8, 0));
            top.setOpaque(false);
            top.setAlignmentX(LEFT_ALIGNMENT);
            top.add(Theme.label(course.getCode(), 11, true, Theme.BLUE),
                    BorderLayout.WEST);
            top.add(typeChip(material.getType()), BorderLayout.EAST);
            top.setMaximumSize(new Dimension(Integer.MAX_VALUE, 24));
            add(top);

            add(Box.createVerticalStrut(12));

            JLabel title = Theme.label(material.getTitle(), 13, true,
                    Theme.TEXT);
            title.setAlignmentX(LEFT_ALIGNMENT);
            add(title);

            add(Box.createVerticalStrut(4));

            JLabel courseLine = Theme.mutedLabel(course.getTitle(), 11);
            courseLine.setAlignmentX(LEFT_ALIGNMENT);
            add(courseLine);

            add(Box.createVerticalGlue());

            JPanel rule = new JPanel();
            rule.setOpaque(true);
            rule.setBackground(Theme.BORDER);
            rule.setPreferredSize(new Dimension(10, 1));
            rule.setMaximumSize(new Dimension(Integer.MAX_VALUE, 1));
            rule.setAlignmentX(LEFT_ALIGNMENT);
            add(rule);

            add(Box.createVerticalStrut(10));

            JPanel footer = new JPanel(new BorderLayout());
            footer.setOpaque(false);
            footer.setAlignmentX(LEFT_ALIGNMENT);
            footer.add(Theme.mutedLabel("Sample material", 10),
                    BorderLayout.WEST);
            footer.setMaximumSize(new Dimension(Integer.MAX_VALUE, 16));
            add(footer);

            animator = new Timer(16, e -> step());
            animator.setCoalesce(true);
            attachHover(this);
        }

        private void attachHover(Component component) {
            component.addMouseListener(new MouseAdapter() {
                @Override
                public void mouseEntered(MouseEvent e) {
                    hoverTo(true);
                }

                @Override
                public void mouseExited(MouseEvent e) {
                    java.awt.Point p = SwingUtilities.convertPoint(
                            e.getComponent(), e.getPoint(), MaterialCard.this);
                    if (!MaterialCard.this.contains(p)) {
                        hoverTo(false);
                    }
                }
            });
            if (component instanceof Container) {
                for (Component child
                        : ((Container) component).getComponents()) {
                    attachHover(child);
                }
            }
        }

        private void hoverTo(boolean on) {
            target = on ? 1f : 0f;
            if (!animator.isRunning()) {
                animator.start();
            }
        }

        private void step() {
            float delta = target - hover;
            if (Math.abs(delta) <= 0.08f) {
                hover = target;
                animator.stop();
            } else {
                hover += Math.signum(delta) * 0.12f;
            }
            repaint();
        }

        @Override
        protected void paintComponent(Graphics g) {
            Graphics2D g2 = (Graphics2D) g.create();
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING,
                    RenderingHints.VALUE_ANTIALIAS_ON);
            int w = getWidth();
            int h = getHeight();

            g2.setColor(Theme.SHADOW);
            g2.fillRoundRect(2, 2, w - 2, h - 2, ARC, ARC);

            g2.setColor(blend(Theme.CARD, HOVER_TINT, hover));
            g2.fillRoundRect(0, 0, w - 2, h - 2, ARC, ARC);

            if (hover > 0.01f) {
                g2.setColor(new Color(37, 99, 235,
                        Math.round(30 * hover)));
                g2.setStroke(new BasicStroke(3f));
                g2.drawRoundRect(1, 1, w - 4, h - 4, ARC, ARC);
            }

            g2.setColor(blend(Theme.BORDER, Theme.BLUE, hover));
            g2.setStroke(new BasicStroke(1f));
            g2.drawRoundRect(0, 0, w - 3, h - 3, ARC, ARC);
            g2.dispose();
        }

        private static Color blend(Color from, Color to, float t) {
            return new Color(
                    Math.round(from.getRed()
                            + (to.getRed() - from.getRed()) * t),
                    Math.round(from.getGreen()
                            + (to.getGreen() - from.getGreen()) * t),
                    Math.round(from.getBlue()
                            + (to.getBlue() - from.getBlue()) * t));
        }

        private static Theme.Chip typeChip(String type) {
            if ("Slides".equals(type)) {
                return Theme.chip(type, Theme.TEAL, Theme.TEAL_LIGHT);
            }
            if ("Reading".equals(type)) {
                return Theme.chip(type, Theme.ORANGE, Theme.ORANGE_BG);
            }
            return Theme.chip(type, Theme.BLUE, Theme.BLUE_LIGHT);
        }
    }
}
