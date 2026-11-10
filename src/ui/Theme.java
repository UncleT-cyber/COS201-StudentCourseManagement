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
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.LayoutManager;
import java.awt.RenderingHints;
import java.awt.Rectangle;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

import javax.swing.BorderFactory;
import javax.swing.Icon;
import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollBar;
import javax.swing.JScrollPane;
import javax.swing.plaf.basic.BasicScrollBarUI;
import javax.swing.JTextField;
import javax.swing.SwingConstants;
import javax.swing.border.EmptyBorder;

/**
 * Colours, fonts and small reusable widgets shared by
 * every screen. Everything is kept here so the whole
 * portal has one consistent look.
 */
public class Theme {

    // main colours
    public static final Color BLUE = new Color(37, 99, 235);
    public static final Color BLUE_DARK = new Color(29, 78, 216);
    public static final Color BLUE_LIGHT = new Color(239, 246, 255);
    public static final Color TEAL = new Color(13, 148, 136);
    public static final Color TEAL_LIGHT = new Color(204, 251, 241);
    public static final Color NAVY = new Color(15, 42, 74);

    // surfaces
    public static final Color BG = new Color(246, 248, 252);
    public static final Color CARD = Color.WHITE;
    public static final Color BORDER = new Color(225, 232, 241);
    public static final Color SIDEBAR = Color.WHITE;

    // text
    public static final Color TEXT = new Color(31, 41, 55);
    public static final Color MUTED = new Color(107, 114, 128);

    // status colours
    public static final Color GREEN = new Color(22, 163, 74);
    public static final Color GREEN_BG = new Color(220, 252, 231);
    public static final Color ORANGE = new Color(234, 88, 12);
    public static final Color ORANGE_BG = new Color(255, 247, 237);
    public static final Color RED = new Color(220, 38, 38);
    public static final Color RED_BG = new Color(254, 226, 226);

    // elevation
    public static final Color SHADOW = new Color(30, 61, 99, 16);

    // chart palette
    public static final Color[] CHART = {
        new Color(37, 99, 235),
        new Color(13, 148, 136),
        new Color(245, 158, 11),
        new Color(139, 92, 246),
        new Color(107, 114, 128)
    };

    /** Where this project's source code is published. */
    public static final String REPO_URL =
            "https://github.com/UncleT-cyber/COS201-StudentCourseManagement";

    private Theme() {
    }

    public static Font font(int size, int style) {
        return new Font("SansSerif", style, size);
    }

    public static Font font(int size) {
        return font(size, Font.PLAIN);
    }

    public static JLabel label(String text, int size, boolean bold,
                               Color colour) {
        JLabel label = new JLabel(text);
        label.setFont(font(size, bold ? Font.BOLD : Font.PLAIN));
        label.setForeground(colour);
        return label;
    }

    public static JLabel mutedLabel(String text, int size) {
        return label(text, size, false, MUTED);
    }

    /** Consistent lightweight scrollbars for all Swing workspaces. */
    public static void styleScrollPane(JScrollPane pane) {
        pane.setBorder(null);
        pane.getViewport().setBackground(BG);
        pane.getVerticalScrollBar().setUnitIncrement(18);
        styleScrollBar(pane.getVerticalScrollBar());
        styleScrollBar(pane.getHorizontalScrollBar());
    }

