package qubo;

import com.formdev.flatlaf.FlatDarkLaf;

import qubo.gui.MainWindow;

import javax.swing.*;

public class Main {
    public static void main(String[] args) {

        if(args.length == 0){
            FlatDarkLaf.install();
            JFrame.setDefaultLookAndFeelDecorated(true);
            Info.gui = true;
            new MainWindow();
        }
        else {
            Info.gui = false;
            CLI.init(args);
        }
    }
}