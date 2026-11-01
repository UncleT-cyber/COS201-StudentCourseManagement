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
import java.awt.Color;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.GridLayout;
import java.awt.RenderingHints;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.Map;

import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JComponent;
import javax.swing.JPanel;
import javax.swing.JProgressBar;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.SwingConstants;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;

import core.CourseManager;
import model.Course;

/**
 * Home screen: enrolment form, active enrolments table,
 * student profile with the credit load bar, the analytics
 * chart and the live system logs. One screen that shows
 * the whole state of the student at a glance.
 */
public class HomePanel extends JPanel {

    private final DashboardFrame owner;
    private final CourseManager manager;

    private final EnrollFormPanel enrollForm;

    // cards are built once and re-arranged on resize
    private final JPanel content = new JPanel();
    private JPanel formCard;
    private JPanel enrolmentsCard;
    private JPanel profileCard;
    private JPanel analyticsCard;
    private JPanel logsCard;
    private JPanel stripCard;
    private LayoutMode wideLayout;

    private final DefaultTableModel tableModel;
    private final JTable enrollTable;
    private int dropHoverRow = -1;

    // mobile enrolments view (shown below 768 px)
    private JPanel enrolmentsMobilePanel;
    // centre panel of the enrolments card, swapped between
    // table view and mobile view by applyLayout()
    private JPanel enrolmentsBox;

    private final JLabel bannerLabel = Theme.label("", 12, true, Theme.BLUE);
    private final JLabel creditsLabel = Theme.label("0 / 24", 13, true,
            Theme.NAVY);
    private final JProgressBar creditBar = new JProgressBar(0,
            CourseManager.MAX_UNITS);
    private final JLabel remainingLabel = Theme.mutedLabel("", 11);
    private final JPanel statusCard = new Theme.RoundedPanel(12);
    private final JLabel statusTitle = Theme.label("", 13, true, Theme.TEXT);
    private final JLabel statusText = Theme.mutedLabel("", 11);

    private final DonutChart donut = new DonutChart();
    private final JPanel legendBox = new JPanel(new BorderLayout());
    private final JPanel analyticsStatus = new Theme.RoundedPanel(10);
    private final JLabel analyticsStatusText = Theme.messageLabel("",
            Theme.MUTED);

    private final JPanel materialInfo = new JPanel(new BorderLayout(0, 2));

