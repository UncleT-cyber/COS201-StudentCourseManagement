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
import java.awt.Component;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Font;

import javax.swing.DefaultListModel;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.ListCellRenderer;

import core.ActivityLog;

/**
 * Shows the activity log. New lines appear at the bottom
 * as they happen because the panel listens to the log.
 */
public class ActivityLogPanel extends JPanel {

    private final DefaultListModel<ActivityLog.Entry> model =
            new DefaultListModel<>();
    private final JList<ActivityLog.Entry> list = new JList<>(model);

    public ActivityLogPanel(ActivityLog log) {
        setLayout(new BorderLayout());
        setOpaque(false);

        list.setCellRenderer(new EntryRenderer());
        list.setVisibleRowCount(6);
        list.setBackground(Color.WHITE);
        list.setSelectionBackground(Color.WHITE);
        list.setFixedCellHeight(26);

        JScrollPane scroll = new JScrollPane(list);
        scroll.setBorder(null);
        scroll.getViewport().setBackground(Color.WHITE);
        add(scroll, BorderLayout.CENTER);

        // copy the lines that already exist, then follow
        // whatever is added after this.
        for (ActivityLog.Entry entry : log.getEntries()) {
            model.addElement(entry);
        }
        log.addListener(entry -> {
            model.addElement(entry);
            if (!model.isEmpty()) {
                list.ensureIndexIsVisible(model.size() - 1);
            }
        });
    }

    /** Row layout: time, INFO/WARN badge, message. */
    private static class EntryRenderer
            implements ListCellRenderer<ActivityLog.Entry> {

        private final Theme.Chip badge = Theme.chip("INFO",
                Theme.BLUE, Theme.BLUE_LIGHT);
        private final javax.swing.JLabel time =
                Theme.label("00:00:00", 11, false, Theme.MUTED);
        private final javax.swing.JLabel message =
                Theme.label("", 12, false, Theme.TEXT);
        private final JPanel row = new JPanel(new FlowLayout(
                FlowLayout.LEFT, 6, 2));

        EntryRenderer() {
            time.setPreferredSize(new Dimension(62, 16));
            time.setFont(new Font("Monospaced", Font.PLAIN, 11));
            row.setOpaque(true);
            row.add(time);
            row.add(badge);
            row.add(message);
        }

        @Override
        public Component getListCellRendererComponent(
                JList<? extends ActivityLog.Entry> jList,
                ActivityLog.Entry entry, int index, boolean isSelected,
                boolean cellHasFocus) {

            time.setText(entry.time);
            message.setText(entry.message);
            if ("WARN".equals(entry.level)) {
                badge.setText("WARN");
                badge.setColors(Theme.ORANGE, Theme.ORANGE_BG);
            } else {
                badge.setText("INFO");
                badge.setColors(Theme.BLUE, Theme.BLUE_LIGHT);
            }

            row.setBackground(isSelected ? Theme.BLUE_LIGHT : Color.WHITE);
            return row;
        }
    }
}
