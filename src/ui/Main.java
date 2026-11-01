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

import javax.swing.SwingUtilities;
import javax.swing.UIManager;

import core.CourseFileIO;

/**
 * Program entry point.
 *
 * Prepares the text files, chooses the Nimbus look and
 * feel and opens the login window.
 *
 * Compile:  javac -d out $(find src -name "*.java")
 * Run:      java -cp out ui.Main
 *
 * Add --demo to open the portal straight away with the
 * seeded student account (handy for screenshots).
 */
public class Main {

    public static void main(String[] args) {
        try {
            UIManager.setLookAndFeel(
                    "javax.swing.plaf.nimbus.NimbusLookAndFeel");
        } catch (Exception ex) {
            // Nimbus is missing, the default look and feel is used
        }

        CourseFileIO.ensureFiles();

        boolean demoMode = args.length > 0 && "--demo".equals(args[0]);
        SwingUtilities.invokeLater(() -> {
            if (demoMode) {
                new DashboardFrame(core.SeedData.demoStudent())
                        .setVisible(true);
            } else {
                LoginFrame login = new LoginFrame();
                login.setVisible(true);
            }
        });
    }
}
