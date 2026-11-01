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
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Image;
import java.awt.Insets;
import java.awt.RenderingHints;
import java.awt.image.BufferedImage;
import java.io.File;
import java.util.List;

import javax.imageio.ImageIO;
import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.SwingConstants;
import javax.swing.border.EmptyBorder;

import core.CourseFileIO;
import core.SeedData;
import model.Student;

/**
 * Split screen login window: sign in on the left, create
 * an account on the second tab, classroom photo on the
 * right.
 *
 * No password is used; this is a lab prototype. The
 * student ID is validated for format and must match a
 * registered student.
 */
public class LoginFrame extends JFrame {

    private final List<Student> students = CourseFileIO.loadStudents();

    private final JTextField idField = new JTextField();
    private final JLabel loginError = Theme.messageLabel("", Theme.RED);

    private final JTextField regIdField = new JTextField();
    private final JTextField regNameField = new JTextField();
    private final JComboBox<String> programBox;
    private final JComboBox<Integer> levelBox;
    private final JLabel registerError = Theme.messageLabel("", Theme.RED);

    private final CardLayout formCards = new CardLayout();
    private final JPanel formBox = new JPanel(formCards);
    private final JButton loginTab = tabButton("Login");
    private final JButton registerTab = tabButton("Register");

    public LoginFrame() {
        super("Student Course Manager - Student Portal");
        programBox = new JComboBox<>(SeedData.programs().toArray(
                new String[0]));
        levelBox = new JComboBox<>(new Integer[]{100, 200, 300, 400});

        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        // fit the screen so the split view works on
        // 12 and 13 inch laptops as well
        Dimension screen = java.awt.Toolkit.getDefaultToolkit()
                .getScreenSize();
        int width = Math.min(1140, screen.width - 40);
        int height = Math.min(740, screen.height - 80);
        setSize(width, height);
        setMinimumSize(new Dimension(Math.min(900, width),
                Math.min(580, height)));
        setLocationRelativeTo(null);
        setLayout(new BorderLayout());

        add(buildFormSide(), BorderLayout.WEST);
        add(new ImagePanel(), BorderLayout.CENTER);

        wireActions();
    }

    // ------------------------------------------------------------------
    // left side
    // ------------------------------------------------------------------

    private JPanel buildFormSide() {
        JPanel side = new JPanel();
        side.setBackground(Color.WHITE);
        side.setPreferredSize(new Dimension(470, 100));
        side.setLayout(new GridBagLayout());

        GridBagConstraints c = new GridBagConstraints();
        c.gridx = 0;
        c.gridy = 0;
        c.insets = new Insets(0, 0, 0, 0);
        c.fill = GridBagConstraints.HORIZONTAL;
        c.weightx = 1;

        // logo
        JPanel logo = new Theme.RoundedPanel(14);
        logo.setBackground(Theme.BLUE);
        logo.setPreferredSize(new Dimension(56, 56));
        logo.setLayout(new BorderLayout());
        JLabel logoText = Theme.label("SCM", 15, true, Color.WHITE);
        logoText.setHorizontalAlignment(SwingConstants.CENTER);
        logo.add(logoText, BorderLayout.CENTER);
        c.anchor = GridBagConstraints.CENTER;
        c.insets = new Insets(24, 0, 14, 0);
        side.add(logo, c);

        c.gridy++;
        JLabel title = Theme.label("Student Course Manager", 24, true,
                Theme.NAVY);
        title.setHorizontalAlignment(SwingConstants.CENTER);
        side.add(title, c);

        c.gridy++;
        c.insets = new Insets(4, 0, 16, 0);
        JLabel subtitle = Theme.label("Student Portal", 14, true,
                Theme.BLUE);
        subtitle.setHorizontalAlignment(SwingConstants.CENTER);
        side.add(subtitle, c);

        // tab row
        c.gridy++;
        c.insets = new Insets(0, 30, 0, 30);
        JPanel tabs = new JPanel(new FlowLayout(FlowLayout.CENTER, 0, 0));
        tabs.setOpaque(false);
        loginTab.setPreferredSize(new Dimension(150, 34));
        registerTab.setPreferredSize(new Dimension(150, 34));
        tabs.add(loginTab);
        tabs.add(registerTab);
        side.add(tabs, c);

        // the two forms
        c.gridy++;
        c.insets = new Insets(10, 40, 0, 40);
        formBox.setOpaque(false);
        formBox.add(loginForm(), "login");
        formBox.add(registerForm(), "register");
        side.add(formBox, c);

        // footer
        c.gridy++;
        c.insets = new Insets(18, 0, 20, 0);
        JLabel footer = Theme.label(
                "Lab Prototype   \u2022   No password required", 11,
                false, Theme.MUTED);
        footer.setHorizontalAlignment(SwingConstants.CENTER);
        side.add(footer, c);

        showTab("login");
        return side;
    }

