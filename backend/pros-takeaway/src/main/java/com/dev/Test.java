package com.dev;

import javax.swing.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.*;
import java.util.Arrays;

public class Test {
    public static void main(String[] args) throws FileNotFoundException {
//        File f = new File("kay.txt");
//        long fl =  f.length();
//        System.out.println(fl);
//        System.out.println(f);
//        boolean created;
//        try {
//            created =  f.createNewFile();
//        } catch (IOException e) {
//            throw new RuntimeException(e);
//        }
//        System.out.println(created);
//        System.out.println(f);
//        FileReader fr = new FileReader("abc.txt");
//        FileReader fr2 = new FileReader(f);
        JFrame frame = new JFrame("Mouse Adapter Example");
        JButton button = new JButton("Click Me");
        // Add a MouseAdapter to handle mouse clicks on the button
        button.addMouseListener(new MouseAdapter() {
            public void mouseClicked(MouseEvent e) {
                System.out.println("Button was clicked!");
            }
        });
        frame.add(button);
        frame.setSize(300, 200);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setVisible(true);
    }

}