    public HomePanel(DashboardFrame owner) {
        this.owner = owner;
        this.manager = owner.getManager();

        enrollForm = new EnrollFormPanel(manager, owner.getLog(),
                owner::refreshAll);

        // -------- active enrolments table --------
        tableModel = new DefaultTableModel(
                new Object[]{"Code", "Title", "Units", "Status", "Action"},
                0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        enrollTable = new JTable(tableModel);
        styleTable(enrollTable);
        enrollTable.getColumnModel().getColumn(3)
                .setCellRenderer(new StatusRenderer());
        enrollTable.getColumnModel().getColumn(4)
                .setCellRenderer(new DropRenderer());
        // proportional resize: every column shrinks together
        // down to its minimum, so the Action column can never
        // be pushed past the right edge of the card and the
        // Drop pill stays visible on every supported screen size
        enrollTable.setAutoResizeMode(
                JTable.AUTO_RESIZE_SUBSEQUENT_COLUMNS);
        enrollTable.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                int row = enrollTable.rowAtPoint(e.getPoint());
                int column = enrollTable.columnAtPoint(e.getPoint());
                if (row >= 0 && column == 4) {
                    String code = (String) enrollTable.getValueAt(row, 0);
                    if (manager.dropCourse(code)) {
                        owner.getLog().info(code + " - Dropped from enrolment");
                        owner.refreshAll();
                    }
                }
            }

            @Override
            public void mouseExited(MouseEvent e) {
                dropHoverRow = -1;
                enrollTable.setCursor(new java.awt.Cursor(
                        java.awt.Cursor.DEFAULT_CURSOR));
                enrollTable.repaint();
            }
        });

        // lights up the Drop pill when the mouse is over it
        // and shows the pointing hand cursor
        enrollTable.addMouseMotionListener(new MouseAdapter() {
            @Override
            public void mouseMoved(MouseEvent e) {
                int row = enrollTable.rowAtPoint(e.getPoint());
                int column = enrollTable.columnAtPoint(e.getPoint());
                boolean overDrop = row >= 0 && column == 4;
                int hovered = overDrop ? row : -1;
                if (hovered != dropHoverRow) {
                    dropHoverRow = hovered;
                    enrollTable.repaint();
                }
                enrollTable.setCursor(new java.awt.Cursor(
                        overDrop ? java.awt.Cursor.HAND_CURSOR
                                : java.awt.Cursor.DEFAULT_CURSOR));
            }
        });

        // -------- assemble the screen --------
        setLayout(new BorderLayout());
        setOpaque(false);

        content.setOpaque(false);
        content.setLayout(new BoxLayout(content, BoxLayout.Y_AXIS));
        content.setBorder(new javax.swing.border.EmptyBorder(18, 18, 18,
                18));

        formCard = createFormCard();
        enrolmentsCard = createEnrolmentsCard();
        profileCard = createProfileCard();
        analyticsCard = createAnalyticsCard();
        logsCard = createLogsCard();
        stripCard = createMaterialsStrip();

        // the arrangement changes between wide (three cards
        // across) and narrow (two cards across) depending
        // on how wide the window is
        addComponentListener(new java.awt.event.ComponentAdapter() {
            @Override
            public void componentResized(java.awt.event.ComponentEvent e) {
                applyLayout();
            }
        });

        JScrollPane scroll = new JScrollPane(content);
        scroll.setBorder(null);
        scroll.setHorizontalScrollBarPolicy(
                javax.swing.ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER);
        scroll.getViewport().setBackground(Theme.BG);
        scroll.getVerticalScrollBar().setUnitIncrement(16);
        add(scroll, BorderLayout.CENTER);

        applyLayout();
        manager.addChangeListener(this::refresh);
        refresh();
    }

    /**
     * Chooses the arrangement of the cards based on window width.
     *
     * Wide (>= 1080 px):  three cards across on row one,
     *                     two cards on row two, kept even on
     *                     small monitors (12.5" laptops) so the
     *                     dashboard uses the screen well.
     * Tablet (768-1079): two cards across on row one,
     *                     two cards on row two.
     * Mobile (< 768 px): one card per row, full width;
     *                     enrolments show as stacked cards
     *                     instead of a wide table.
     *
     * The five enrolment table columns are sized to fit
     * inside the enrolments card at every width, so the
     * Action column (Drop pill) stays fully visible down
     * to the smallest supported window.
     */
    private void applyLayout() {
        int width = getWidth();
        LayoutMode mode;
        if (width >= 1080) {
            mode = LayoutMode.WIDE;
        } else if (width >= 768) {
            mode = LayoutMode.TABLET;
        } else {
            mode = LayoutMode.MOBILE;
        }
        if (wideLayout != null && wideLayout == mode) {
            return;
        }
        wideLayout = mode;

        content.removeAll();
        switch (mode) {
            case WIDE: {
                JPanel rowOne = Theme.row(formCard, enrolmentsCard,
                        profileCard);
                rowOne.setAlignmentX(CENTER_ALIGNMENT);
                content.add(rowOne);
                content.add(Theme.gap(14));

                JPanel rowTwo = Theme.row(analyticsCard, logsCard);
                rowTwo.setAlignmentX(CENTER_ALIGNMENT);
                content.add(rowTwo);
                content.add(Theme.gap(14));
                break;
            }
            case TABLET: {
                JPanel rowOne = Theme.row(formCard, enrolmentsCard);
                rowOne.setAlignmentX(CENTER_ALIGNMENT);
                content.add(rowOne);
                content.add(Theme.gap(14));

                JPanel rowTwo = Theme.row(profileCard, analyticsCard);
                rowTwo.setAlignmentX(CENTER_ALIGNMENT);
                content.add(rowTwo);
                content.add(Theme.gap(14));

                logsCard.setAlignmentX(CENTER_ALIGNMENT);
                content.add(logsCard);
                content.add(Theme.gap(14));
                break;
            }
            case MOBILE: {
                content.add(formCard);
                content.add(Theme.gap(14));

                content.add(enrolmentsCard);
                content.add(Theme.gap(14));

                content.add(profileCard);
                content.add(Theme.gap(14));

                content.add(analyticsCard);
                content.add(Theme.gap(14));

                logsCard.setAlignmentX(CENTER_ALIGNMENT);
                content.add(logsCard);
                content.add(Theme.gap(14));
                break;
            }
            default:
                break;
        }
        stripCard.setAlignmentX(CENTER_ALIGNMENT);
        content.add(stripCard);

        content.revalidate();
        content.repaint();

        // swap the enrolments centre view: table on
        // desktop/tablet, stacked cards on mobile
        enrolmentsBox.removeAll();
        if (mode == LayoutMode.MOBILE) {
            buildEnrolmentsMobile();
            enrolmentsBox.add(enrolmentsMobilePanel,
                    BorderLayout.CENTER);
        } else {
            enrolmentsBox.add(enrolmentsTableView(),
                    BorderLayout.CENTER);
        }
        enrolmentsBox.revalidate();
        enrolmentsBox.repaint();
    }

    /** Layout mode selected by window width. */
    private enum LayoutMode {
        WIDE, TABLET, MOBILE
    }

    // ------------------------------------------------------------------
    // cards
    // ------------------------------------------------------------------

    private JPanel createFormCard() {
        JPanel card = Theme.card();
        card.add(header("Enrollment Portal",
                "Search and enrol in available courses"),
                BorderLayout.NORTH);
        card.add(enrollForm, BorderLayout.CENTER);
        return card;
    }

    private JPanel createEnrolmentsCard() {
        JPanel card = Theme.card(new BorderLayout(0, 10));
        card.add(header("Active Enrollments",
                "Your current course enrolments"), BorderLayout.NORTH);

        // centre panel that swaps between the desktop table and
        // the mobile card list depending on window width
        enrolmentsBox = new JPanel(new BorderLayout());
        enrolmentsBox.setOpaque(false);
        enrolmentsBox.add(enrolmentsTableView(), BorderLayout.CENTER);
        card.add(enrolmentsBox, BorderLayout.CENTER);

        enrolmentsMobilePanel = new JPanel();
        enrolmentsMobilePanel.setOpaque(false);
        enrolmentsMobilePanel.setLayout(new BoxLayout(
                enrolmentsMobilePanel, BoxLayout.Y_AXIS));

        bannerLabel.setIcon(null);
        JPanel banner = new Theme.RoundedPanel(10);
        banner.setBackground(Theme.BLUE_LIGHT);
        banner.setLayout(new BorderLayout());
        banner.setBorder(new javax.swing.border.EmptyBorder(10, 12, 10,
                12));
        banner.add(bannerLabel, BorderLayout.CENTER);
        card.add(banner, BorderLayout.SOUTH);
        return card;
    }

    /** Returns the table-based enrolments view (desktop /
     *  tablet). */
    private JPanel enrolmentsTableView() {
        JPanel box = new JPanel(new BorderLayout());
        box.setOpaque(false);
        box.add(new JScrollPane(enrollTable), BorderLayout.CENTER);
        return box;
    }

    /** Builds the mobile enrolments view: each course is a
     *  compact card row instead of a wide table row. */
    private void buildEnrolmentsMobile() {
        enrolmentsMobilePanel.removeAll();
        for (Course course : manager.getEnrollments()) {
            enrolmentsMobilePanel.add(enrolmentMobileRow(course));
            enrolmentsMobilePanel.add(Theme.gap(8));
        }
        enrolmentsMobilePanel.revalidate();
        enrolmentsMobilePanel.repaint();
    }

    /** One enrolment as a mobile card row: code + title on
     *  the left, units + status in the middle, Drop pill on
     *  the right. */
    private JPanel enrolmentMobileRow(Course course) {
        JPanel row = new JPanel(new BorderLayout(8, 0));
        row.setOpaque(false);
        row.setBorder(new javax.swing.border.EmptyBorder(7, 10, 7, 10));

        // left: code + title
        JPanel left = new JPanel(new BorderLayout(0, 2));
        left.setOpaque(false);
        JLabel codeLabel = Theme.label(course.getCode(), 12, true,
                Theme.NAVY);
        JLabel titleLabel = Theme.mutedLabel(course.getTitle(), 11);
        left.add(codeLabel, BorderLayout.NORTH);
        left.add(titleLabel, BorderLayout.SOUTH);

        // middle: units + status
        JPanel middle = new JPanel(new BorderLayout(0, 2));
        middle.setOpaque(false);
        JLabel unitsLabel = Theme.mutedLabel(
                course.getUnits() + " units", 11);
        JLabel statusLabel = Theme.label("Enrolled", 11, true,
                Theme.GREEN);
        middle.add(unitsLabel, BorderLayout.NORTH);
        middle.add(statusLabel, BorderLayout.SOUTH);

        // right: drop pill
        Theme.RoundedButton dropBtn = Theme.dropButton("Drop");
        dropBtn.addActionListener(e -> {
            if (manager.dropCourse(course.getCode())) {
                owner.getLog().info(course.getCode()
                        + " - Dropped from enrolment");
                owner.refreshAll();
            }
        });

        row.add(left, BorderLayout.WEST);
        row.add(middle, BorderLayout.CENTER);
        row.add(dropBtn, BorderLayout.EAST);

        // subtle hover fill
        row.addMouseListener(new java.awt.event.MouseAdapter() {
            @Override
            public void mouseEntered(java.awt.event.MouseEvent e) {
                row.setBackground(new Color(239, 246, 255));
            }

            @Override
            public void mouseExited(java.awt.event.MouseEvent e) {
                row.setBackground(null);
            }
        });
        return row;
    }

    private JPanel createProfileCard() {
        JPanel card = Theme.card(new BorderLayout(0, 8));

        JPanel top = new JPanel(new BorderLayout(0, 4));
        top.setOpaque(false);
        top.add(new AvatarPanel(initials(owner.getStudent().getName())),
                BorderLayout.WEST);
        JPanel who = new JPanel(new BorderLayout(0, 2));
        who.setOpaque(false);
        who.add(Theme.label(owner.getStudent().getName(), 15, true,
                Theme.NAVY), BorderLayout.NORTH);
        who.add(Theme.mutedLabel("Student ID: "
                + owner.getStudent().getId(), 11), BorderLayout.SOUTH);
        top.add(who, BorderLayout.CENTER);
        card.add(top, BorderLayout.NORTH);

        JPanel middle = new JPanel();
        middle.setOpaque(false);
        middle.setLayout(new BoxLayout(middle, BoxLayout.Y_AXIS));
        JLabel programLabel = Theme.mutedLabel(
                "Program: " + owner.getStudent().getProgram(), 11);
        programLabel.setAlignmentX(LEFT_ALIGNMENT);
        middle.add(programLabel);
        middle.add(Theme.gap(4));
        JLabel levelLabel = Theme.mutedLabel(
                "Level: " + owner.getStudent().getLevelText(), 11);
        levelLabel.setAlignmentX(LEFT_ALIGNMENT);
        middle.add(levelLabel);
        middle.add(Theme.gap(12));

        JPanel creditHead = new JPanel(new BorderLayout());
        creditHead.setOpaque(false);
        creditHead.setAlignmentX(LEFT_ALIGNMENT);
        creditHead.add(Theme.label("Credit Load", 12, true, Theme.TEXT),
                BorderLayout.WEST);
        creditHead.add(creditsLabel, BorderLayout.EAST);
        middle.add(creditHead);
        middle.add(Theme.gap(6));

        creditBar.setStringPainted(false);
        creditBar.setPreferredSize(new Dimension(120, 10));
        creditBar.setMinimumSize(new Dimension(60, 10));
        creditBar.setMaximumSize(new Dimension(Short.MAX_VALUE, 10));
        creditBar.setAlignmentX(LEFT_ALIGNMENT);
        creditBar.setForeground(Theme.BLUE);
        creditBar.setBackground(Theme.BLUE_LIGHT);
        middle.add(creditBar);
        middle.add(Theme.gap(6));
        remainingLabel.setAlignmentX(LEFT_ALIGNMENT);
        middle.add(remainingLabel);
        middle.add(Theme.gap(12));

        statusCard.setBackground(Theme.BLUE_LIGHT);
        statusCard.setLayout(new BorderLayout(0, 3));
        statusCard.setBorder(new javax.swing.border.EmptyBorder(10, 12,
                10, 12));
        statusCard.setAlignmentX(LEFT_ALIGNMENT);
        statusCard.add(statusTitle, BorderLayout.NORTH);
        statusCard.add(statusText, BorderLayout.CENTER);
        middle.add(statusCard);
        middle.add(Box.createVerticalGlue());

        card.add(middle, BorderLayout.CENTER);
        return card;
    }

    private JPanel createAnalyticsCard() {
        JPanel card = Theme.card(new BorderLayout(0, 10));
        card.add(header("Academic Analytics Visualizer",
                "Overview of your enrolled courses by category"),
                BorderLayout.NORTH);

        JPanel body = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 0));
        body.setOpaque(false);
        body.add(donut);
        legendBox.setOpaque(false);
        body.add(legendBox);
        card.add(body, BorderLayout.CENTER);

        // status strip under the chart
        analyticsStatus.setBackground(Theme.BLUE_LIGHT);
        analyticsStatus.setLayout(new BorderLayout());
        analyticsStatus.setBorder(new javax.swing.border.EmptyBorder(9,
                12, 9, 12));
        analyticsStatus.add(analyticsStatusText, BorderLayout.CENTER);
        card.add(analyticsStatus, BorderLayout.SOUTH);
        return card;
    }

    private JPanel createLogsCard() {
        JPanel card = Theme.card(new BorderLayout(0, 8));
        JPanel head = new JPanel(new BorderLayout(0, 2));
        head.setOpaque(false);
        head.add(header("Live System Logs",
                "Real-time enrolment and system activity"),
                BorderLayout.CENTER);
        JLabel connected = Theme.label("\u25CF  Live", 11, true,
                Theme.GREEN);
        head.add(connected, BorderLayout.EAST);
        card.add(head, BorderLayout.NORTH);
        card.add(new ActivityLogPanel(owner.getLog()), BorderLayout.CENTER);
        return card;
    }

    private JPanel createMaterialsStrip() {
        JPanel card = Theme.card(new BorderLayout(12, 0));
        card.setLayout(new BorderLayout(16, 0));
        JPanel text = new JPanel(new BorderLayout(0, 2));
        text.setOpaque(false);
        text.add(Theme.label(
                "\u2726  Materials for your enrolled courses", 13, true,
                Theme.NAVY), BorderLayout.NORTH);
        materialInfo.setOpaque(false);
        materialInfo.add(Theme.mutedLabel(
                "Lecture notes, slides, readings and resources", 11),
                BorderLayout.CENTER);
        text.add(materialInfo, BorderLayout.SOUTH);
        card.add(text, BorderLayout.CENTER);

        JButton viewAll = Theme.outlineButton("View all materials  \u2192");
        viewAll.addActionListener(e -> owner.showScreen("materials"));
        card.add(viewAll, BorderLayout.EAST);
        return card;
    }

    private JPanel header(String title, String subtitle) {
        JPanel head = new JPanel(new BorderLayout(0, 2));
        head.setOpaque(false);
        head.add(Theme.label(title, 15, true, Theme.NAVY),
                BorderLayout.NORTH);
        head.add(Theme.mutedLabel(subtitle, 11), BorderLayout.SOUTH);
        return head;
    }

    // ------------------------------------------------------------------
    // refresh
    // ------------------------------------------------------------------

    /** Rebuilds everything that shows course data. */
    public void refresh() {
        // table rows
        tableModel.setRowCount(0);
        for (Course course : manager.getEnrollments()) {
            tableModel.addRow(new Object[]{course.getCode(),
                course.getTitle(), course.getUnits(),
                "Enrolled", "Drop"});
        }

        // mobile enrolments card rows
        buildEnrolmentsMobile();

        // banner with the total units
        int total = manager.totalUnits();
        if (total == 0) {
            bannerLabel.setText(
                    "  No courses yet - enrol in your first course above.");
        } else {
            bannerLabel.setText("  Great job! You are enrolled in "
                    + total + " credits this semester.");
        }

        // credit load bar
        creditsLabel.setText(total + " / " + CourseManager.MAX_UNITS);
        creditBar.setValue(total);
        remainingLabel.setText(total + " credits enrolled  \u2022  "
                + manager.remainingUnits() + " remaining");

        // profile status card
        if (total == 0) {
            statusCard.setBackground(Theme.BLUE_LIGHT);
            statusTitle.setText("Get started");
            statusTitle.setForeground(Theme.BLUE);
            statusText.setText("Enrol in a course to begin the semester.");
            statusText.setForeground(Theme.MUTED);
        } else if (total >= 20) {
            statusCard.setBackground(Theme.ORANGE_BG);
            statusTitle.setText("Heavy credit load!");
            statusTitle.setForeground(Theme.ORANGE);
            statusText.setText("Only " + manager.remainingUnits()
                    + " units left before the limit.");
            statusText.setForeground(Theme.ORANGE);
        } else {
            statusCard.setBackground(Theme.GREEN_BG);
            statusTitle.setText("\u2605 You're on track!");
            statusTitle.setForeground(Theme.GREEN);
            statusText.setText("Your credit load looks good this semester.");
            statusText.setForeground(Theme.GREEN);
        }

        // analytics chart and legend
        Map<String, Integer> byCategory = manager.creditsByCategory();
        donut.setData(byCategory);
        legendBox.removeAll();
        legendBox.add(DonutChart.legend(byCategory), BorderLayout.CENTER);
        legendBox.revalidate();
        legendBox.repaint();

        // status strip under the chart
        if (total == 0) {
            analyticsStatus.setBackground(Theme.BLUE_LIGHT);
            analyticsStatusText.setForeground(Theme.BLUE);
            analyticsStatusText.setText(
                    "<html><b>Nothing enrolled yet</b> - add a course "
                    + "to see your analytics.</html>");
        } else if (byCategory.size() < 2) {
            analyticsStatus.setBackground(Theme.ORANGE_BG);
            analyticsStatusText.setForeground(Theme.ORANGE);
            analyticsStatusText.setText(
                    "<html><b>Single category</b> - all your units are "
                    + "in one category. Add an elective to spread the "
                    + "load.</html>");
        } else {
            analyticsStatus.setBackground(Theme.GREEN_BG);
            analyticsStatusText.setForeground(Theme.GREEN);
            analyticsStatusText.setText(
                    "<html><b>\u2605 Balanced</b> - your enrolment is "
                    + "well-balanced across " + byCategory.size()
                    + " categories.</html>");
        }

        // keep the enrolment form's status pill in step
        // when a course was dropped on another screen
        enrollForm.refresh();
    }

    // ------------------------------------------------------------------
    // small helpers
    // ------------------------------------------------------------------

    static void styleTable(JTable table) {
        table.setRowHeight(34);
        table.setFont(Theme.font(12));
        table.setGridColor(Theme.BORDER);
        table.setSelectionBackground(Theme.BLUE_LIGHT);
        table.setSelectionForeground(Theme.TEXT);
        table.setBackground(Color.WHITE);
        table.setFillsViewportHeight(true);
        table.setPreferredScrollableViewportSize(new Dimension(320, 140));
        table.getTableHeader().setFont(Theme.font(12, Font.BOLD));
        table.getTableHeader().setBackground(Color.WHITE);
        table.getTableHeader().setOpaque(true);
        table.getTableHeader().setPreferredSize(new Dimension(10, 30));
        // column widths sized so the five columns always fit
        // inside the enrolments card: headers and pills stay
        // un-clipped and the Drop pill is never cut off
        table.getColumnModel().getColumn(0).setPreferredWidth(72);
        table.getColumnModel().getColumn(0).setMinWidth(60);
        table.getColumnModel().getColumn(1).setPreferredWidth(140);
        table.getColumnModel().getColumn(1).setMinWidth(78);
        table.getColumnModel().getColumn(2).setPreferredWidth(52);
        table.getColumnModel().getColumn(2).setMinWidth(46);
        table.getColumnModel().getColumn(3).setPreferredWidth(70);
        table.getColumnModel().getColumn(3).setMinWidth(60);
        table.getColumnModel().getColumn(4).setPreferredWidth(68);
        table.getColumnModel().getColumn(4).setMinWidth(58);
        table.getColumnModel().getColumn(4).setMaxWidth(110);
    }

    private static String initials(String name) {
        String[] parts = name.trim().split("\\s+");
        String result = "";
        if (parts.length > 0 && !parts[0].isEmpty()) {
            result += Character.toUpperCase(parts[0].charAt(0));
        }
        if (parts.length > 1 && !parts[parts.length - 1].isEmpty()) {
            result += Character.toUpperCase(
                    parts[parts.length - 1].charAt(0));
        }
        return result;
    }

    /** Green "Enrolled" pill in the Status column. */
    private static class StatusRenderer extends DefaultTableCellRenderer {
        @Override
        public java.awt.Component getTableCellRendererComponent(
                JTable table, Object value, boolean isSelected,
                boolean hasFocus, int row, int column) {
            JLabel label = (JLabel) super.getTableCellRendererComponent(
                    table, value, isSelected, hasFocus, row, column);
            // centred, no leading spaces so the full word
            // fits inside the pill
            label.setText(String.valueOf(value));
            label.setForeground(Theme.GREEN);
            label.setBackground(isSelected ? Theme.BLUE_LIGHT
                    : Theme.GREEN_BG);
            label.setOpaque(true);
            label.setFont(Theme.font(11, Font.BOLD));
            label.setHorizontalAlignment(SwingConstants.CENTER);
            label.setBorder(new javax.swing.border.EmptyBorder(3, 4, 3,
                    4));
            return label;
        }
    }

    /** Drop button in the Action column: a soft light-pink
     *  rounded pill with red text (not solid red), sitting
     *  slightly inset inside the cell. The pill deepens on
     *  hover so it still reads as clickable. Uses a JPanel
     *  so the fill paints on every look and feel, including
     *  macOS. */
    private class DropRenderer extends JPanel
            implements javax.swing.table.TableCellRenderer {

        private int row = -1;

        DropRenderer() {
            setOpaque(false);
            setCursor(new java.awt.Cursor(java.awt.Cursor.HAND_CURSOR));
            setToolTipText("Click to drop this course");
        }

        @Override
        protected void paintComponent(Graphics g) {
            super.paintComponent(g);
            Graphics2D g2 = (Graphics2D) g.create();
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING,
                    RenderingHints.VALUE_ANTIALIAS_ON);
            boolean hovered = (row == dropHoverRow);
            // soft pill: light pink fill, slightly deeper
            // when the mouse is over it
            int x = 4;
            int y = 3;
            int w = getWidth() - 8;
            int h = getHeight() - 6;
            if (w < 10 || h < 10) {
                g2.dispose();
                return;
            }
            g2.setColor(hovered ? new Color(254, 202, 202)
                    : Theme.RED_BG);
            g2.fillRoundRect(x, y, w, h, 10, 10);
            g2.setColor(Theme.RED);
            g2.setFont(Theme.font(11, Font.BOLD));
            java.awt.FontMetrics fm = g2.getFontMetrics();
            String text = "Drop";
            g2.drawString(text, x + (w - fm.stringWidth(text)) / 2,
                    y + ((h - fm.getHeight()) / 2) + fm.getAscent());
            g2.dispose();
        }

        @Override
        public java.awt.Component getTableCellRendererComponent(
                JTable table, Object value, boolean isSelected,
                boolean hasFocus, int r, int column) {
            row = r;
            repaint();
            return this;
        }
    }

    /** Round avatar with the student's initials. */
    private static class AvatarPanel extends JPanel {
        private final String initials;

        AvatarPanel(String initials) {
            this.initials = initials;
            setOpaque(false);
            setPreferredSize(new Dimension(64, 64));
        }

        @Override
        protected void paintComponent(Graphics g) {
            Graphics2D g2 = (Graphics2D) g.create();
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING,
                    RenderingHints.VALUE_ANTIALIAS_ON);
            int size = Math.min(getWidth(), getHeight()) - 2;
            g2.setColor(Theme.TEAL);
            g2.fillOval(1, 1, size, size);
            g2.setColor(Color.WHITE);
            g2.setFont(new Font("SansSerif", Font.BOLD, 20));
            int width = g2.getFontMetrics().stringWidth(initials);
            g2.drawString(initials, (getWidth() - width) / 2,
                    getHeight() / 2 + 7);
            g2.dispose();
        }
    }
}
