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
import java.util.Map;

import javax.swing.BoxLayout;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JProgressBar;

import core.CourseManager;

/**
 * Analytics screen: the category donut chart, the credit
 * load progress bar (out of 24 units) and a short summary
 * of the student's enrolment.
 */
public class AnalyticsPanel extends JPanel {

    private final DashboardFrame owner;
    private final CourseManager manager;

    private final DonutChart donut = new DonutChart();
    private final JPanel legendBox = new JPanel(new BorderLayout());
    private final JLabel creditsValue = Theme.label("0 / 24", 22, true,
            Theme.NAVY);
    private final JProgressBar creditBar = new JProgressBar(0,
            CourseManager.MAX_UNITS);
    private final JLabel remainingValue = Theme.mutedLabel("", 12);
    private final JLabel summaryValue = Theme.label("", 14, true,
            Theme.NAVY);
    private final JLabel balanceValue = Theme.messageLabel("", Theme.TEXT);
    private final JPanel balanceCard = new Theme.RoundedPanel(12);

    public AnalyticsPanel(DashboardFrame owner) {
        this.owner = owner;
        this.manager = owner.getManager();

        setLayout(new BorderLayout(14, 14));
        setOpaque(false);
        setBorder(new javax.swing.border.EmptyBorder(18, 18, 18, 18));

        JPanel top = new JPanel(new BorderLayout(0, 2));
        top.setOpaque(false);
        top.add(Theme.label("Analytics", 18, true, Theme.NAVY),
                BorderLayout.NORTH);
        top.add(Theme.mutedLabel(
                "How your credit load is distributed this semester", 12),
                BorderLayout.SOUTH);
        add(top, BorderLayout.NORTH);

        // chart card on the left
        JPanel chartCard = Theme.card(new BorderLayout(0, 10));
        chartCard.add(header("Credit Distribution",
                "Enrolled units grouped by category"),
                BorderLayout.NORTH);
        JPanel chartBody = new JPanel(new FlowLayout(FlowLayout.CENTER,
                24, 8));
        chartBody.setOpaque(false);
        chartBody.add(donut);
        legendBox.setOpaque(false);
        chartBody.add(legendBox);
        chartCard.add(chartBody, BorderLayout.CENTER);

        // summary cards on the right
        JPanel right = new JPanel();
        right.setOpaque(false);
        right.setLayout(new BoxLayout(right, BoxLayout.Y_AXIS));

        JPanel loadCard = Theme.card(new BorderLayout(0, 8));
        loadCard.add(header("Credit Load",
                "Maximum allowed this semester: 24 units"),
                BorderLayout.NORTH);
        JPanel loadBody = new JPanel();
        loadBody.setOpaque(false);
        loadBody.setLayout(new BoxLayout(loadBody, BoxLayout.Y_AXIS));
        loadBody.add(creditsValue);
        loadBody.add(Theme.gap(8));
        creditBar.setStringPainted(false);
        creditBar.setPreferredSize(new Dimension(240, 16));
        creditBar.setForeground(Theme.BLUE);
        creditBar.setBackground(Theme.BLUE_LIGHT);
        loadBody.add(creditBar);
        loadBody.add(Theme.gap(8));
        loadBody.add(remainingValue);
        loadCard.add(loadBody, BorderLayout.CENTER);
        right.add(loadCard);
        right.add(Theme.gap(14));

        JPanel summaryCard = Theme.card(new BorderLayout(0, 8));
        summaryCard.add(header("Enrolment Summary",
                "Courses and units this semester"), BorderLayout.NORTH);
        summaryCard.add(summaryValue, BorderLayout.CENTER);
        right.add(summaryCard);
        right.add(Theme.gap(14));

        balanceCard.setBackground(Theme.GREEN_BG);
        balanceCard.setLayout(new BorderLayout());
        balanceCard.setBorder(new javax.swing.border.EmptyBorder(12, 14,
                12, 14));
        balanceCard.add(balanceValue, BorderLayout.CENTER);
        right.add(balanceCard);

        JPanel centre = new JPanel(new BorderLayout(14, 0));
        centre.setOpaque(false);
        centre.add(chartCard, BorderLayout.CENTER);
        centre.add(right, BorderLayout.EAST);
        add(centre, BorderLayout.CENTER);

        manager.addChangeListener(this::refresh);
        refresh();
    }

    private JPanel header(String title, String subtitle) {
        JPanel head = new JPanel(new BorderLayout(0, 2));
        head.setOpaque(false);
        head.add(Theme.label(title, 14, true, Theme.NAVY),
                BorderLayout.NORTH);
        head.add(Theme.mutedLabel(subtitle, 11), BorderLayout.SOUTH);
        return head;
    }

    /** Refreshes every number on the screen. */
    public void refresh() {
        Map<String, Integer> byCategory = manager.creditsByCategory();
        donut.setData(byCategory);
        legendBox.removeAll();
        legendBox.add(DonutChart.legend(byCategory), BorderLayout.CENTER);
        legendBox.revalidate();
        legendBox.repaint();

        int total = manager.totalUnits();
        creditsValue.setText(total + " / " + CourseManager.MAX_UNITS);
        creditBar.setValue(total);
        remainingValue.setText(manager.remainingUnits()
                + " units remaining before the limit");

        summaryValue.setText(manager.enrolledCount()
                + " courses  \u2022  " + total + " units");

        if (total == 0) {
            balanceCard.setBackground(Theme.BLUE_LIGHT);
            balanceValue.setForeground(Theme.BLUE);
            balanceValue.setText("<html><b>Nothing enrolled yet</b><br>"
                    + "Add a course to see your analytics.</html>");
        } else if (byCategory.size() < 2) {
            balanceCard.setBackground(Theme.ORANGE_BG);
            balanceValue.setForeground(Theme.ORANGE);
            balanceValue.setText("<html><b>Single category</b><br>"
                    + "All your units are in one category. Consider "
                    + "adding a course from another category.</html>");
        } else {
            balanceCard.setBackground(Theme.GREEN_BG);
            balanceValue.setForeground(Theme.GREEN);
            balanceValue.setText("<html><b>Balanced</b><br>"
                    + "Your enrolment covers " + byCategory.size()
                    + " categories.</html>");
        }
    }
}
