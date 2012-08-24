package de.lathspell.dtx2gp.frontend;

import java.awt.BorderLayout;
import java.awt.Container;

import javax.swing.*;

public class WebStartApp extends JFrame {

    public static void main(String args[]) {
        Gui g = new Gui();
        g.init();
        g.start();

        JFrame frame = new JFrame("Web Start Test");
        frame.setSize(300, 300);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setResizable(false);
        Container cp = frame.getContentPane();
        cp.add(g, BorderLayout.CENTER);
        frame.setVisible(true);
    }
}
