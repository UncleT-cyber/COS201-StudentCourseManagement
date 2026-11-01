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
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.util.LinkedHashMap;
import java.util.Map;

import javax.swing.BorderFactory;
import javax.swing.BoxLayout;
import javax.swing.JLabel;
import javax.swing.JPanel;

import java.awt.geom.Arc2D;

/**
 * Ring chart drawn by hand: credit units grouped by
 * category (Core, Elective, Other). No chart library is
 * used, the arcs are painted directly.
 */
public class DonutChart extends JPanel {

    private Map<String, Integer> data = new LinkedHashMap<>();
    private int total = 0;

    public DonutChart() {
        setOpaque(false);
        setPreferredSize(new Dimension(190, 190));
    }

    /** Replaces the values and repaints the chart. */
    public void setData(Map<String, Integer> values) {
        this.data = new LinkedHashMap<>(values);
        this.total = 0;
        for (Integer value : data.values()) {
            total += value;
        }
        repaint();
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        Graphics2D g2 = (Graphics2D) g.create();
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING,
                RenderingHints.VALUE_ANTIALIAS_ON);

        int size = Math.min(getWidth(), getHeight()) - 20;
        int x = (getWidth() - size) / 2;
        int y = (getHeight() - size) / 2;
        g2.setStroke(new BasicStroke(24f, BasicStroke.CAP_BUTT,
                BasicStroke.JOIN_ROUND));

        if (total == 0) {
            g2.setColor(new Color(229, 231, 235));
            g2.draw(new Arc2D.Double(x, y, size, size, 0, 360,
                    Arc2D.OPEN));
        } else {
            double angle = 90;
            int colourIndex = 0;
            double gap = (data.size() > 1) ? 3.0 : 0.0;
            for (Map.Entry<String, Integer> entry : data.entrySet()) {
                double extent = 360.0 * entry.getValue() / total;
                double drawn = Math.max(extent - gap, 1.0);
                g2.setColor(Theme.CHART[colourIndex % Theme.CHART.length]);
                g2.draw(new Arc2D.Double(x, y, size, size,
                        angle + gap / 2, drawn, Arc2D.OPEN));
                angle += extent;
                colourIndex++;
            }
        }

        // number in the middle of the ring
        String centre = String.valueOf(total);
        g2.setFont(new Font("SansSerif", Font.BOLD, 26));
        int centreWidth = g2.getFontMetrics().stringWidth(centre);
        g2.setColor(Theme.NAVY);
        g2.drawString(centre, (getWidth() - centreWidth) / 2,
                getHeight() / 2 - 2);

        g2.setFont(new Font("SansSerif", Font.PLAIN, 11));
        String caption = "Total Credits";
        int captionWidth = g2.getFontMetrics().stringWidth(caption);
        g2.setColor(Theme.MUTED);
        g2.drawString(caption, (getWidth() - captionWidth) / 2,
                getHeight() / 2 + 16);
        g2.dispose();
    }

    /** Builds the legend under or beside the chart:
     *  colour dot, category name, credits and percentage. */
    public static JPanel legend(Map<String, Integer> values) {
        JPanel panel = new JPanel();
        panel.setOpaque(false);
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));

        int sum = 0;
        for (Integer value : values.values()) {
            sum += value;
        }

        if (values.isEmpty()) {
            panel.add(Theme.mutedLabel("No courses enrolled yet.", 12));
            return panel;
        }

        int index = 0;
        for (Map.Entry<String, Integer> entry : values.entrySet()) {
            Color colour = Theme.CHART[index % Theme.CHART.length];
            int percent = (sum == 0) ? 0
                    : Math.round(100f * entry.getValue() / sum);
            String hex = String.format("#%06x",
                    colour.getRGB() & 0xffffff);
            // coloured dot, category name, then credits below
            JLabel line = new JLabel("<html><b><font color='" + hex
                    + "'>&#9679;</font> " + displayName(entry.getKey())
                    + "</b><br>&nbsp;&nbsp;&nbsp;"
                    + "<font color='#6B7280'>" + entry.getValue()
                    + " credits (" + percent + "%)</font></html>");
            line.setFont(Theme.font(12));
            line.setForeground(Theme.TEXT);
            line.setBorder(BorderFactory.createEmptyBorder(4, 2, 6, 2));
            panel.add(line);
            index++;
        }
        return panel;
    }

    /** Longer legend names to match the design mock-up. */
    private static String displayName(String category) {
        if ("Core".equals(category)) {
            return "Core Courses";
        }
        if ("Elective".equals(category)) {
            return "Elective Courses";
        }
        if ("Other".equals(category)) {
            return "Other Courses";
        }
        return category;
    }
}