    private static void styleScrollBar(JScrollBar bar) {
        bar.setUI(new BasicScrollBarUI() {
            @Override
            protected void configureScrollBarColors() {
                thumbColor = new Color(173, 187, 205);
                trackColor = BG;
            }

            @Override
            protected void paintTrack(Graphics g, JComponent c, Rectangle bounds) {
                // Keep the track visually quiet.
            }

            @Override
            protected void paintThumb(Graphics g, JComponent c, Rectangle bounds) {
                if (bounds.isEmpty() || !scrollbar.isEnabled()) {
                    return;
                }
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING,
                        RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setColor(isThumbRollover() ? new Color(126, 147, 175)
                        : new Color(173, 187, 205));
                int inset = 3;
                if (scrollbar.getOrientation() == JScrollBar.VERTICAL) {
                    g2.fillRoundRect(bounds.x + inset, bounds.y + 2,
                            Math.max(2, bounds.width - inset * 2),
                            Math.max(2, bounds.height - 4), 8, 8);
                } else {
                    g2.fillRoundRect(bounds.x + 2, bounds.y + inset,
                            Math.max(2, bounds.width - 4),
                            Math.max(2, bounds.height - inset * 2), 8, 8);
                }
                g2.dispose();
            }

            @Override
            protected JButton createDecreaseButton(int orientation) {
                return invisibleArrow();
            }

            @Override
            protected JButton createIncreaseButton(int orientation) {
                return invisibleArrow();
            }

            private JButton invisibleArrow() {
                JButton button = new JButton();
                button.setPreferredSize(new Dimension(0, 0));
                button.setMinimumSize(new Dimension(0, 0));
                button.setMaximumSize(new Dimension(0, 0));
                return button;
            }
        });
        bar.setPreferredSize(new Dimension(11, 11));
        bar.setOpaque(false);
    }

    /** Rounded white card with a light border and subtle shadow. */
    public static JPanel card() {
        return card(new BorderLayout(0, 10));
    }

    public static JPanel card(LayoutManager layout) {
        RoundedPanel panel = new RoundedPanel(18, true);
        panel.setBackground(CARD);
        panel.setLayout(layout);
        panel.setBorder(new EmptyBorder(18, 18, 18, 18));
        return panel;
    }

    /** Adds even spacing around a component. */
    public static JComponent pad(JComponent component, int top, int left,
                                 int bottom, int right) {
        if (component instanceof JPanel) {
            component.setBorder(new EmptyBorder(top, left, bottom, right));
            return component;
        }
        JPanel wrapper = new JPanel(new BorderLayout());
        wrapper.setOpaque(false);
        wrapper.setBorder(new EmptyBorder(top, left, bottom, right));
        wrapper.add(component);
        return wrapper;
    }