    private JPanel loginForm() {
        JPanel form = new JPanel();
        form.setOpaque(false);
        form.setLayout(new GridBagLayout());
        GridBagConstraints c = new GridBagConstraints();
        c.gridx = 0;
        c.gridy = 0;
        c.fill = GridBagConstraints.HORIZONTAL;
        c.weightx = 1;
        c.insets = new Insets(0, 0, 6, 0);

        form.add(Theme.label("Student ID", 12, true, Theme.TEXT), c);
        c.gridy++;
        Theme.styleField(idField, "Enter student ID");
        idField.setText(SeedData.demoStudent().getId());
        form.add(idField, c);

        c.gridy++;
        c.insets = new Insets(8, 0, 12, 0);
        form.add(Theme.mutedLabel(
                "Enter your student ID to sign in. No password required.",
                11), c);

        c.gridy++;
        c.insets = new Insets(0, 0, 8, 0);
        JButton signIn = Theme.primaryButton(" \u21AA  Sign In");
        form.add(signIn, c);

        c.gridy++;
        c.insets = new Insets(6, 0, 0, 0);
        form.add(loginError, c);

        signIn.addActionListener(e -> signIn());
        return form;
    }

    private JPanel registerForm() {
        JPanel form = new JPanel();
        form.setOpaque(false);
        form.setLayout(new GridBagLayout());
        GridBagConstraints c = new GridBagConstraints();
        c.gridx = 0;
        c.gridy = 0;
        c.fill = GridBagConstraints.HORIZONTAL;
        c.weightx = 1;
        c.insets = new Insets(0, 0, 6, 0);

        JPanel heading = new JPanel(new FlowLayout(FlowLayout.LEFT, 0, 0));
        heading.setOpaque(false);
        JLabel number = Theme.label(" 2 ", 11, true, Color.WHITE);
        number.setOpaque(true);
        number.setBackground(Theme.TEAL);
        number.setHorizontalAlignment(SwingConstants.CENTER);
        heading.add(number);
        heading.add(Theme.label("  Create Account", 13, true, Theme.TEAL));
        form.add(heading, c);

        c.gridy++;
        c.insets = new Insets(10, 0, 6, 0);
        form.add(Theme.label("Student ID", 12, true, Theme.TEXT), c);
        c.gridy++;
        Theme.styleField(regIdField, "e.g. 30166082");
        form.add(regIdField, c);

        c.gridy++;
        c.insets = new Insets(8, 0, 6, 0);
        form.add(Theme.label("Full Name", 12, true, Theme.TEXT), c);
        c.gridy++;
        Theme.styleField(regNameField, "e.g. ABAH ANTHONY");
        form.add(regNameField, c);

        c.gridy++;
        c.insets = new Insets(8, 0, 6, 0);
        form.add(Theme.label("Programme of Study", 12, true, Theme.TEXT),
                c);
        c.gridy++;
        programBox.setFont(Theme.font(12));
        form.add(programBox, c);

        c.gridy++;
        c.insets = new Insets(8, 0, 6, 0);
        form.add(Theme.label("Level of Study", 12, true, Theme.TEXT), c);
        c.gridy++;
        levelBox.setFont(Theme.font(12));
        levelBox.setSelectedItem(200);
        form.add(levelBox, c);

        c.gridy++;
        c.insets = new Insets(12, 0, 8, 0);
        JButton create = Theme.tealButton("Create Account");
        form.add(create, c);

        c.gridy++;
        c.insets = new Insets(6, 0, 0, 0);
        form.add(registerError, c);

        create.addActionListener(e -> createAccount());
        return form;
    }

    private JButton tabButton(String text) {
        JButton button = new JButton(text);
        button.setOpaque(false);
        button.setContentAreaFilled(false);
        button.setBorderPainted(false);
        button.setFont(Theme.font(13, Font.BOLD));
        button.setCursor(new java.awt.Cursor(
                java.awt.Cursor.HAND_CURSOR));
        button.addActionListener(e -> showTab(
                "login".equals(text.toLowerCase()) ? "login" : "register"));
        return button;
    }

    /** Switches between the Login and Register tabs and
     *  marks the active one with a blue underline. */
    private void showTab(String key) {
        formCards.show(formBox, key);
        boolean loginActive = "login".equals(key);
        styleTab(loginTab, loginActive);
        styleTab(registerTab, !loginActive);
        loginError.setText("");
        registerError.setText("");
    }

    private void styleTab(JButton button, boolean active) {
        if (active) {
            button.setForeground(Theme.BLUE);
            button.setBorder(BorderFactory.createMatteBorder(0, 0, 3, 0,
                    Theme.BLUE));
        } else {
            button.setForeground(Theme.MUTED);
            button.setBorder(BorderFactory.createEmptyBorder(0, 0, 3, 0));
        }
    }

