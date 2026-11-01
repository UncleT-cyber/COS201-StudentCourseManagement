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
import java.awt.CardLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.time.LocalDate;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.SwingConstants;
import javax.swing.Timer;

import core.ActivityLog;
import core.CourseFileIO;
import core.CourseManager;
import core.SeedData;
import model.CourseMaterial;
import model.CourseTemplate;
import model.Student;

/**
 * The main window after login: header with the welcome
 * line and the clock, sidebar with the six screens, and
 * the screen area switched with a CardLayout.
 *
 * The enrolments are saved to file when the window is
 * closed or the student logs out.
 */
public class DashboardFrame extends JFrame {

    private final Student student;
    private final ActivityLog log = new ActivityLog();
    private final List<CourseTemplate> templates;
    private final CourseManager manager;

    private final CardLayout screenCards = new CardLayout();
    private final JPanel screenBox = new JPanel(screenCards);
    private final Map<String, Theme.NavButton> navButtons =
            new LinkedHashMap<>();

    // sidebar state (expanded shows labels, collapsed is an
    // icon rail like VS Code)
    private static final int SIDEBAR_WIDE = 210;
    private static final int SIDEBAR_NARROW = 76;
    private boolean sidebarExpanded = true;
    private JPanel sidebar;
    private JLabel institutionLabel;
    private Theme.NavButton logoutButton;
    private Theme.NavButton menuButton;

    private HomePanel homePanel;
    private EnrollmentPanel enrollmentPanel;
    private CoursesPanel coursesPanel;
    private MaterialsPanel materialsPanel;
    private AnalyticsPanel analyticsPanel;
    private SettingsPanel settingsPanel;

    public DashboardFrame(Student student) {
        super("Student Course Manager - " + student.getName());
        this.student = student;

        templates = CourseFileIO.loadTemplates(log);
        manager = new CourseManager(student, templates);
        log.seedStartup(templates.size(), student.getName());
        int loaded = CourseFileIO.loadEnrollments(manager, log);
        if (loaded > 0) {
            log.info("Loaded " + loaded + " saved course(s) from "
                    + CourseFileIO.ENROLLMENTS_FILE.getFileName());
        }

        setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);

        // fit the screen: full size on a large monitor,
        // smaller on a 12 or 13 inch laptop
        Dimension screen = java.awt.Toolkit.getDefaultToolkit()
                .getScreenSize();
        int width = Math.min(1380, screen.width - 40);
        int height = Math.min(850, screen.height - 80);
        setSize(width, height);
        setMinimumSize(new Dimension(Math.min(1000, width),
                Math.min(640, height)));
        setLocationRelativeTo(null);
        setLayout(new BorderLayout());
        setBackground(Theme.BG);

        add(buildHeader(), BorderLayout.NORTH);
        add(buildSidebar(), BorderLayout.WEST);
        add(buildScreens(), BorderLayout.CENTER);

        addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent e) {
                saveAndExit();
            }
        });

        clockTimer = new Timer(1000, e -> updateClock(clockLabel));
        clockTimer.start();
        updateClock(clockLabel);

        showScreen("home");
    }

    // ------------------------------------------------------------------
    // access for the panels
    // ------------------------------------------------------------------

    public CourseManager getManager() {
        return manager;
    }

    public ActivityLog getLog() {
        return log;
    }

    public Student getStudent() {
        return student;
    }

    /** Switches the visible screen and moves the highlight
     *  on the sidebar rows. */
    public void showScreen(String key) {
        screenCards.show(screenBox, key);
        for (Map.Entry<String, Theme.NavButton> entry
                : navButtons.entrySet()) {
            entry.getValue().setActive(entry.getKey().equals(key));
        }
    }

    /** Refreshes every screen after the enrolments change. */
    public void refreshAll() {
        if (homePanel != null) {
            homePanel.refresh();
        }
        if (enrollmentPanel != null) {
            enrollmentPanel.refresh();
        }
        if (coursesPanel != null) {
            coursesPanel.refresh();
        }
        if (materialsPanel != null) {
            materialsPanel.refresh();
        }
        if (analyticsPanel != null) {
            analyticsPanel.refresh();
        }
    }

    // ------------------------------------------------------------------
    // header, sidebar, screens
    // ------------------------------------------------------------------

    private JLabel clockLabel;
    private Timer clockTimer;

    private JPanel buildHeader() {
        JPanel header = new JPanel(new BorderLayout(12, 0));
        header.setBackground(Color.WHITE);
        header.setBorder(BorderFactory.createCompoundBorder(
                javax.swing.BorderFactory.createMatteBorder(0, 0, 1, 0,
                        Theme.BORDER),
                new javax.swing.border.EmptyBorder(12, 18, 12, 18)));

        // logo and product name
        JPanel brand = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 0));
        brand.setOpaque(false);
        JPanel logo = new Theme.RoundedPanel(10);
        logo.setBackground(Theme.BLUE);
        logo.setPreferredSize(new Dimension(38, 38));
        logo.setLayout(new BorderLayout());
        JLabel logoText = Theme.label("SCM", 11, true, Color.WHITE);
        logoText.setHorizontalAlignment(SwingConstants.CENTER);
        logo.add(logoText, BorderLayout.CENTER);
        brand.add(logo);

        JPanel names = new JPanel(new BorderLayout(0, 0));
        names.setOpaque(false);
        names.add(Theme.label("Student Course Manager", 15, true,
                Theme.NAVY), BorderLayout.NORTH);
        names.add(Theme.label("Student Portal", 11, false, Theme.BLUE),
                BorderLayout.SOUTH);
        brand.add(names);
        header.add(brand, BorderLayout.WEST);

        // welcome line and clock
        JPanel welcome = new JPanel(new BorderLayout(0, 0));
        welcome.setOpaque(false);
        JLabel welcomeLabel = Theme.label(
                "Welcome back, " + student.getFirstName() + "  \uD83D\uDC4B",
                20, true, Theme.NAVY);
        welcomeLabel.setHorizontalAlignment(SwingConstants.CENTER);
        clockLabel = Theme.label("", 12, false, Theme.MUTED);
        clockLabel.setHorizontalAlignment(SwingConstants.CENTER);
        // fixed width so the header is not re-laid out every second
        clockLabel.setPreferredSize(new Dimension(380, 16));
        welcome.add(welcomeLabel, BorderLayout.NORTH);
        welcome.add(clockLabel, BorderLayout.SOUTH);
        header.add(welcome, BorderLayout.CENTER);

        // level badge
        JPanel badgeBox = new JPanel(new FlowLayout(FlowLayout.RIGHT, 0,
                0));
        badgeBox.setOpaque(false);
        Theme.Chip levelBadge = Theme.chip(student.getLevelText(),
                Theme.TEAL, Theme.TEAL_LIGHT);
        levelBadge.setFont(Theme.font(12, Font.BOLD));
        badgeBox.add(levelBadge);
        header.add(badgeBox, BorderLayout.EAST);

        return header;
    }

    private void updateClock(JLabel label) {
        String date = LocalDate.now().format(
                DateTimeFormatter.ofPattern("EEEE, MMMM d, yyyy"));
        String time = LocalTime.now().format(
                DateTimeFormatter.ofPattern("h:mm:ss a"));
        label.setText("Today is " + date + "   \u2022   " + time);
    }

    private JPanel buildSidebar() {
        sidebar = new JPanel(new BorderLayout(0, 10));
        sidebar.setBackground(Theme.SIDEBAR);
        sidebar.setPreferredSize(new Dimension(SIDEBAR_WIDE, 100));
        sidebar.setBorder(javax.swing.BorderFactory.createCompoundBorder(
                javax.swing.BorderFactory.createMatteBorder(0, 1, 0, 0,
                        Theme.BORDER),
                new javax.swing.border.EmptyBorder(12, 10, 14, 10)));

        // ---- collapse / expand toggle ----
        menuButton = new Theme.NavButton("",
                Theme.NavKind.COLLAPSE);
        menuButton.setToolTipText("Collapse sidebar");
        menuButton.setAlignmentX(LEFT_ALIGNMENT);
        menuButton.setHorizontalAlignment(SwingConstants.LEFT);
        menuButton.setPreferredSize(new Dimension(120, 38));
        menuButton.setMaximumSize(new Dimension(120, 38));
        menuButton.addActionListener(e -> toggleSidebar());
        JPanel toggleRow = new JPanel(new BorderLayout());
        toggleRow.setOpaque(false);
        toggleRow.add(menuButton, BorderLayout.WEST);
        sidebar.add(toggleRow, BorderLayout.NORTH);

        // ---- navigation rows ----
        JPanel navList = new JPanel();
        navList.setOpaque(false);
        navList.setLayout(new BoxLayout(navList, BoxLayout.Y_AXIS));

        navList.add(navButton("home", "Home", Theme.NavKind.HOME));
        navList.add(Theme.gap(2));
        navList.add(navButton("enrollment", "Enrollment",
                Theme.NavKind.ENROLLMENT));
        navList.add(Theme.gap(2));
        navList.add(navButton("courses", "Courses",
                Theme.NavKind.COURSES));
        navList.add(Theme.gap(2));
        navList.add(navButton("materials", "Materials",
                Theme.NavKind.MATERIALS));
        navList.add(Theme.gap(2));
        navList.add(navButton("analytics", "Analytics",
                Theme.NavKind.ANALYTICS));
        navList.add(Theme.gap(2));
        navList.add(navButton("settings", "Settings",
                Theme.NavKind.SETTINGS));
        navList.add(Box.createVerticalGlue());
        sidebar.add(navList, BorderLayout.CENTER);

        // ---- bottom: institution and logout ----
        JPanel bottom = new JPanel();
        bottom.setOpaque(false);
        bottom.setLayout(new BoxLayout(bottom, BoxLayout.Y_AXIS));
        institutionLabel = Theme.mutedLabel("MIVA Open University", 10);
        institutionLabel.setAlignmentX(LEFT_ALIGNMENT);
        bottom.add(institutionLabel);
        bottom.add(Theme.gap(8));
        logoutButton = new Theme.NavButton("Logout",
                Theme.NavKind.LOGOUT);
        logoutButton.setNormalColour(Theme.RED);
        logoutButton.setAlignmentX(LEFT_ALIGNMENT);
        logoutButton.setCollapsed(false);
        logoutButton.addActionListener(e -> logout());
        bottom.add(logoutButton);
        sidebar.add(bottom, BorderLayout.SOUTH);
        return sidebar;
    }

    /** Hides the labels and shrinks the sidebar to an icon
     *  rail, or brings the labels back. */
    private void toggleSidebar() {
        sidebarExpanded = !sidebarExpanded;
        int width = sidebarExpanded ? SIDEBAR_WIDE : SIDEBAR_NARROW;
        sidebar.setPreferredSize(new Dimension(width, 100));
        // flip the chevron to match the new state
        menuButton.setKind(sidebarExpanded ? Theme.NavKind.COLLAPSE
                : Theme.NavKind.EXPAND);
        menuButton.setToolTipText(sidebarExpanded ? "Collapse sidebar"
                : "Expand sidebar");
        if (sidebarExpanded) {
            menuButton.setText("\u00AB Collapse");
            menuButton.setFont(Theme.font(13));
            menuButton.setHorizontalAlignment(SwingConstants.LEFT);
            menuButton.setPreferredSize(new Dimension(120, 38));
            menuButton.setMaximumSize(new Dimension(120, 38));
        } else {
            // collapsed: one clear chevron, sized up so it is
            // easy to hit and read on small screens
            menuButton.setText("\u00BB");
            menuButton.setFont(Theme.font(17, java.awt.Font.BOLD));
            menuButton.setHorizontalAlignment(SwingConstants.CENTER);
            menuButton.setPreferredSize(new Dimension(54, 38));
            menuButton.setMaximumSize(new Dimension(54, 38));
        }
        for (Theme.NavButton button : navButtons.values()) {
            button.setCollapsed(!sidebarExpanded);
        }
        logoutButton.setCollapsed(!sidebarExpanded);
        institutionLabel.setVisible(sidebarExpanded);
        revalidate();
        repaint();
    }

    private Theme.NavButton navButton(String key, String text,
                                      Theme.NavKind kind) {
        Theme.NavButton button = new Theme.NavButton(text, kind);
        button.setAlignmentX(LEFT_ALIGNMENT);
        button.setCollapsed(false);
        button.addActionListener(e -> showScreen(key));
        navButtons.put(key, button);
        return button;
    }

    private JPanel buildScreens() {
        homePanel = new HomePanel(this);
        enrollmentPanel = new EnrollmentPanel(this);
        coursesPanel = new CoursesPanel(this);
        materialsPanel = new MaterialsPanel(this, SeedData.materials());
        analyticsPanel = new AnalyticsPanel(this);
        settingsPanel = new SettingsPanel(this);

        screenBox.setOpaque(false);
        screenBox.add(wrap(homePanel), "home");
        screenBox.add(wrap(enrollmentPanel), "enrollment");
        screenBox.add(wrap(coursesPanel), "courses");
        screenBox.add(wrap(materialsPanel), "materials");
        screenBox.add(wrap(analyticsPanel), "analytics");
        screenBox.add(wrap(settingsPanel), "settings");
        return screenBox;
    }

    /** Some screens scroll on their own, the others get a
     *  scroll pane here. */
    private JPanel wrap(JPanel panel) {
        JPanel holder = new JPanel(new BorderLayout());
        holder.setBackground(Theme.BG);
        if (panel instanceof HomePanel
                || panel instanceof MaterialsPanel
                || panel instanceof SettingsPanel) {
            holder.add(panel, BorderLayout.CENTER);
        } else {
            javax.swing.JScrollPane scroll =
                    new javax.swing.JScrollPane(panel);
            scroll.setBorder(null);
            scroll.setHorizontalScrollBarPolicy(
                    javax.swing.ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER);
            scroll.getViewport().setBackground(Theme.BG);
            holder.add(scroll, BorderLayout.CENTER);
        }
        return holder;
    }

    // ------------------------------------------------------------------
    // leaving the portal
    // ------------------------------------------------------------------

    /** Saves the record, then closes the program. */
    private void saveAndExit() {
        if (clockTimer != null) {
            clockTimer.stop();
        }
        boolean saved = CourseFileIO.saveEnrollments(student,
                manager.getEnrollments());
        if (saved) {
            System.out.println("Saved " + manager.enrolledCount()
                    + " course(s) to "
                    + CourseFileIO.ENROLLMENTS_FILE);
        } else {
            System.out.println("Warning: could not save the course list.");
        }
        System.exit(0);
    }

    /** Saves the record and returns to the login screen. */
    private void logout() {
        if (clockTimer != null) {
            clockTimer.stop();
        }
        CourseFileIO.saveEnrollments(student, manager.getEnrollments());
        dispose();
        new LoginFrame().setVisible(true);
    }
}