    /** Plain text input with a light border. */
    public static void styleField(JTextField field, String placeholder) {
        field.setFont(font(13));
        field.setForeground(TEXT);
        field.setBackground(Color.WHITE);
        field.setCaretColor(TEXT);
        field.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(202, 215, 232)),
                new EmptyBorder(10, 12, 10, 12)));
        field.setText("");
        field.putClientProperty("placeholder", placeholder);
    }

    /** Opens the project repository in the default browser. */
    public static void openRepo() {
        try {
            java.awt.Desktop.getDesktop().browse(
                    new java.net.URI(REPO_URL));
        } catch (Exception e) {
            // no browser available, leave it alone
        }
    }

    /** Clickable GitHub mark that opens the repository
     *  when pressed. */
    public static JLabel githubLink(int size) {
        JLabel link = new JLabel(new Icon() {
            @Override
            public void paintIcon(Component c, Graphics g,
                                  int x, int y) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.translate(x, y);
                g2.setColor(c.getForeground());
                g2.scale(size / 16.0, size / 16.0);
                g2.fill(GITHUB_MARK);
                g2.dispose();
            }

            @Override
            public int getIconWidth() {
                return size;
            }

            @Override
            public int getIconHeight() {
                return size;
            }
        });
        link.setForeground(TEXT);
        link.setCursor(java.awt.Cursor.getPredefinedCursor(
                java.awt.Cursor.HAND_CURSOR));
        link.setToolTipText("View the source code on GitHub");
        link.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                openRepo();
            }
        });
        return link;
    }

    /** Blue call to action button. */
    public static RoundedButton primaryButton(String text) {
        return new RoundedButton(text, BLUE, Color.WHITE, true);
    }

    /** White button with a blue outline. */
    public static RoundedButton outlineButton(String text) {
        return new RoundedButton(text, Color.WHITE, BLUE, false);
    }

    /** White button with a red outline, used for Drop. */
    public static RoundedButton dangerButton(String text) {
        return new RoundedButton(text, Color.WHITE, RED, false);
    }

    /** Soft pink pill button for mobile Action rows (Drop),
     *  matching the table's Drop pill: light pink fill with
     *  red text. */
    public static RoundedButton dropButton(String text) {
        RoundedButton btn = new RoundedButton(text, RED_BG, RED, true);
        btn.setFont(font(10, Font.BOLD));
        btn.setBorder(new EmptyBorder(4, 8, 4, 8));
        return btn;
    }

    /** Small rounded information pill. */
    public static Chip chip(String text, Color foreground, Color background) {
        return new Chip(text, foreground, background);
    }

    /** One line of coloured text used for form messages. */
    public static JLabel messageLabel(String html, Color colour) {
        JLabel label = new JLabel("<html>" + html + "</html>");
        label.setFont(font(12));
        label.setForeground(colour);
        return label;
    }

    /** Panel with rounded corners, paints itself. */
    public static class RoundedPanel extends JPanel {
        private final int arc;
        private final boolean shadow;

        public RoundedPanel(int arc) {
            this(arc, false);
        }

        public RoundedPanel(int arc, boolean shadow) {
            this.arc = arc;
            this.shadow = shadow;
            setOpaque(false);
        }

        @Override
        protected void paintComponent(Graphics g) {
            Graphics2D g2 = (Graphics2D) g.create();
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING,
                    RenderingHints.VALUE_ANTIALIAS_ON);
            // subtle drop shadow behind the card
            if (shadow) {
                g2.setColor(SHADOW);
                g2.fillRoundRect(2, 2, getWidth(), getHeight(), arc, arc);
            }
            // card face
            g2.setColor(getBackground());
            g2.fillRoundRect(0, 0, getWidth(), getHeight(), arc, arc);
            // border
            g2.setColor(BORDER);
            g2.setStroke(new BasicStroke(1f));
            g2.drawRoundRect(0, 0, getWidth() - 1, getHeight() - 1,
                    arc, arc);
            g2.dispose();
        }
    }

    /** Button that draws its own rounded shape so it looks
     *  the same on every platform. */
    public static class RoundedButton extends JButton {
        private final Color base;
        private final Color textColour;
        private final boolean filled;

        public RoundedButton(String text, Color base, Color textColour,
                             boolean filled) {
            super(text);
            this.base = base;
            this.textColour = textColour;
            this.filled = filled;
            setFocusPainted(false);
            setOpaque(false);
            setContentAreaFilled(false);
            setBorderPainted(false);
            setFont(font(13, Font.BOLD));
            setForeground(textColour);
            setCursor(new java.awt.Cursor(java.awt.Cursor.HAND_CURSOR));
            setBorder(new EmptyBorder(10, 18, 10, 18));

            addMouseListener(new MouseAdapter() {
                @Override
                public void mouseEntered(MouseEvent e) {
                    repaint();
                }

                @Override
                public void mouseExited(MouseEvent e) {
                    repaint();
                }
            });
        }

        @Override
        protected void paintComponent(Graphics g) {
            Graphics2D g2 = (Graphics2D) g.create();
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING,
                    RenderingHints.VALUE_ANTIALIAS_ON);
            Color fill = base;
            if (!isEnabled()) {
                fill = new Color(209, 213, 219);
            } else if (getModel().isPressed()) {
                fill = base.darker();
            } else if (getModel().isRollover()) {
                fill = (filled) ? base.brighter() : BLUE_LIGHT;
            }
            g2.setColor(fill);
            g2.fillRoundRect(0, 0, getWidth(), getHeight(), 10, 10);
            if (!filled) {
                g2.setColor(base);
                g2.setStroke(new BasicStroke(1.4f));
                g2.drawRoundRect(0, 0, getWidth() - 1, getHeight() - 1,
                        10, 10);
            }
            g2.dispose();
            super.paintComponent(g);
        }
    }

    /** Rounded pill label (Available, INFO, Drop ...). */
    public static class Chip extends JLabel {
        private Color pillBackground;

        public Chip(String text, Color foreground, Color background) {
            super(text);
            this.pillBackground = background;
            setOpaque(false);
            setFont(font(11, Font.BOLD));
            setForeground(foreground);
            setBorder(new EmptyBorder(4, 10, 4, 10));
            setHorizontalAlignment(JLabel.CENTER);
        }

        public void setColors(Color foreground, Color background) {
            setForeground(foreground);
            pillBackground = background;
            repaint();
        }

        @Override
        protected void paintComponent(Graphics g) {
            Graphics2D g2 = (Graphics2D) g.create();
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING,
                    RenderingHints.VALUE_ANTIALIAS_ON);
            g2.setColor(pillBackground);
            g2.fillRoundRect(0, 0, getWidth(), getHeight(), 16, 16);
            g2.dispose();
            super.paintComponent(g);
        }
    }

    /** Which picture a navigation button shows. */
    public enum NavKind {
        HOME, ENROLLMENT, COURSES, MATERIALS, ANALYTICS, SETTINGS,
        LOGOUT, COLLAPSE, EXPAND, GITHUB
    }

    /** Outline of the GitHub mark, drawn in code so the
     *  program needs no image files for it. Sized for a
     *  16 x 16 box. */
    private static final java.awt.geom.Path2D GITHUB_MARK =
            buildGithubMark();

    private static java.awt.geom.Path2D buildGithubMark() {
        java.awt.geom.Path2D.Double p =
                new java.awt.geom.Path2D.Double();
        p.moveTo(8, 0);
        p.curveTo(3.58, 0, 0, 3.58, 0, 8);
        p.curveTo(0, 11.53, 2.29, 14.53, 5.47, 15.59);
        p.curveTo(5.87, 15.67, 6, 15.42, 6, 15.21);
        p.lineTo(6, 13.72);
        p.curveTo(3.77, 14.2, 3.31, 12.77, 3.31, 12.77);
        p.curveTo(2.95, 11.85, 2.42, 11.6, 2.42, 11.6);
        p.curveTo(1.7, 11.11, 2.48, 11.12, 2.48, 11.12);
        p.curveTo(3.28, 11.17, 3.7, 11.94, 3.7, 11.94);
        p.curveTo(4.42, 13.16, 5.58, 12.81, 6.03, 12.61);
        p.curveTo(6.1, 12.09, 6.31, 11.74, 6.54, 11.54);
        p.curveTo(4.76, 11.33, 2.9, 10.65, 2.9, 7.58);
        p.curveTo(2.9, 6.71, 3.21, 6, 3.72, 5.44);
        p.curveTo(3.64, 5.23, 3.36, 4.42, 3.8, 3.32);
        p.curveTo(3.8, 3.32, 4.47, 3.1, 6, 4.14);
        p.curveTo(6.64, 3.96, 7.32, 3.87, 8, 3.87);
        p.curveTo(8.68, 3.87, 9.36, 3.96, 10, 4.14);
        p.curveTo(11.53, 3.1, 12.2, 3.32, 12.2, 3.32);
        p.curveTo(12.64, 4.42, 12.36, 5.23, 12.28, 5.44);
        p.curveTo(12.79, 6, 13.1, 6.71, 13.1, 7.58);
        p.curveTo(13.1, 10.66, 11.23, 11.33, 9.45, 11.53);
        p.curveTo(9.74, 11.78, 10, 12.26, 10, 13.01);
        p.lineTo(10, 15.21);
        p.curveTo(10, 15.42, 10.13, 15.67, 10.53, 15.59);
        p.curveTo(13.71, 14.53, 16, 11.53, 16, 8);
        p.curveTo(16, 3.58, 12.42, 0, 8, 0);
        p.closePath();
        return p;
    }

    /** Small line icon painted in code so nothing extra
     *  has to be installed to run the program. */
    public static class NavIcon implements Icon {
        private final NavKind kind;

        public NavIcon(NavKind kind) {
            this.kind = kind;
        }

        @Override
        public int getIconWidth() {
            return 16;
        }

        @Override
        public int getIconHeight() {
            return 16;
        }

        @Override
        public void paintIcon(Component c, Graphics g, int x, int y) {
            Graphics2D g2 = (Graphics2D) g.create();
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING,
                    RenderingHints.VALUE_ANTIALIAS_ON);
            g2.translate(x, y);
            g2.setColor(c.getForeground());
            g2.setStroke(new BasicStroke(1.7f, BasicStroke.CAP_ROUND,
                    BasicStroke.JOIN_ROUND));

            switch (kind) {
                case HOME:
                    java.awt.geom.Path2D house =
                            new java.awt.geom.Path2D.Double();
                    house.moveTo(1.5, 7.5);
                    house.lineTo(8, 2);
                    house.lineTo(14.5, 7.5);
                    house.moveTo(3.5, 6.5);
                    house.lineTo(3.5, 14);
                    house.lineTo(12.5, 14);
                    house.lineTo(12.5, 6.5);
                    g2.draw(house);
                    break;
                case ENROLLMENT:
                    g2.drawOval(2, 2, 12, 12);
                    g2.drawLine(8, 8, 11, 5);
                    break;
                case COURSES:
                    g2.drawRoundRect(2, 4, 12, 9, 2, 2);
                    g2.drawLine(2, 8, 14, 8);
                    g2.drawLine(7, 4, 7, 8);
                    break;
                case MATERIALS:
                    g2.drawRoundRect(3, 2, 10, 12, 2, 2);
                    g2.drawLine(6, 5, 10, 5);
                    g2.drawLine(6, 8, 10, 8);
                    g2.drawLine(6, 11, 9, 11);
                    break;
                case ANALYTICS:
                    g2.setColor(c.getForeground());
                    g2.fillRoundRect(3, 9, 3, 5, 1, 1);
                    g2.fillRoundRect(7, 4, 3, 10, 1, 1);
                    g2.fillRoundRect(11, 7, 3, 7, 1, 1);
                    break;
                case SETTINGS:
                    g2.drawOval(4, 4, 8, 8);
                    for (int i = 0; i < 8; i++) {
                        double angle = Math.PI / 4 * i;
                        int x1 = (int) Math.round(8 + 5.6 * Math.cos(angle));
                        int y1 = (int) Math.round(8 + 5.6 * Math.sin(angle));
                        int x2 = (int) Math.round(8 + 7.6 * Math.cos(angle));
                        int y2 = (int) Math.round(8 + 7.6 * Math.sin(angle));
                        g2.drawLine(x1, y1, x2, y2);
                    }
                    g2.fillOval(6, 6, 4, 4);
                    break;
                case COLLAPSE:
                    // bordered box with chevron pointing left:
                    // sidebar is open, click to collapse
                    g2.drawRoundRect(2, 1, 12, 14, 3, 3);
                    g2.drawLine(7, 2, 7, 14);
                    g2.drawLine(11, 6, 9, 8);
                    g2.drawLine(9, 8, 11, 10);
                    break;
                case EXPAND:
                    // bordered box with chevron pointing right:
                    // sidebar is collapsed, click to expand
                    g2.drawRoundRect(2, 1, 12, 14, 3, 3);
                    g2.drawLine(7, 2, 7, 14);
                    g2.drawLine(10, 6, 12, 8);
                    g2.drawLine(12, 8, 10, 10);
                    break;
                case GITHUB:
                    g2.fill(GITHUB_MARK);
                    break;
                case LOGOUT:
                default:
                    g2.drawLine(2, 8, 9, 8);
                    java.awt.geom.Path2D head =
                            new java.awt.geom.Path2D.Double();
                    head.moveTo(6, 5);
                    head.lineTo(9.5, 8);
                    head.lineTo(6, 11);
                    g2.draw(head);
                    g2.drawLine(13, 4, 13, 13);
                    break;
            }
            g2.dispose();
        }
    }

    /** Sidebar button: icon plus label, no border, the
     *  active row gets a rounded blue background. When the
     *  sidebar collapses the label disappears and only the
     *  icon stays, like the VS Code activity bar. */
    public static class NavButton extends JButton {
        private final String label;
        private boolean active;
        private Color normalColour = TEXT;

        public NavButton(String text, NavKind kind) {
            super(text, new NavIcon(kind));
            if (kind == NavKind.COLLAPSE || kind == NavKind.EXPAND) {
                this.label = kind == NavKind.COLLAPSE
                        ? "Collapse sidebar" : "Expand sidebar";
                setText("");
                setIcon(new NavIcon(kind));
            } else {
                this.label = text;
                setIcon(new NavIcon(kind));
            }
            setFont(font(13));
            setForeground(TEXT);
            setHorizontalAlignment(SwingConstants.LEFT);
            setIconTextGap(11);
            setFocusPainted(false);
            setOpaque(false);
            setContentAreaFilled(false);
            setBorderPainted(false);
            setBorder(new EmptyBorder(9, 12, 9, 8));
            setCursor(new java.awt.Cursor(java.awt.Cursor.HAND_CURSOR));
            addMouseListener(new MouseAdapter() {
                @Override
                public void mouseEntered(MouseEvent e) {
                    repaint();
                }

                @Override
                public void mouseExited(MouseEvent e) {
                    repaint();
                }
            });
        }

        /** Changes the painted icon, used to flip between the
         *  collapse and expand arrows when the sidebar toggles. */
        public void setKind(NavKind kind) {
            if (kind == NavKind.COLLAPSE || kind == NavKind.EXPAND) {
                setIcon(new NavIcon(kind));
                setText("");
            } else {
                setIcon(new NavIcon(kind));
            }
        }

        /** Hides or shows the text label when the sidebar
         *  is collapsed or expanded. */
        public void setCollapsed(boolean collapsed) {
            if (collapsed) {
                setText("");
                setToolTipText(label);
                setHorizontalAlignment(SwingConstants.CENTER);
                setBorder(new EmptyBorder(9, 0, 9, 0));
                setPreferredSize(new Dimension(54, 38));
                setMaximumSize(new Dimension(54, 38));
            } else {
                setText(label);
                setToolTipText(null);
                setHorizontalAlignment(SwingConstants.LEFT);
                setBorder(new EmptyBorder(9, 12, 9, 8));
                setPreferredSize(new Dimension(180, 38));
                setMaximumSize(new Dimension(Short.MAX_VALUE, 40));
            }
        }

        public void setActive(boolean value) {
            active = value;
            if (value) {
                setForeground(BLUE);
                setFont(font(13, Font.BOLD));
            } else {
                setForeground(normalColour);
                setFont(font(13));
            }
            repaint();
        }

        /** Used for the red Logout row. */
        public void setNormalColour(Color colour) {
            normalColour = colour;
            if (!active) {
                setForeground(colour);
            }
        }

        @Override
        protected void paintComponent(Graphics g) {
            if (active || getModel().isRollover()) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING,
                        RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setColor(active ? BLUE_LIGHT : new Color(241, 245, 249));
                g2.fillRoundRect(0, 0, getWidth(), getHeight(), 10, 10);
                g2.dispose();
            }
            super.paintComponent(g);
        }
    }

    /** Helper for fixed-height rows of cards. */
    public static JPanel row(JPanel... cards) {
        JPanel row = new JPanel(new java.awt.GridLayout(
                1, cards.length, 14, 0));
        row.setOpaque(false);
        for (JPanel card : cards) {
            row.add(card);
        }
        return row;
    }

    /** Helper for vertical spacing between sections. */
    public static JComponent gap(int height) {
        JPanel gap = new JPanel();
        gap.setOpaque(false);
        gap.setPreferredSize(new Dimension(1, height));
        gap.setMaximumSize(new Dimension(Short.MAX_VALUE, height));
        return gap;
    }
}