    // ------------------------------------------------------------------
    // actions
    // ------------------------------------------------------------------

    private void wireActions() {
        idField.addActionListener(e -> signIn());
        regIdField.addActionListener(e -> createAccount());
    }

    /** Sign in: validates the ID, then opens the portal. */
    private void signIn() {
        String id = idField.getText().trim();
        if (!id.matches("\\d{6,10}")) {
            loginError.setText("Student ID must be 6 to 10 digits.");
            return;
        }
        for (Student student : students) {
            if (student.sameIdAs(id)) {
                openPortal(student);
                return;
            }
        }
        loginError.setText("Student ID not found. Create an account "
                + "first using the Register tab.");
    }

    /** Register: validates every field, saves the student
     *  and opens the portal straight away. */
    private void createAccount() {
        String id = regIdField.getText().trim();
        String name = regNameField.getText().trim();

        if (!id.matches("\\d{6,10}")) {
            registerError.setText("Student ID must be 6 to 10 digits.");
            return;
        }
        if (name.length() < 3 || !name.matches("[A-Za-z .'-]+")) {
            registerError.setText("Enter your full name using letters only.");
            return;
        }
        for (Student student : students) {
            if (student.sameIdAs(id)) {
                registerError.setText("That Student ID is already registered.");
                return;
            }
        }

        int level = (Integer) levelBox.getSelectedItem();
        String program = (String) programBox.getSelectedItem();
        Student student = new Student(id, name, program, level);
        students.add(student);
        CourseFileIO.saveStudents(students);
        openPortal(student);
    }

    private void openPortal(Student student) {
        dispose();
        new DashboardFrame(student).setVisible(true);
    }

    // ------------------------------------------------------------------
    // right side picture
    // ------------------------------------------------------------------

    /** Classroom photograph with a soft white fade on the
     *  left so the form stays readable. The scaled picture
     *  is rendered once per resize, not on every repaint.
     *  Falls back to a plain gradient when the file is
     *  missing. */
    private static class ImagePanel extends JPanel {
        private final BufferedImage image;
        private BufferedImage rendered;
        private int renderedWidth = -1;
        private int renderedHeight = -1;

        ImagePanel() {
            BufferedImage loaded = null;
            String[] candidates = {
                "assets/login-bg.jpg",
                CourseFileIO.BASE.resolve("assets/login-bg.jpg").toString()
            };
            for (String path : candidates) {
                try {
                    File file = new File(path);
                    if (file.exists()) {
                        loaded = ImageIO.read(file);
                        break;
                    }
                } catch (Exception e) {
                    // try the next path
                }
            }
            image = loaded;
            setOpaque(true);
        }

        /** Scales the photo to cover the panel and paints
         *  the white fade on top. Only rebuilt when the
         *  panel changes size. */
        private void renderIfNeeded(int width, int height) {
            if (width <= 0 || height <= 0) {
                return;
            }
            if (rendered != null && width == renderedWidth
                    && height == renderedHeight) {
                return;
            }
            BufferedImage out = new BufferedImage(width, height,
                    BufferedImage.TYPE_INT_RGB);
            Graphics2D g2 = out.createGraphics();
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING,
                    RenderingHints.VALUE_ANTIALIAS_ON);
            g2.setRenderingHint(RenderingHints.KEY_INTERPOLATION,
                    RenderingHints.VALUE_INTERPOLATION_BILINEAR);

            if (image == null) {
                g2.setColor(new Color(219, 234, 254));
                g2.fillRect(0, 0, width, height);
            } else {
                double scale = Math.max(
                        (double) width / image.getWidth(),
                        (double) height / image.getHeight());
                int drawWidth = (int) (image.getWidth() * scale);
                int drawHeight = (int) (image.getHeight() * scale);
                g2.drawImage(image, (width - drawWidth) / 2,
                        (height - drawHeight) / 2, drawWidth, drawHeight,
                        null);
            }

            // white fade from the left edge
            int fadeWidth = width * 45 / 100;
            for (int x = 0; x < fadeWidth; x++) {
                float alpha = 1f - (float) x / (float) fadeWidth;
                g2.setColor(new Color(1f, 1f, 1f, alpha));
                g2.drawLine(x, 0, x, height);
            }
            g2.dispose();

            rendered = out;
            renderedWidth = width;
            renderedHeight = height;
        }

        @Override
        protected void paintComponent(Graphics g) {
            super.paintComponent(g);
            renderIfNeeded(getWidth(), getHeight());
            if (rendered != null) {
                g.drawImage(rendered, 0, 0, null);
            }
        }
    }
}
