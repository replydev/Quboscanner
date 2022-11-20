package me.replydev.qubo;

import com.formdev.flatlaf.FlatDarculaLaf;
import me.replydev.qubo.gui.MainWindow;

import javax.swing.*;

public class Main {
    public static void main(String[] args) {
        if (args.length == 0) {
            FlatDarculaLaf.install();
            JFrame.setDefaultLookAndFeelDecorated(true);
            Info.gui = true;
            new MainWindow();
        } else {
            Info.gui = false;
            CLI.init(args);
        }
    }
}